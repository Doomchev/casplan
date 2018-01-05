package casplan.vm;

public class Function extends Base {
  Command firstCommand = null;
  Command lastCommand;

  public void add(Command command, String label) {
    labels.put(label, command);
    add(command);
  }
  
  public void add(Command command) {
    if(firstCommand == null) {
      firstCommand = command;
    } else {
      lastCommand.nextCommand = command;
    }
    lastCommand = command;
  }
}
