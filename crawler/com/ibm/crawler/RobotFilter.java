package com.ibm.crawler;

//import java.util.Random;

/**
 * Objects of this class are filters that check whether a connection can be allowed to proceed.
 * This is a dummy implementation. In reality, the robot filter should contain rules dictated by a site's robots.txt file, 
 * which is obtained by a <code>RobotFilterConnection</code> object dispatched by the <code>FilteredConnectionQueue</code>
 * @see com.ibm.crawler.RobotFilterConnection
 * @see com.ibm.crawler.FilteredConnectionQueue
 */
public class RobotFilter {

  /**
   * the hostname of this filter's site
   */
  private String hostname;

  /**
   * Constructs a robot filter for a site.
   * This is a dummy implementation.
   */
  public RobotFilter (String hostname) {
    this.hostname = hostname;
  }
  
  /**
   * This is a dummy version of the filter's acceptance method.
   * (The input for this method should probably be an object implementing a Filterable interface, not a <code>Connection</code>)
   * @return true if the connection is allowed to proceed, false otherwise.
   */
  public boolean isAllowed(Connection connection) {
    return (Math.abs(new Random().nextInt()) % 2 == 0) ? true : false;
  }

  /**
   * Gets the hostname of this filter's site
   */
  public String getHostname() {
    return hostname;
  }

}
