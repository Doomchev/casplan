package tests;

import casplan.Base;
import casplan.Parser;

public class SixPawns {
  public static void main(String[] args) {
    //Parser.toHTML(Parser.readModule("six_pawns.cas"), "index.html");
    Base.executeCodeBlock(Parser.readModule("six_pawns.cas"));
  }
}
