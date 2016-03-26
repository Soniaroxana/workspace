package com.ibm.crawler;

/**
 * This object is called to obtain new connection objects
 */
public class ConnectionFactory {
  
  /**
   * the database from which nodes are fetched to populate new connections
   */
  private Database database;

  /**
   * Constructs the connection factory.
   * @param database the database from which new nodes are fetched for retrieval
   */
  public ConnectionFactory(Database database) {
    System.out.println("ConncectionFactory");
    this.database = database;
  }

  /**
   * Creates and returns a new connection object.
   * If the database has no new nodes for retieval, null is returned.
   * This a a dummy implementation.
   */
  public Connection getNewConnection() {
    System.out.println("Conncection#getNewConnection");

    Node node = database.getNodeForRetrieval();
    if (node == null) {
      Debug.print("connection factory: no new connection");
      return null;
    }
    Debug.print("connection factory: returning new connection");
    return new TestConnection(database, node);
  }
}
