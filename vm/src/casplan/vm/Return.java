package casplan.vm;

public class Return extends Command {
  @Override
  public void execute() {
    currentCommand = currentContext.nextCommand;
    currentContext = currentContext.parentContext;
  }
}
