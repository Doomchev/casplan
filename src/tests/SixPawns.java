package tests;

import casplan.Parser;

public class SixPawns {
  public static void main(String[] args) {
    //Parser.toHTML(Parser.readModule("examples/six_pawns/main.cas"), "index.html");
    Parser.executeModule("examples/six_pawns/main.cas");
  }
}
