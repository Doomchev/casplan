package casplan.target;

import casplan.template.CodeBlockTemplate;
import casplan.template.UserFunctionTemplate;

public class JavaScript extends Target {
  @Override
  public String getHeader() {
    return "<!DOCTYPE html><html lang='en'><head>"
          + "<meta charset='UTF-8'><script type='text/javascript'>\n"
          + "images_ = ['pawns.png', 'board.png'];\nfunction main_() {";
  }

  @Override
  public String getFooter() {
    return "}\n</script><script src='casplan.js'></script></head>"
          + "<body></body></html>";
  }

  @Override
  public String exportUserFunction(UserFunctionTemplate func) {
    return "";
  }

  @Override
  public String exportCode(CodeBlockTemplate code) {
    return "";
  }
}
