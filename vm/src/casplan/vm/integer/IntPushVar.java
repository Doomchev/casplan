package casplan.vm.integer;

import casplan.vm.Command;

public class IntPushVar extends Command {
  int pos;

  public IntPushVar(int pos) {
    this.pos = pos;
  }

  @Override
  public void execute() {
    intStackPos++;
    intStack[intStackPos] = currentContext.intVars[pos];
  }
}
