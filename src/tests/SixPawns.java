package tests;

import casplan.Base;
import casplan.Parser;

public class SixPawns {
  public static void main(String[] args) {
    //Parser.toHTML(Parser.readModule("examples/six_pawns/main.cas"), "index.html");
    Base.executeFunctionCall(Parser.readModule("examples/six_pawns/main.cas"));
  }
}
