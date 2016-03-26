package com.ibm.crawler;

/**
 * The Worker class is responsible for running and controlling connections. <br>
 * A worker runs in its own thread and waits for connections that need to be performed. <br>
 * A WorkerPool object owning a reference to a Worker object can assign a connection to it,
 * and also shut it down in an orderly manner.
 */
public class Worker implements Runnable {


  /**
   * the worker pool owning this worker
   */
  private WorkerPool       pool;

  /**
   * the connection that this worker currently performs
   */
  private Connection       connection = null;

  /**
   * the thread running this object
   */
  private Thread           workerThread = null;

  /**
   * loop continuation flag
   */
  private volatile boolean cont = true;

  /**
   * Creates a worker owned to a specified worker pool.
   */
  public Worker (WorkerPool pool) {
    this.pool = pool;
  }

  /**
   * Runs the worker thread. <br>
   * Initially, the worker is in an idle mode, and waits until a connection is assigned to it. <br>
   * When a connection has been assigned to it, the worker invokes its <code>perform</code> method. <br>
   * When the connection is done, the worker returns itself to the worker pool and goes back to wait
   * for a new connection.
   */
  public void run () {

    /* save a reference to this thread */
    workerThread = Thread.currentThread();

    mainloop: while (cont) {

      try {
        /* wait for a connection */
        waitForConnection();
        
        /* perform the connection */
        connection.perform();

        /* finish connection */
        connection.setState(Connection.ENDED);

        /* free this worker for next available connection */
        connection = null;
        pool.returnWorker(this);
      } catch (InterruptedException e) {
        /* wait within the waitForConnection method may be interrupted by the finish method */
        Debug.print("worker main loop interrupted");
        break mainloop;
      }
        
    }
    
  }

  /**
   * Assigns a connection to this worker and signals it to perform it.
   * @param connection the connection to be performed.
   */
  public synchronized void assignConnection(Connection connection) {
    System.out.println("in  assignConnection");
    this.connection = connection;
    notify();
  }

  /**
   * Waits for a connection that needs to be performed.
   * @exception InterruptedException if another thread interrupted this one during wait.
   */
  private synchronized void waitForConnection() throws InterruptedException {
    System.out.println("in  waitForConnection");
    while(connection == null)
      wait(); 
  }

  /**
   * Shuts down this worker. <br>
   * If this worker is currently running a conneection, it's notified that it should finish as soon as possible.
   */
  public void finish() {
    cont = false;
    Debug.print("worker finishing");
    // here is the problem - data race
    //try{
      if (connection != null){
	try {
	  Thread.sleep(500);
	} catch (InterruptedException e) {	}

	// problematic access
        connection.setStopFlag();
      }
      if (workerThread != null) workerThread.interrupt();
    //} catch (NullPointerException e){
      // react on error manifestation
      //cz.vutbr.fit.coverage.ExceptionCover.reportException(e);
      // rethrow the exception
      //throw e;
    //}
  }

}
