package casplan.template;

import java.util.LinkedList;

public class CreateListTemplate extends FunctionTemplate {
  public LinkedList<ObjectTemplate> values;

  public CreateListTemplate(LinkedList<ObjectTemplate> values) {
    super(0, "createList");
    this.values = values;
  }
}
