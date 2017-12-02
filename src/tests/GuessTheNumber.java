package tests;

import casplan.Base;
import casplan.Parser;

public class GuessTheNumber {
  public static void main(String[] args) {
    Base.executeFunctionCall(Parser.readModule("guess.cas"));
  }
}
