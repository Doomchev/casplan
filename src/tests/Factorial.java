package tests;

import casplan.Base;
import casplan.Parser;

public class Factorial {
  public static void main(String[] args) {
    Base.executeFunctionCall(Parser.readModule("factorial.cas"));
  }
}
