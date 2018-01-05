package external;

import casplan.vm.Command;

public class End extends Command {
  @Override
  public void execute() {
    System.exit(0);
  }
}
