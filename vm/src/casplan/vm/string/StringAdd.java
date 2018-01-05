package casplan.vm.string;

import casplan.vm.Command;

public class StringAdd extends Command {
  @Override
  public void execute() {
    stringStack[stringStackPos - 1] += stringStack[stringStackPos];
    stringStackPos--;
  }
}
