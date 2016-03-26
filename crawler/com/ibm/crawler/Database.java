package com.ibm.crawler;

import java.util.Vector;

/**
 * This is a dummy implementation of a database object, just for illustration.
 * The database initially comes with a predefined number of nodes, and will only provide a limited number of nodes for retrieval
 */
public class Database {

  /**
   * the database nodes
   */ 
  private Vector nodes;

  /**
   * number of nodes left for retrieval
   */
  private int nodesLeft;


  /**
   * Constructs a database.
   * @param maxNumberOfNodes the maximum number of nodes this database will return in response to <code>getNodeForRetrieval</code> calls.
   * @param initialNumberOfNodes the initial number of nodes this database should come with.
   */
  public Database (int maxNumberOfNodes, int initialNumberOfNodes) {
    this.nodesLeft = maxNumberOfNodes;
    this.nodes = new Vector(initialNumberOfNodes);
    for (int i=0;i<initialNumberOfNodes;i++) {
      nodes.addElement(new Node());
    }
  }

  /**
   * Returns a node for retrieval.
   */
  public synchronized Node getNodeForRetrieval () {
    System.out.println("in  getNodeForRetrieval ");
    if (nodesLeft == 0) 
      return null;
    if (nodes.size() == 0) 
      return null;
    nodesLeft--;
    Node node = (Node)nodes.elementAt(0);
    nodes.removeElementAt(0);
    return node;
  }

  /**
   * Puts a node into database.
   */
  public synchronized void put (Node node) {
    System.out.println("in  put ");
    nodes.addElement(node);
  }

  
}
