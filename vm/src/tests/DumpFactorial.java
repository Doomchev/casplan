package tests;

import casplan.Base;
import casplan.target.Dump;

public class DumpFactorial {
  public static void main(String[] args) {
    Base.target = new Dump();
    Base.target.compile("tests/factorial.cas", "tests/factorial.txt");
  }
}
