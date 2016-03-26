package com.ibm.crawler;

import java.util.EventObject;

/**
 * A connection state change event object.
 */
public class ConnectionEvent extends EventObject {

  /**
   * the new connection state
   */
  private int state;
  
  /**
   * Construct a connection event.
   * @param connection the source of the event
   * @param state the new connection state
   */
  public ConnectionEvent (Connection connection, int state) {
    super(connection);
    this.state = state;
  }

  /**
   * Gets the new connection state.
   */
  public int getState () {
    return state;
  }
}
