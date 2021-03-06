package com.ibm.crawler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ConnectionManager class is responsible for:
 * <ul>
 * <li>Creating the worker manager and its queue
 * <li>Feeding new connections (from the connectionFactory) into the worker manager's queue
 * <li>Limiting the runnning time of the worker manager
 * </ul>
 */
public class ConnectionManager implements Runnable, Timed, ConnectionListener {

  /**
   * a factory object from which new connections are obtained
   */
  private ConnectionFactory connectionFactory;

  /**
   * reference to the manager of worker threads
   */
  private WorkerManager     workerManager;

  /**
   * time limit on the worker manager
   */
  private long              timeLimit;

  /**
   * a queue to which new connections (waiting to be dispatched for worker threads) are inserted
   */
  private ConnectionQueue   waitingConnections;

  /**
   * the thread running this object
   */
  private Thread            managerThread = null;

  /**
   * loop continuation flag
   */
  private volatile boolean  cont = true;

  /**
   * counts the number of live connections (waiting in the queue+currently being processed)
   */
  private volatile int  numberOfLiveConnections = 0;
  //private AtomicInteger numberOfLiveConnections = new AtomicInteger(0);

  /**
   * Constructs the connection manager. <br>
   * initializes and runs the worker manager and the waiting connections queue.
   * @param crawler the Crawler object (containing crawler parameters needed by this object).
   */
  public ConnectionManager (Crawler crawler) {
    Debug.print("instantiating connection manager");
    this.connectionFactory = crawler.getConnectionFactory();
    int numberOfWorkers = crawler.getNumberOfThreads();
    this.waitingConnections = 
      crawler.obeyRobotsExclusionProtocol() ?
      new FilteredConnectionQueue(numberOfWorkers) :
      new ConnectionQueue(numberOfWorkers);
    this.workerManager = new WorkerManager(waitingConnections, numberOfWorkers);
    this.timeLimit = crawler.getGlobalTimeLimit();
    new Thread(workerManager).start();
  }

  /**
   * Runs the connection manager's thread. <br>
   * Feeds the worker manager with new connections from the connectionFactory, 
   * until the time limit has expired or until the worker manager goes idle and there are no more new connections.
   */
  public void run () {

    Debug.print("running connection manager");

    /* save a reference to this thread */
    managerThread = Thread.currentThread();

    /* set a timer on this thread */
    Timer timer = new Timer(this, timeLimit);

    mainloop: while(cont) {
      
      try {
        /* get a new connection from connectionFactory (may block if connection factory is currently empty) */
        Connection connection = getNewConnection();
        /* a null return value means that:
           1. there are no more live connections in the system, and 
           2. the connection factory produced no new connection */
        if (connection == null) 
          break mainloop;

        /* listen to connection */
        connection.addConnectionListener(this);

        /* make this connection alive */
        connection.setState(Connection.ALIVE);

        /* add connection to waiting queue (may block if queue does not accept new connections because it's full) */
        waitingConnections.enqueue(connection); 

      } catch (InterruptedException e) {
        /* wait within the enqueue or getNewConnection methods may be interrupted by the timeExpired method */
        Debug.print("connection manager main loop interrupted");
        break mainloop;
      }
    }
    
    /* shutdown the timer */
    timer.finish();

    /* shutdown the worker manager */
    workerManager.finish();    
    
  }  

  /**
   * Handles managed connections state changes. <br>
   * When a connection goes alive, the number of live connections is increased by one. <br>
   * when a live connection ends or is filtered out, th number of live connections is decreased by one, <br>
   * and the ConnectionManager object is notified, so that a thread waiting inside getNewConnection could
   * check if the connection factory can now provide a new connection.
   * @param event the event object containing the state change insformation
   * @see com.ibm.crawler.ConnectionListener
   */
  public synchronized void connectionStateChanged(ConnectionEvent event) {
    System.out.println("in connectionStateChanged");
    switch (event.getState()) {
    case Connection.ALIVE:
      Debug.print(/*event.getSource()+*/": alive");
      numberOfLiveConnections++;
      //numberOfLiveConnections.getAndIncrement();
      break;
    case Connection.ENDED:
      Debug.print(/*event.getSource()+*/": dead");
      numberOfLiveConnections--;
      //numberOfLiveConnections.getAndDecrement();
      notify();
      break;
    case Connection.FILTERED:
      Debug.print(/*event.getSource()+*/": filtered out");      
      numberOfLiveConnections--;
      //numberOfLiveConnections.getAndDecrement();
      if (numberOfLiveConnections == 0)
      //if (numberOfLiveConnections.get() == 0)
        notify();
      break;
    }
      
  }

  /**
   * checks whether there are currently any live connections.
   */
  public synchronized boolean hasLiveConnections() {
    System.out.println("in hasLiveConnections");
    return (numberOfLiveConnections != 0);
    //return(numberOfLiveConnections.get() != 0);
  }

  /**
   * Called by the thread's timer when the time limit has expired. <br>
   * Sets the loop continue flag to false.
   */
  public synchronized void timeExpired() {
    System.out.println("eitan: executing timedExpired");
    cont = false;
    Debug.print("finishing connection manager");
    if (managerThread != null)
      managerThread.interrupt();
  }

  /**
   * Tries to get a new connection from the connection factory. <br>
   * If the connection factory produced no new connection, then:
   * <ul>
   * <li> if there are currently live connections, this method blocks until it is notified that one of the connections has ended, 
   *      at which time the factory may have new connections to produce.
   * <li> if there are no live connections, it means that no new connections will ever be produced, and this method returns null.
   */
  private synchronized Connection getNewConnection() throws InterruptedException {
    System.out.println("in getNewConnection");
    Connection connection = null;
    waitloop: while ((connection = connectionFactory.getNewConnection()) == null) {
      if (this.hasLiveConnections()) {
        Debug.print("connection manager has live connections. waiting");
        wait();
      }
      else {
        Debug.print("connection manager has no live connections. breaking");
        break waitloop;
      }
    }
    return connection;
  }
  
}
