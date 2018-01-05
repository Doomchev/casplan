package casplan.vm;

import casplan.vm.string.*;
import casplan.vm.integer.*;
import external.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Base {
  public static final int stackSize = 100;
  
  public static boolean[] boolStack = new boolean[stackSize];
  public static int boolStackPos = -1;
  public static int[] intStack = new int[stackSize];
  public static int intStackPos = -1, intVarpos = -1;
  public static String[] stringStack = new String[stackSize];
  public static int stringStackPos = -1;
  public static Command[] commandStack = new Command[stackSize];
  public static int commandStackPos = -1;
  
  public static Context currentContext;
  public static Command currentCommand;
  public static HashMap<String, Command> labels = new HashMap<>();
  public static LinkedList<GoTo> gotos = new LinkedList<>();
  
  public static void main(String[] args) {
    Function main = new Function();    
    
    switch("factorial") {
      case "factorial":
        Function factorial = new Function();
          factorial.add(new IntParamInit(1));
          factorial.add(new IntPushParam(0));
          factorial.add(new IntPush(1));
          factorial.add(new IntEqual());
          If ifCommand = new If();
          factorial.add(ifCommand);
            ifCommand.addThen(new IntPush(1));
            ifCommand.addThen(new GoTo("next"));

            ifCommand.addElse(new IntPushParam(0));
            ifCommand.addElse(new IntPush(1));
            ifCommand.addElse(new IntSubtract());
            ifCommand.addElse(new CallFunction(factorial));
            ifCommand.addElse(new IntPushParam(0));
            ifCommand.addElse(new IntMultiply());
            ifCommand.addElse(new GoTo("next"));

          factorial.add(new IntShift(1), "next");
          factorial.add(new Return());

        main.add(new StringPush("Factorial of 5 is "));
        main.add(new IntPush(5));
        main.add(new CallFunction(factorial));
        main.add(new IntToString());
        main.add(new StringAdd());
        main.add(new StringPush("."));
        main.add(new StringAdd());
        main.add(new ShowMessage());
        main.add(new End());
        break;
        
      case "guess":
        main.add(new IntVarsInit(2));
        main.add(new IntPush(100));
        main.add(new RandomInteger());
        main.add(new IntVarSet(0));
        
        main.add(new StringPush("What number did I guess?"), "doStart");
        main.add(new EnterString());
        main.add(new StringToInt());
        main.add(new IntVarSet(1));
        main.add(new IntPushVar(1));
        main.add(new IntPushVar(0));
        main.add(new IntLess());
        ifCommand = new If();
        main.add(ifCommand);
          ifCommand.addThen(new StringPush("Your number is too small!"));
          ifCommand.addThen(new ShowMessage());
          ifCommand.addThen(new GoTo("doStart"));
          
          ifCommand.addElse(new IntPushVar(1));
          ifCommand.addElse(new IntPushVar(0));
          ifCommand.addElse(new IntMore());
          If ifCommand2 = new If();
          ifCommand.addElse(ifCommand2);
            ifCommand2.addThen(new StringPush("Your number is too big!"));
            ifCommand2.addThen(new ShowMessage());
            ifCommand2.addThen(new GoTo("doStart"));
          
            ifCommand2.addElse(new StringPush("You are right!"));
            ifCommand2.addElse(new ShowMessage());
            ifCommand2.addElse(new End());
            
        main.add(new GoTo("doStart"));
        break;
    }
    
    for(GoTo command : gotos) command.destination = labels.get(command.label);
    
    currentCommand = main.firstCommand;
    currentContext = new Context();
    while(true) {
      //System.out.println(currentCommand.getClass().getSimpleName());
    
      Command command = currentCommand;
      currentCommand = command.nextCommand;
      command.execute();
      
      /*System.out.print("bool: ");
      for(int index = 0; index <= boolStackPos; index++) {
        System.out.print(boolStack[index] ? "true, " : "false, ");
      }
      System.out.print("\nint: ");
      for(int index = 0; index <= intStackPos; index++) {
        System.out.print(intStack[index] + ", ");
      }
      System.out.print("\nstring: ");
      for(int index = 0; index <= stringStackPos; index++) {
        System.out.print(stringStack[index] + ", ");
      }
      System.out.print("\n\n");*/
    }
  }
  
  public static void stop() {
    int a = 0;
  }
}
