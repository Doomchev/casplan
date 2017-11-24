package tests;

import casplan.Base;
import casplan.Parser;

public class Dialogs {
  public static void main(String[] args) {
    Base.executeCodeBlock(Parser.readModule("dialogs.cas"));
  }
}
