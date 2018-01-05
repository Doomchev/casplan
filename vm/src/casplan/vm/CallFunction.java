package casplan.vm;

public class CallFunction extends Command {
  Function function;

  public CallFunction(Function function) {
    this.function = function;
  }
  
  @Override
  public void execute() {
    currentContext = new Context();
    currentCommand = function.firstCommand;
  }
}
