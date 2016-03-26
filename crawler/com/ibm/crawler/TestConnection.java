package com.ibm.crawler;

//import java.util.Random;

/**
 * This class is a dummy implementation of the <code>Connection</code> class.
 * it mimics the retiraval and parsing of a node, and the insertion of new descendant nodes into the database.
 */ 
public class TestConnection extends Connection {

  /**
   * the database object to which new nodes are instertd
   */
  Database database;

  /**
   * the node which should be retrieved by this connection
   */
  Node node;

  /**
   * Constructs a test connection object.
   */
  public TestConnection(Database database, Node node) {
    this.database = database;
    this.node = node;
  }
  
  /**
   * The test connection's perform method mimics the retrieval of the node and the insertion of new descendant nodes into the database.
   * Note how the <code>shouldStop</code> method is called to avoid unnecessary work after the connection has been advised to stop.
   */
  public void perform() {
    Debug.print(this+": begin");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
    }
    
    if (shouldStop()) {
      Debug.print(this+": stopping");
      return;
    }
    
    int numberOfDescendants = Math.abs(new Random().nextInt()) % 4;
    Debug.print(this+": adding "+numberOfDescendants+" descendants");
    for (int i=0;i<numberOfDescendants;i++)
      database.put(new Node());
  }
}
