package com.ibm.crawler;

/**
 * The Timer class times objects implementing the Timed interface. <br>
 * The object runs in a thread of its own, and sleeps until a predefined amount of time has passed. <br>
 * when it wakes up, it calls the timed object's <code>timeExpired</code> method. <br>
 * It's possible to stop the timer before the time limit has passed, by calling the timer's <code>finish</code> method.
 * @see com.ibm.crawler.Timed
 */
public class Timer implements Runnable{

  /**
   * the object that this timer times
   */
  private Timed  timedObject;

  /**
   * the time limit set on the timed object
   */
  private long   timeLimit;

  /**
   * the thread running this object
   */
  private Thread timerThread = null;

  /**
   * Creates and runs a timer on the specified object with the specified time limit.
   * @param timedObject the object to time
   * @param timeLimit the time limit, in milliseconds
   */
  public Timer (Timed timedObject, long timeLimit) {
    this.timedObject = timedObject;
    this.timeLimit = timeLimit;
    new Thread(this).start();
  }

  /**
   * Runs this timer. <br>
   * When the time limit has passed, the object's <code>timeExpired</code> method is called.
   */
  public void run() {
    
    timerThread = Thread.currentThread();

    try {
      Debug.print("going to sleep for "+timeLimit+" millis");
      Thread.sleep(timeLimit);
    } catch (InterruptedException e) {
      Debug.print("timer interrupted");
      return;
    }

    Debug.print("time has expired");
    System.out.println("After the call to time has expired in run of Timer");
    timedObject.timeExpired();
  }

  /**
   * Stops this timer.
   */
  public void finish() {
    if (timerThread != null)
      timerThread.interrupt();
  }

}
