package casplan.vm.string;

import casplan.vm.Command;

public class StringPush extends Command {
  String string;

  public StringPush(String string) {
    this.string = string;
  }

  @Override
  public void execute() {
    stringStackPos++;
    stringStack[stringStackPos] = string;
  }
}
