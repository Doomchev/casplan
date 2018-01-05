package casplan.object;

import casplan.Base;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class SaveObject extends Function {
  @Override
  public Function execute(Context context, CasObject[] params) {
    String fileName = params[1].toStr(context);
    UserObject object = params[0].toValue(context).toUserObject();
    linkIndex = -1;
    object.initLink();
    usedObjects.clear();
    try {
      output = Output.CASPLAN;
      PrintWriter writer = new PrintWriter(fileName, "UTF-8");
      writer.print(object.toString(true));
      writer.close();
    } catch (FileNotFoundException ex) {
      Base.parserError("Cannot write to " + fileName);
    } catch (UnsupportedEncodingException ex) {
      Base.parserError("Unsupported encoding");
    }
    usedObjects.clear();
    objectToLink.clear();
    linkToObject.clear();
    return null;
  }
}
