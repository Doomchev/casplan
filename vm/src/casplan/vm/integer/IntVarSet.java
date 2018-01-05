package casplan.vm.integer;

import casplan.vm.Command;

public class IntVarSet extends Command {
  int pos;

  public IntVarSet(int pos) {
    this.pos = pos;
  }
  
  @Override
  public void execute() {
    currentContext.intVars[pos] = intStack[intStackPos];
    intStackPos--;
  }
}
