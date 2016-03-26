package com.ibm.crawler;

import java.util.Stack;

/**
 * The WorkerPool class maintains a predefined number of worker objects, each running in a different thread. <br>
 * the pool consists of an array which keeps references to all workers in the pool and a stack from which avaiable
 * workers are taken and to which idle workers are returned.
 */
public class WorkerPool {

  /**
   * array of references to this pool's worker objects 
   */
  Worker[] workers;

  /**
   * a stack from which wokers are taken and returned
   */
  Stack    stack;

  /**
   * Creates a worker pool. <br>
   * The workers are created and run within separate threads. <br>
   * After creation, all workers are waiting for work.
   * @param numberOfWorkers number of workers in the pool
   */
  public WorkerPool (int numberOfWorkers) {
    workers = new Worker[numberOfWorkers];
    stack = new Stack();
    for (int i=0;i<numberOfWorkers;i++) {
      Worker worker = new Worker(this);
      workers[i] = worker;
      stack.push(worker);
      new Thread(worker).start();
    }
  }
    
  /**
   * Retrieves an idle worker waiting for work. <br>
   * This method blocks until a idle worker is available.
   * @return the idle worker object
   * @exception InterruptedException if another thread interrupted this one during wait.
   */
  public synchronized Worker grabWorker() throws InterruptedException {
    System.out.println("in grabWorker");
    while (stack.empty())
      wait();
    return (Worker)stack.pop();
  }
  
  /**
   * Returns an idle worker to the worker pool. <br>
   * Also notifies the worker pool thread that a new idle worker is available.
   * @param worker idle worker to return to the pool
   */
  public synchronized void returnWorker (Worker worker) {
    System.out.println("in returnWorker");
    stack.push(worker);
    
    /* notify only when needed */
    if (stack.size() == 1)
      notify();
  }
  
  /**
   * Tells the worker pool to finish. <br>
   * The <code>Worker finish</code> method is invoked on every worker in the pool (idle or busy).
   */
  public void finish() {
    Debug.print("worker pool finishing");
    for (int i=0;i<workers.length;i++)
      workers[i].finish();
  }


}
