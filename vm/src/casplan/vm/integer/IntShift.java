package casplan.vm.integer;

import casplan.vm.Command;

public class IntShift extends Command {
  int value;

  public IntShift(int value) {
    this.value = value;
  }
  
  @Override
  public void execute() {
    intStack[intStackPos - value] = intStack[intStackPos];
    intStackPos -= value;
  }
}
