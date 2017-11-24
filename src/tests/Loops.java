package tests;

import casplan.Base;
import casplan.Parser;

public class Loops {
  public static void main(String[] args) {
    Base.executeCodeBlock(Parser.readModule("loops.cas"));
  }
}
