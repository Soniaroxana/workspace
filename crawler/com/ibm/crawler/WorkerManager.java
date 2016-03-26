package com.ibm.crawler;


/**
 * The WorkerManager maintains a pool of worker objects, each running in a separate thread, 
 * and a queue of connections waiting to be processed in one of the worker threads. <br>
 * The WorkerManager runs in a separate thread. It waits for an idle worker, then for a waiting connection. <br>
 * When it has both, it assign the connection to the worker, and so on.
 */
public class WorkerManager implements Runnable {

  /** 
   * a queue for maintining connections waiting for worker threads
   */
  private ConnectionQueue   waitingConnections;

  /**
   * a pool of workers from which free workers can be drafted for waiting connections
   */
  private WorkerPool        workers;

  /**
   * the thread running this object
   */
  private Thread            managerThread = null;

  /**
   * loop continuation flag
   */
  private volatile boolean cont = true;

  /**
   * Constructs a WorkerManager. <br>
   * Creates a new pool of worker objects and sets the waiting connections queue.
   * @param waitingConnections a queue of connections waiting to be served by the worker manager
   * @param numberOfWorkers the number of workres in the worker pool
   */
  public WorkerManager (ConnectionQueue waitingConnections, int numberOfWorkers) {
    this.workers = new WorkerPool(numberOfWorkers);
    this.waitingConnections = waitingConnections;
  }

  /**
   * Runs the worker manager's thread. <br>
   * The thread runs in a loop. in each cycle of the loop, the worker manager:
   * <ul>
   * <li>waits for a free worker
   * <li>waits for a connection that needs a worker
   * <li>assigns the connection to the worker
   * </ul>
   * The worker manager runs indefinitely, until it is interrupted by calling its <code>finish</code> method. <br>
   * When the worker manager finishes, it calls the worker pool's <code>finish</code> method, 
   * to notify workers that they should finish.
   */
  public void run () {

    /* save a reference to this thread */
    managerThread = Thread.currentThread();

    mainloop: while (cont) {
      
      try {
        /* first wait for a free worker (may block if there are no available workers) */
        Worker worker = workers.grabWorker();

        /* then wait for a connection (may block if there are no waiting connections) */
        Connection connection = waitingConnections.dequeue();

        /* assign the connection to the free worker */
        worker.assignConnection(connection);
      } catch (InterruptedException e) {
        /* wait within the grabWorker or dequeue methods may be interrupted by the finish method */
        Debug.print("worker manager main loop interrupted");
        break mainloop;
      }
      
    } // end while (cont)
    
    /* release the workers pool */
    workers.finish();

  }

  /**
   * Tells the worker manager to finish waiting on the connections queue and to release its workers.
   */
  public void finish() {
    cont = false;
    Debug.print("worker manager finishing");
    if (managerThread != null)
      managerThread.interrupt();
  }


}
