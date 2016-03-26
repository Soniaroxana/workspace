package com.ibm.crawler;

import java.util.Vector;

/**
 * The ConnectionQueue class maintains a queue of connections. <br>
 * Methods are provided for bounded enquing and dequing of connections. <br>
 * The queue has a predefined capacity, which can only be exceeded using the special forceEnqueue method.
 */
public class ConnectionQueue {

  /**
   * queue implementation
   */
  private Vector queue;

  /**
   * queue capacity
   */
  private int    capacity; 
  
  /**
   * creates a connection queue with a predefined capacity.
   * @param capacity the queue's capacity
   */
  public ConnectionQueue (int capacity) {
    if (capacity <= 0)
      capacity = 1;
    this.queue = new Vector(capacity);
    this.capacity = capacity;
  }

  /**
   * Dequeues a connection from the queue. <br>
   * If the queue is empty, the calling thread waits on this object until the queue is non-empty 
   * (when it will be notified by the <code>enqueue<code> method).
   * @return the connection at the beginning of the queue
   * @exception InterruptedException if another thread interrupted this one during wait.
   * @see #enqueue(com.ibm.crawler.Connection)
   */
  public synchronized Connection dequeue () throws InterruptedException {
    System.out.println("in dequeue");
    while (queue.isEmpty())
      wait();
    Connection connection = (Connection)queue.elementAt(0);
    queue.removeElementAt(0);
    
    /* notify only when needed */
    if (queue.size() == capacity - 1)
      notify();

    return connection;
  }

  /**
   * Enqueues a connection to the queue. <br>
   * If the queue is currently at full or over capacity, the calling thread waits on this object until
   * there is room for the new connection (when it will be notified by the <code>dequeue<code> method).
   * @param connection the connection to be enqueued
   * @exception InterruptedException if another thread interrupted this one during wait.
   * @see #dequeue()
   */
  public synchronized void enqueue (Connection connection) throws InterruptedException {
    System.out.println("in enqueue ");
    while (queue.size() > capacity)
      wait();
    queue.addElement(connection);

    /* notify only when needed */
    if (queue.size() == 1)
      notify();
  }

  /**
   * Enqueues a connection to the queue. <br>
   * Like <code>enqueue</code> except the connection is enqueued even if queue is at full or over capacity.
   * @param connection the connection to be enqueued
   * @see #enqueue(com.ibm.crawler.Connection)
   */
  public synchronized void forceEnqueue(Connection connection) {
    System.out.println("in forceEnqueue");
    queue.addElement(connection);

    /* notify only when needed */
    if (queue.size() == 1)
      notify();
  }

}
