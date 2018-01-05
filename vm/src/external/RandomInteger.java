package external;

import casplan.vm.Command;

public class RandomInteger extends Command {
  @Override
  public void execute() {
    intStack[intStackPos] = (int) Math.floor(Math.random() * intStack[intStackPos]);
  }
}
