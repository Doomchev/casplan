package casplan.vm.string;

import casplan.vm.Command;

public class StringToInt extends Command {
  @Override
  public void execute() {
    intStackPos++;
    intStack[intStackPos] = Integer.parseInt(stringStack[stringStackPos]);
    stringStackPos =- 1;
  }
}
