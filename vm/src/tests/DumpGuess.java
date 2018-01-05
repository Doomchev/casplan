package tests;

import casplan.Base;
import casplan.target.Dump;

public class DumpGuess {
  public static void main(String[] args) {
    Base.target = new Dump();
    Base.target.compile("examples/small/guess.cas", "examples/small/guess.txt");
  }
}
