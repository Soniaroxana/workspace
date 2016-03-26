package com.ibm.crawler;

import java.util.EventListener;

/**
 * a listener interface for connection state change events.
 * @see com.ibm.crawler.Connection
 */
public interface ConnectionListener extends EventListener {
  
  /**
   * Called when a connection has changed its state.
   * @param event the event object associated with the state change.
   */
  public void connectionStateChanged (ConnectionEvent event);
}
