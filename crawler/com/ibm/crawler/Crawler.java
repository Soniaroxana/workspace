package com.ibm.crawler;

import java.io.IOException;
import java.util.Random;

/**
 * This is the crawler's external interface object, which also keeps all the crawler's instance settings and parameters.
 */
public class Crawler {

  /**
   * The crawler's connectionFactory object
   */
  private ConnectionFactory connectionFactory;

  /** 
   * number of worker threads the crawler should use
   */
  private int numberOfThreads;

  /**
   * global time limit on the crawler's run time, in miliseconds
   */
  private long globalTimeLimit;

  /**
   * this crawler's connection manager
   */
  private ConnectionManager connectionManager;

  /**
   * indicates whether this crawler should obey site robot exclusion rules
   */
  private boolean obeyRobotsExclusionProtocol;

  /**
   * Instantiates a Crawler object.
   * Sets the crawler's instance data and runs the connection manager.
   */
  public Crawler(ConnectionFactory connectionFactory, int numberOfThreads, long globalTimeLimit, boolean obeyRobotsExclusionProtocol) {
    System.out.println("Crawler");  
    this.numberOfThreads = numberOfThreads;
    this.globalTimeLimit = globalTimeLimit;
    this.connectionFactory = connectionFactory;
    this.obeyRobotsExclusionProtocol = obeyRobotsExclusionProtocol;
    this.connectionManager = new ConnectionManager(this);
    connectionManager.run();    
  }

  /**
   * Gets this crawler's connection factory.
   */
  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  /**
   * Gets the number of working threads the crawler should use.
   */
  public int getNumberOfThreads() {
    return numberOfThreads;
  }

  /**
   * Gets the crawler's global time limit.
   */
  public long getGlobalTimeLimit() {
    return globalTimeLimit;
  }

  /**
   * Checks whether the crawler should obey site-dictated robot exclusion rules.
   */
  public boolean obeyRobotsExclusionProtocol() {
    return obeyRobotsExclusionProtocol;
  }

  public static void main(String[] args) {
   
    Random r = new Random(System.currentTimeMillis());

    System.out.println("Crawler#main");
    Database database = new Database(r.nextInt(20)+2,r.nextInt(10)+1);
    ConnectionFactory connectionFactory = new ConnectionFactory(database);
    int numberOfThreads = 16; //r.nextInt(32);
    long globalTimeLimit = r.nextInt(300000);
    boolean obeyRobotsExclusionProtocol = true;

    new Crawler(connectionFactory, numberOfThreads, globalTimeLimit, obeyRobotsExclusionProtocol);
  }



}
