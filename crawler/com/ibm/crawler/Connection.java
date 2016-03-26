package com.ibm.crawler;

import java.util.Enumeration;
import java.util.Vector;

/**
 * The Connection class encapsulates an action which needs to be performed within a worker thread from beginning to end. <br>
 * The <code>perform</code> method should be implemented to perform whatever actions the connection needs to perform. <br>
 * Implementors of subclasses are strongly encouraged to place calls to <code>shouldStop</code> within their implementation
 * of <code>perform</code> in order to check whether the worker thread running the connection has been asked to stop.
 * A mechanizm is also provided to let listeners be informed of state changes within the connection.
 * @see com.ibm.crawler.ConnectionListener
 */
public abstract class Connection {

  /**
   * flag indicating if this connection should finish as soon as possible
   */
  private volatile boolean stopFlag = false;

  /**
   * listeners to this connection
   */
  private Vector listeners = new Vector();

  /**
   * connection state
   */
  private int state;

  /**
   * a connection state type indicating that the connection has been equeued or is currently running
   */
  public final static int ALIVE =    1;
  /**
   * a connection state type indicating that the connection has finished running
   */
  public final static int ENDED =    2;
  /**
   * a connection state type indicating that the connection has been filtered out and will not be run
   */
  public final static int FILTERED = 3;

  /**
   * Performs all the actions needed to be performed by this object.
   */
  public abstract void perform ();

  /**
   * Checks if this connection should be stopped. <br>
   * Should be called by <code>perform</code> in user defined checkpoints.
   * @return true if this connection should finish as soon as possible, false if the connection can continue normally.
   */
  public boolean shouldStop () {
    return stopFlag;
  }

  /**
   * Sets the stop flag for this connection.
   */
  public void setStopFlag () {
    Debug.print("connection advised to stop");
    stopFlag = true;
  }

  /**
   * Adds a listener to this connection.
   */
  public void addConnectionListener (ConnectionListener listener) {
    System.out.println("Conncection#addConnectionListener");
    listeners.addElement(listener);
  }

  /**
   * Removes a listener from this connection.
   */
  public void removeConnectionListener (ConnectionListener listener) {
    System.out.println("Conncection#removeConnectionListener");
    listeners.removeElement(listener);
  }

  /**
   * Changes the state of this connection. <br>
   * This method is made public (and not protected) so that other objects could change the state as well.
   * A <code>ConnectionEvent</code> containing the new state is sent to all listeners.
   * @param state the new connection state.
   */
  public synchronized void setState (int state) {
    System.out.println("Conncection#setState");
    this.state = state;
    for (Enumeration e = listeners.elements();e.hasMoreElements();) {
      ConnectionListener listener = (ConnectionListener)e.nextElement();
      listener.connectionStateChanged(new ConnectionEvent(this, state));
    }
  }

}
