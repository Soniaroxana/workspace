package com.ibm.crawler;

/**
 * this interface needs to be implemented by classes which need to be timed by Timer objects
 * @see com.ibm.crawler.Timer
 */
public interface Timed {

  /**
   * called by the Timer object when the time limit has expired
   */
  public void timeExpired();
}
