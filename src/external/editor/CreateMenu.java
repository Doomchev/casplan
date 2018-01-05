package external.editor;

import casplan.function.ObjectFunction;
import casplan.list.CasList;
import casplan.object.CasObject;
import casplan.object.Context;
import casplan.object.Function;
import casplan.object.UserFunction;
import java.awt.event.ActionEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class CreateMenu extends ObjectFunction {
  void setListener(JPopupMenu menu, JMenuItem item, UserFunction func) {
    item.addActionListener((ActionEvent e) -> {
      menu.setVisible(false);
      execute(func, this);
    });
  }

  @Override
  public Function execute(Context context, CasObject[] params) {
    JPopupMenu menu = new JPopupMenu();
    CasList list = params[2].toValue(context).toList();
    for(CasObject entry : list.items) {
      JMenuItem item = new JMenuItem();
      CasList entryList = entry.toList();
      item.setText(entryList.items[0].toStr(context));
      setListener(menu, item, entryList.items[1].toUserFunction());
      menu.add(item);
    }
    menu.show(null, params[0].toInteger(context), params[1].toInteger(context));
    return null;
  }
  
  

  @Override
  public String toString() {
    return "CreateMenu";
  }
}
