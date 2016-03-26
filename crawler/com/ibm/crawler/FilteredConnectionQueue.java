package com.ibm.crawler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class extends the <code>ConnectionQueue</code> class, to handle queing of connections 
 * which can be filtered-out according to site-specific robot exclusion rules. <br>
 * This object consists of:
 * <ul>
 * <li>a set of lists containing connections for which robot policy has not yet been determined, (one list per site), 
 * <li>a set of robot filter objects, for all sites for which robot policy has already been determined.
 * </ul>
 * Enqueued connections are checked against the appropriate filter or added to the appropriate list (if the filter does not yet exist).
 * For each new site, a <code>RobotFilterConnection</code> is dispatched to obtain the robot policy for that site.
 * When a robot policy has been obtained, all connections waiting for it are checked against the resulting robot filter, 
 * and, if allowed, are added to the main queue for dispatching.
 */
public class FilteredConnectionQueue extends ConnectionQueue implements ConnectionListener {

  /**
   * lists of waiting connections, keyed by site hostname
   */ 
  private Hashtable waitingLists;

  /**
   * robot filters, keyed by site hostname
   */
  private Hashtable robotFilters;

  /**
   * Constructs a filtered connection queue.
   */
  public FilteredConnectionQueue (int capacity) {
    super(capacity);
    this.robotFilters = new Hashtable();
    this.waitingLists = new Hashtable();
  }

  /**
   * Enqueues a connection on the filtered connection queue.
   * <ul>
   * <li>If a filter already exists for the connection's hostname, the connection is checked against the filter 
   * and is enqueued on the main queue for dispatching (may block if queue is currently full or over capacity).
   * <li>If no filter exists yet, this method tries to find a waiting list for that filter:
   * <ul>
   * <li> If no waiting list exists, then this is the first connection from the connection's site, 
   * so a new <code>RobotFilterConnection</code> is dispatched to find the robot policy for the site, 
   * and a new waiting list for this site is created.
   * <li> If the waiting list already exists, then the connection is just added to the list.
   * </ul>
   * </ul>
   * @exception InterruptedException if this method was interrupted during wait.
   */
  public synchronized void enqueue (Connection connection) throws InterruptedException {
    System.out.println("FilterConnectionQueue: in enqueue");
    String hostname = getHostnameFromConnection(connection);
    RobotFilter filter = (RobotFilter)robotFilters.get(hostname);
    if (filter == null) {
      Vector waitingList = (Vector)waitingLists.get(hostname);
      if (waitingList == null) {
        waitingList = new Vector();
        RobotFilterConnection newFilterConnection = new RobotFilterConnection(hostname);
        newFilterConnection.addConnectionListener(this);
        newFilterConnection.setState(Connection.ALIVE);
        forceEnqueue(newFilterConnection);
        waitingLists.put(hostname, waitingList);
      }
      waitingList.addElement(connection);
      return;
    }
    if (filter.isAllowed(connection))
      super.enqueue(connection);
    else
      connection.setState(Connection.FILTERED);
  }

  /**
   * This method is called when state changes occur in <code>RobotFilterConnection</code> objects dispatched by this object.
   * Whenever a <code>RobotFilterConnection</code> finishes, the filter object for the site is obtained from it and is added 
   * to the filter set. In addition, all the connections waiting for this filter are checked against it, and the ones which 
   * pass the check are added to the main queue for dispatching (the connections are added using forceEnqueue, so this method
   * <b>does not</b> block).
   * @param event the event object containing the state change information.
   */ 
  public synchronized void connectionStateChanged(ConnectionEvent event) {
    System.out.println("FilterConnectionQueue: in connectionStateChanged");
    switch(event.getState()) {
    case Connection.ENDED:
      RobotFilterConnection filterConnection = (RobotFilterConnection)event.getSource();
      Debug.print(/*filterConnection+*/": dead");
      RobotFilter filter = filterConnection.getFilter();
      String hostname = filter.getHostname();
      Vector waitingList = (Vector)waitingLists.remove(hostname);
      for (Enumeration e=waitingList.elements();e.hasMoreElements();) {
        Connection connection = (Connection)e.nextElement();
        if (filter.isAllowed(connection))
          forceEnqueue(connection);
        else
          connection.setState(Connection.FILTERED);
      }
      robotFilters.put(hostname, filter);
      break;
    case Connection.ALIVE:
      Debug.print(/*event.getSource()+*/": alive");
    }
  }

  /**
   * Extracts the hostname from a connection.
   * This is a dummy implemetation.
   * This method should probably not even be here, but should be implemented by a subclass 
   * of the <code>Connection</code> class which is filterable (actually, Filterable should probably be an interface).
   */
  private String getHostnameFromConnection (Connection connection) {
    return new String("dummy hostname");
  }
}
