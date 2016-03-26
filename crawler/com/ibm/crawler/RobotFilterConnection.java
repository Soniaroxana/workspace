package com.ibm.crawler;

/**
 * This class is a special <code>Connection</code> class which retrieves a web server's robots.txt file, 
 * parses it, and creates a <code>RobotFilter</code> object accordingly. 
 * This is a dummy implementation.
 * @see com.ibm.crawler.RobotFilter
 */
public class RobotFilterConnection extends Connection {

  String hostname;
  RobotFilter filter;

  /**
   * Constructs a connection to retrieve a web server's robots.txt file.
   * @param hostname the hostname of the web server
   */
  public RobotFilterConnection (String hostname) {
    this.hostname = hostname;
  }
  
  /**
   * Retrieves the filter created by this object.
   */
  public RobotFilter getFilter() {
    return filter;
  }

  /**
   * This method should implement the retrieval of the web serer's robots.txt file 
   * and the creation of the <code>RobotFilter</code> object accordingly. 
   * Note that if a web server does not contain a robots.txt file, 
   * then this method should still create a filter which accepts all connections.
   * This is a dummy implementation.
   */
  public void perform() {
    Debug.print(/*this+*/": begin");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }

    filter = new RobotFilter(hostname);
  }

}
