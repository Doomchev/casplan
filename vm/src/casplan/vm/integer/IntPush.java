package casplan.vm.integer;

import casplan.vm.Command;

public class IntPush extends Command {
  int value;
  
  public IntPush(int value) {
    this.value = value;
  }

  @Override
  public void execute() {
    intStackPos++;
    //if(intStackPos >= 100) stop();
    intStack[intStackPos] = value;
  }
}
