package com.ibm.crawler;

/**
 * Debug printing facility. provides synchronized printing.
 */
public class Debug {

  /**
   * Prints a string, followed by a newline character.
   */
  public static synchronized void print(String s) {
    System.out.println("crawler: in Debug");
    System.out.println(s);
  }
}
