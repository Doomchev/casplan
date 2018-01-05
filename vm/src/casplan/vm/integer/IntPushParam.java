package casplan.vm.integer;

import casplan.vm.Command;

public class IntPushParam extends Command {
  int paramPos;
  
  public IntPushParam(int paramPos) {
    this.paramPos = paramPos;
  }

  @Override
  public void execute() {
    intStackPos++;
    intStack[intStackPos] = intStack[currentContext.intStackPos + paramPos];
  }
}
