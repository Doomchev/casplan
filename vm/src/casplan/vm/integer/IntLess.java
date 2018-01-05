package casplan.vm.integer;

import casplan.vm.Command;

public class IntLess extends Command {
  @Override
  public void execute() {
    boolStackPos++;
    boolStack[boolStackPos] = 
        intStack[intStackPos - 1] < intStack[intStackPos];
    intStackPos -= 2;
  }
}
