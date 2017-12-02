package tests;

import casplan.Base;
import casplan.Parser;

public class Dialogs {
  public static void main(String[] args) {
    Base.executeFunctionCall(Parser.readModule("dialogs.cas"));
  }
}
