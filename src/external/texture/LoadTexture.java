package external.texture;

import java.io.IOException;
import casplan.object.CasObject;
import casplan.function.ObjectFunction;
import casplan.object.Context;

public class LoadTexture extends ObjectFunction {
  @Override
  public CasObject toValue(Context context) {
    String fileName = params[0].toStr(context);
    try {
      return new Texture(fileName, "");
    } catch (IOException ex) {
      error("Cannot load image \"" + fileName + "\"");
    }
    return null;
  }
}
