package external.object;

import casplan.function.VoidFunctionCall;
import java.awt.image.BufferedImage;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Field;
import casplan.object.Function;
import casplan.value.CasInteger;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Texture extends CasObject {
  private static final Field widthField = Field.get("width");
  private static final Field heightField = Field.get("height");
  private static final Field drawField = Field.get("draw");
  
  BufferedImage image;
  String fileName;
  
  public Texture(String fileName) throws IOException {
    this.fileName = fileName;
    this.image = ImageIO.read(new File(fileName));
  }

  private static class Draw extends VoidFunctionCall {
    Function caller;

    public Draw(Function caller) {
      this.caller = caller;
    }
    
    @Override
    public Function execute(Context context, CasObject[] params) {
      try {
        Texture texture = (Texture) context.functionObject.toValue(context);
        int x = params.length < 1 ? 0 : params[0].toInteger(context);
        int y = params.length < 2 ? 0 : params[1].toInteger(context);
        int sourceX = params.length < 3 ? 0 : params[2].toInteger(context);
        int sourceY = params.length < 4 ? 0 : params[3].toInteger(context);
        int width = params.length < 5 ? texture.image.getWidth()
            : params[4].toInteger(context);
        int height = params.length < 6 ? texture.image.getHeight()
            : params[5].toInteger(context);
        Window.graphics.drawImage(texture.image, x, y, x + width, y + height
            , sourceX, sourceY, sourceX + width, sourceY + height, null);
      } catch(ClassCastException ex) {
        caller.error("First parameter is not Texture");
      }
      return null;
    }
  }
  
  @Override
  public CasObject toValue(Context context) {
    return this;
  }

  @Override
  public CasObject getField(Field field, Function caller) {
    if(field == drawField) return new Draw(caller);
    if(field == widthField) return new CasInteger(image.getWidth());
    if(field == heightField) return new CasInteger(image.getHeight());
    caller.error("There's no field \"" + field.name + "\" in list");
    return null;
  }      
  
  

  @Override
  public String toString() {
    return "Texture(" + fileName + ")";
  }
}
