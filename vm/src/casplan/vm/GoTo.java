package casplan.vm;

public class GoTo extends Command {
  String label;
  Command destination;

  @SuppressWarnings("LeakingThisInConstructor")
  public GoTo(String destination) {
    this.label = destination;
    gotos.add(this);
  }
  
  @Override
  public void execute() {
    currentCommand = destination;
  }
}
