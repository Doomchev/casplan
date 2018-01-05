package casplan;

import casplan.template.FunctionTemplate;
import casplan.template.ParameterTemplate;

public abstract class ParsingFunction extends Parser {
  abstract FunctionTemplate create(Position startingPos);

  public static void init() {
    parsingFunctions.put("let", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        String id = readId();
        readExpectedSymbol('=');
        FunctionTemplate setVariable = new FunctionTemplate(2, "let");
        setVariable.params[0] = addLocalVariable(id);
        setVariable.params[1] = readFunction();
        return init(setVariable, startingPos);
      }
    });
    
    parsingFunctions.put("break", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        return new FunctionTemplate("break");
      }
    });
    
    parsingFunctions.put("continue", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        return new FunctionTemplate("continue");
      }
    });
    
    parsingFunctions.put("return", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        FunctionTemplate ret = new FunctionTemplate(1, "return");
        ret.params[0] = readFunction();
        return init(ret, startingPos);
      }
    });
    
    parsingFunctions.put("do", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        FunctionTemplate doFunc = init(new FunctionTemplate(1, "do")
            , startingPos);
        doFunc.params[0] = readCodeBlock(false);
        return doFunc;
      }
    });
    
    parsingFunctions.put("if", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        readExpectedSymbol('(');
        FunctionTemplate ifBlock = init(new FunctionTemplate(3, "if")
            , startingPos);
        ifBlock.params[0] = readFunction();
        readExpectedSymbol(')');
        ifBlock.params[1] = readCodeBlock(false);
        Position elsePos = new Position();
        if(readId().equals("else")) {
          ifBlock.params[2] = readCodeBlock(false);
        } else {
          elsePos.set();
        }
        return ifBlock;
      }
    });
    
    parsingFunctions.put("for", new ParsingFunction() {
      @Override
      FunctionTemplate create(Position startingPos) {
        readExpectedSymbol('(');
        Position bracketPos = new Position();
        
        String id1 = readId();
        if(id1.isEmpty()) error("Expected identifier");
        ParameterTemplate var1 = addLocalVariable(id1);
        
        switch(readId()) {
          case "at":
            String id2 = readId();
            if(id2.isEmpty()) error("Expected identifier");
            
            FunctionTemplate forIn = new FunctionTemplate(4, "forIn");
            //var1, var2, object
            //value, index, object, code
            forIn.params[0] = var1;
            forIn.params[1] = addLocalVariable(id2);
            if(!readId().equals("in")) error("\"in\" expected");
            forIn.params[2] = readFunction();
            readExpectedSymbol(')');
    
            forIn.params[3] = readCodeBlock(false);
            return init(forIn, startingPos);
          case "in":
            forIn = new FunctionTemplate(4, "forIn");
            forIn.params[0] = var1;
            forIn.params[2] = readFunction();
            readExpectedSymbol(')');
            forIn.params[3] = readCodeBlock(false);
            return init(forIn, startingPos);
          case "indexin":
            forIn = new FunctionTemplate(4, "forIn");
            forIn.params[0] = var1;
            forIn.params[2] = readFunction();
            readExpectedSymbol(')');
            forIn.params[3] = readCodeBlock(false);
            return init(forIn, startingPos);
        }
        
        if(readSymbol('=')) {
          FunctionTemplate range = readFunction().toFunctionTemplate();
          readExpectedSymbol(')');
          FunctionTemplate forInRange
              = init(new FunctionTemplate(3, "forInRange"), startingPos);
          forInRange.params[0] = var1;
          forInRange.params[1] = range;
          forInRange.params[2] = readCodeBlock(false);
          return forInRange;
        }
        
        bracketPos.set();
        FunctionTemplate forFunc = new FunctionTemplate(4, "for");
        forFunc.params[0] = readFunctionCall();
        readExpectedSymbol(';');
        forFunc.params[1] = readFunction();
        readExpectedSymbol(';');
        forFunc.params[2] = readFunctionCall();
        readExpectedSymbol(')');
        
        forFunc.params[3] = readCodeBlock(false);
        return init(forFunc, startingPos);
      }
    });
  }
}
