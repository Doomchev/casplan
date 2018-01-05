package casplan.vm;

class If extends Command {
  public Command firstThenCommand = null, lastThenCommand;
  public Command firstElseCommand = null, lastElseCommand;

  public void addThen(Command command) {
    if(firstThenCommand == null) {
      firstThenCommand = command;
    } else {
      lastThenCommand.nextCommand = command;
    }
    lastThenCommand = command;
  }

  public void addElse(Command command) {
    if(firstElseCommand == null) {
      firstElseCommand = command;
    } else {
      lastElseCommand.nextCommand = command;
    }
    lastElseCommand = command;
  }

  @Override
  public void execute() {
    if(boolStack[boolStackPos]) {
      boolStackPos--;
      currentCommand = firstThenCommand;
    } else {
      boolStackPos--;
      currentCommand = firstElseCommand;
    }
  }
}
