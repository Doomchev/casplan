package casplan.vm.integer;

import casplan.vm.Command;

public class IntToString extends Command {
  @Override
  public void execute() {
    stringStackPos++;
    stringStack[stringStackPos] = String.valueOf(intStack[intStackPos]);
    intStackPos =- 1;
  }
}
