package casplan;

import casplan.value.*;
import casplan.structure.*;
import casplan.object.*;
import casplan.function.object.*;
import casplan.ChunkSequence.Chunk;
import casplan.function.*;
import casplan.function.object.CreateList;
import casplan.function.object.CreateObject.Entry;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Parser extends Base {
  public static void main(String[] args) throws IOException {
    executeFunctionCall(readModule("main.cas"));
  }
  
  static String text;
  static int textIndex, textLength, currentLine, lineStart;
  
  
  public static FunctionCall readModule(String fileName) {
    workingDirectory = new File(fileName).getParent() + "/";
    
    currentLine = 1;
    lineStart = -1;
    InputStream stream = null;
    try {
      stream = new FileInputStream(fileName);
    } catch (FileNotFoundException ex) {
      Base.parserError(fileName + " not found.");
    }
    
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(stream, "UTF8"));
      text = "";
      while(true) {      
        String str = reader.readLine();
        if(str == null) break;
        text += str + "\n";
      }
      textIndex = 0;
      textLength = text.length();
      
      reader.close();
    } catch (UnsupportedEncodingException ex) {
      Base.parserError("Unsupported encoding");
    } catch (IOException ex) {
      Base.parserError("Cannot read " + fileName);
    }
    
    UserFunction func = new UserFunction();
    func.code = readCodeBlock(true, func);
    func.vars = new Parameter[0];
    func.defaultValues = new CasObject[0];
    
    Function params = new Function();
    params.params = new CasObject[0];
    
    FunctionCall call = new FunctionCall();
    call.params = new CasObject[2];
    call.params[0] = func;
    call.params[1] = params;
    return call;
  }  

  public static void toHTML(Function[] code, String fileName) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(fileName, "UTF-8");
      writer.println("<!DOCTYPE html><html lang='en'><head>"
          + "<meta charset='UTF-8'><script type='text/javascript'>\n"
          + "images_ = ['pawns.png', 'board.png'];\nfunction main_() {");
      
      tabString = "\t";
      for(HashMap.Entry<String, UserFunction> entry : userFunctions.entrySet()) {
        UserFunction func = entry.getValue();
        writer.println(tabString + "function " + entry.getKey()
            + func.getParams() + " {\n" + func.getCode() + tabString + "}");
      }
      tabString = "";
      
      writer.print(codeToString(code));
      
      writer.println("}\n</script><script src='casplan.js'></script></head>"
          + "<body></body></html>");
      writer.close();
    } catch (FileNotFoundException ex) {
      Base.parserError("Cannot write to " + fileName);
    } catch (UnsupportedEncodingException ex) {
      Base.parserError("Unsupported encoding");
    }
  }
  
  static Function[] readCodeBlock(boolean multipleLines, Function parent) {
    LinkedList<Function> code = new LinkedList<>();
    while(true) {
      if(textIndex == textLength) break;
      if(text.charAt(textIndex) == '{') {
        textIndex++;
        if(multipleLines) error("Function call expected while encountered {");
        multipleLines = true;
        continue;
      }
      
      if(text.charAt(textIndex) == '}') {
        textIndex++;
        break;
      }

      Function call = readFunctionCall(null);
      if(call == null) continue;
      call.blockParent = parent;
      code.add(call);
      if(!multipleLines) break;
    }    
    
    return code.toArray(new Function[0]);
  }
  
  static CasObject[] readParams(Function parent) {
    LinkedList<CasObject> params = new LinkedList<>();
    while(true) {
      if(readSymbol(')')) {
        return params.toArray(new CasObject[0]);
      }
      CasObject param = readFunction(parent);
      params.add(param);
      readSymbol(',');
    }    
  }
   
  static class Position {
    int posTextIndex, posLine, posLineStart;
     
    Position() {
      posTextIndex = textIndex;
      posLine = currentLine;
      posLineStart = lineStart;
    }
 
    void set() {
      textIndex = posTextIndex;
      currentLine = posLine;
      lineStart = posLineStart;
    }
  }
  
  static <E extends Function> E init(E func, Position pos) {
    func.line = pos.posLine;
    func.column = pos.posTextIndex - pos.posLineStart;
    func.startingTextIndex = pos.posTextIndex;
    func.textLength = textIndex - pos.posTextIndex;
    return func;
  }
  
  static If currentIf = null;
  static Function readFunctionCall(Function parent) {
    skipEmptyCharacters();
    Position startingPos = new Position();
    String id = readId();
    
    if(id.equals("else")) {
      if(currentIf == null) error("Else without if");
      If ifBlock = currentIf;
      currentIf = null;
      ifBlock.elseCode = readCodeBlock(false, ifBlock);
      return readFunctionCall(currentIf);
    }
    currentIf = null;
    
    switch(id) {
      case "stop":
        return init(new Breakpoint(), startingPos);
      case "let":
        id = readId();
        readExpectedSymbol('=');
        SetVariable setVariable = new SetVariable(true);
        setVariable.params[1] = readFunction(setVariable);
        setVariable.params[0] = addParameter(id);
        return init(setVariable, startingPos);
      case "break":
        return Break.instance;
      case "continue":
        return Continue.instance;
      case "return":
        Return ret = new Return();
        ret.params[0] = readFunction(ret);
        return init(ret, startingPos);
      case "do":
        Do doFunc = init(new Do(), startingPos);
        doFunc.code = readCodeBlock(false, doFunc);
        return doFunc;
      case "if":
        readExpectedSymbol('(');
        If ifBlock = init(new If(), startingPos);
        ifBlock.condition = readFunction(ifBlock);
        readExpectedSymbol(')');
        ifBlock.thenCode = readCodeBlock(false, ifBlock);
        currentIf = ifBlock;
        return ifBlock;
      case "for":
        readExpectedSymbol('(');
        Position bracketPos = new Position();
        
        String id1 = readId();
        if(id1.isEmpty()) error("Expected identifier");
        Parameter var1 = addParameter(id1);
        
        switch(readId()) {
          case "at":
            String id2 = readId();
            if(id2.isEmpty()) error("Expected identifier");
            
            ForIn forIn = new ForIn();
            //var1, var2, object
            forIn.value = var1;
            forIn.index = addParameter(id2);
            if(!readId().equals("in")) error("\"in\" expected");
            forIn.object = readFunction(forIn);
            readExpectedSymbol(')');
            forIn.code = readCodeBlock(false, forIn);
            return init(forIn, startingPos);
          case "in":
            forIn = new ForIn();
            forIn.value = var1;
            forIn.object = readFunction(forIn);
            readExpectedSymbol(')');
            forIn.code = readCodeBlock(false, forIn);
            return init(forIn, startingPos);
          case "indexin":
            forIn = new ForIn();
            forIn.value = var1;
            forIn.object = readFunction(forIn);
            readExpectedSymbol(')');
            forIn.code = readCodeBlock(false, forIn);
            return init(forIn, startingPos);
        }
        
        if(readSymbol('=')) {
          Range range = readFunction(null).toRange();
          if(range != null) {
            readExpectedSymbol(')');
            ForInRange forInRange
                = init(new ForInRange(var1, range), startingPos);
            range.setParent(forInRange);
            forInRange.code = readCodeBlock(false, forInRange);
            return forInRange;
          }
        }
        
        bracketPos.set();
        For forFunc = new For();
        forFunc.init = readFunctionCall(forFunc);
        readExpectedSymbol(';');
        forFunc.condition = readFunction(forFunc);
        readExpectedSymbol(';');
        forFunc.increment = readFunctionCall(forFunc);
        readExpectedSymbol(')');
        
        forFunc.code = readCodeBlock(false, forFunc);
        return init(forFunc, startingPos);
    }
    
    while(true) {
      if(textIndex == textLength) return null;
      char character = text.charAt(textIndex);
      
      switch(character) {
        case '{':
          return null;
        case '}':
          return null;
        case '\n':
          newLine();
        case ' ':
        case '\t':
          break;
        case '(':
          textIndex++;
          UserFunction func = readFunctionDeclaration();
          if(func != null) {
            getVariable(id).setValue(null, func, func);
            userFunctions.put(id, func);
            return null;
          }
        default:
          startingPos.set();
          Chunk chunk = readChunkSequence().compile();
          Function call = chunk.call;
          if(call == null) error("Function call expected");
          return init(call, startingPos);
      }
      
      textIndex++;
    }    
  }
  
  
  
  static UserFunction readFunctionDeclaration() {
    final Position startingPos = new Position();
    final LinkedList<String> idList = new LinkedList<>();
    final LinkedList<CasObject> defaultValues = new LinkedList<>();
    while(true) {
      String varId = readId();
      if(!varId.isEmpty()) {
        idList.add(varId);
        if(readSymbol('=')) {
          defaultValues.add(readFunction(null));
        } else {
          defaultValues.add(Null.instance);
        }
        if(readSymbol(',')) continue;
      }
      if(!readSymbol(')')) break;

      boolean shortFunction;
      if(readSymbol('{')) {
        shortFunction = false;
        textIndex--;
      } else if(readSymbol('-')) {
        if(readSymbol('>')) {
          shortFunction = true;
        } else {
          break;
        }
      } else {
        break;
      }

      HashMap<String, Parameter> oldParameters = currentParameters;
      currentParameters = new HashMap<>();
      LinkedList<Parameter> oldParametersList = currentParametersList;
      currentParametersList = new LinkedList<>();
      
      UserFunction func = new UserFunction();
      
      for(String varId2 : idList) {
        func.name += (func.name.isEmpty() ? "" : ", ") + varId2;
        addParameter(varId2);
      }
      func.name = "(" + func.name + ")";
      init(func, startingPos);

      if(shortFunction) {
        func.code = new Function[1];
        Position pos = new Position();
        Return ret = new Return();
        ret.blockParent = func;
        ret.params[0] = readFunction(ret);
        func.code[0] = init(ret, pos);
      } else {
        func.code = readCodeBlock(false, func);
      }
      func.vars = currentParametersList.toArray(new Parameter[0]);
      func.defaultValues = defaultValues.toArray(new CasObject[0]);
      
      currentParameters = oldParameters;
      currentParametersList = oldParametersList;
      
      return func;
    }
    startingPos.set();
    return null;
  }
  
  
  
  static CasObject readFunction(Function parent) {
    CasObject func = readChunkSequence().compile().value;
    func.setParent(parent);
    return func;
  }
  
  static CreateObject readObject(ChunkSequence sequence
      , CasObject[] constructorParams) {
    CreateObject creator = new CreateObject(constructorParams);
    while(true) {
      String fieldId = readId();

      if(readSymbol('(')) {
        // object function or constructor
        UserFunction func = readFunctionDeclaration();
        if(func != null) {
          if(fieldId.isEmpty()) {
            creator.constructor = func;
          } else {
            creator.entries.add(new Entry(Field.get(fieldId), func));
          }
          continue;
        }
      }

      if(fieldId.isEmpty()) {
        readExpectedSymbol('}');
        return creator;
      }

      readExpectedSymbol(':');
      creator.entries.add(new Entry(Field.get(fieldId), readFunction(creator)));
    }
  }
  
  static ChunkSequence readChunkSequence() {
    skipEmptyCharacters();
    String id = readId();
    ChunkSequence sequence = new ChunkSequence();
    
    while(true) {
      skipEmptyCharacters();
      if(textIndex == textLength) {
        if(!id.isEmpty()) error("operator or function parameters are expected");
        return sequence;
      }
      char character = text.charAt(textIndex);
      
      if(".;,\"(){}+-/*=<>!|&?:[]".indexOf(character) >= 0) {
        textIndex++;
        switch(character) {
          case '"':
            if(sequence.last != null && sequence.last.separator == null) {
              textIndex--;
              return sequence;
            }
            readString(sequence);
            continue;
          case '(':
            if(id.isEmpty()) {
              // anonymous function like "() -> blah"
              UserFunction func = readFunctionDeclaration();
              if(func != null) {
                sequence.addObject(new GetFunction(func));
                continue;
              }
              
              // function inside brackets like "(a + b)"
              CasObject object = readFunction(null);
              Function func2 = (Function) object;
              if(func2 != null) func2.inBrackets = true;
              sequence.addObject(object);
              readExpectedSymbol(')');
              continue;
            }
            
            CasObject[] params = readParams(null);
            
            // function / constructor call
            Function func = readSymbol('{') ? readObject(sequence, params)
                : new Function();
            func.params = params;
            for(CasObject param : params) param.setParent(func);
            sequence.add(id);
            sequence.add('(');
            sequence.addObject(func);
            id = "";
            continue;
          case '{':
            sequence.addObject(readObject(sequence, null));
            id = "";
            continue;
          case ';':
          case ',':
            textIndex--;
            if(!id.isEmpty()) sequence.add(id);
            return sequence;
          case '[':
            if(id.isEmpty()) {
              // list creation
              LinkedList<CasObject> values = new LinkedList<>();
              while(true) {
                CasObject value = readFunction(null);
                if(value == null) {
                  readExpectedSymbol(']');
                  sequence.addObject(new CreateList(
                      values.toArray(new CasObject[0])));
                  break;
                }
                values.add(value);
                readSymbol(',');
              }
            } else {
              // getting item at index
              sequence.add(id);
              sequence.add('[');
              sequence.addObject(readFunction(null));
              readExpectedSymbol(']');
            }
            id = "";
            continue;
          case '}':
          case ')':
          case ']':
            textIndex--;
            if(!id.isEmpty()) sequence.add(id);
            if(sequence.isEmpty()) return ChunkSequence.empty;
            return sequence;
        }
        
        if(!id.isEmpty()) {
          sequence.add(id);
          id = "";
        }
        sequence.add(character);
        continue;
      }
      
      if(!id.isEmpty()) {
        sequence.add(id);
        id = "";
      }
      
      if(sequence.last != null && sequence.last.separator == null) {
        return sequence;
      }
      
      if(character >= '0' && character <= '9') {
        sequence.addObject(readNumber());
      } else {
        id = readId();
      }
    }
  }
  
  static void newLine() {
    /*System.out.println(currentLine + ": " +text.substring(lineStart + 1
        , textIndex));*/
    currentLine++;
    lineStart = textIndex;
  }
  
  static void skipEmptyCharacters() {
    while(true) {  
      char character;
      if(textIndex == textLength) return;
      character = text.charAt(textIndex);
      
      switch(character) {
        case '\n':
          newLine();
        case ' ':
        case '\t':
          break;
        default:
          return;
      }
      textIndex++;
    }
  }
  
  static String readId() {
    skipEmptyCharacters();
    int startIndex = -1;
    while(true) {  
      char character;
      if(textIndex == textLength) {
        if(startIndex < 0) return "";
        character = ' ';
      } else {
        character = text.charAt(textIndex);
      }
      
      while(true) {
        if(startIndex < 0) startIndex = textIndex;
        if(character >= 'a' && character <= 'z' || character >= 'A'
            && character <= 'Z' || character == '_') break;
        if(startIndex == textIndex) return "";
        if(character >= '0' && character <= '9') break;
        return text.substring(startIndex, textIndex).trim();
      }
      textIndex++;
    }
  }
  
  static boolean readSymbol(char symbol) {
    skipEmptyCharacters();
    if(textIndex == textLength) return false;
    char character = text.charAt(textIndex);
    if(character == symbol) textIndex++;
    return character == symbol;
  }
  
  static void readExpectedSymbol(char symbol) {
    if(!readSymbol(symbol)) error("\"" + String.valueOf(symbol)
        + "\" is expected");
  }
  
  static CasObject readNumber() {
    int startIndex = textIndex;
    while(true) {      
      if(textIndex == textLength) break;
      char character = text.charAt(textIndex);
      if(character < '0' || character > '9') break;
      textIndex++;
    }
    CasInteger integer = new CasInteger(Integer.parseInt(
        text.substring(startIndex, textIndex)));
    return integer;
  }
  
  static void readString(ChunkSequence sequence) {
    int startIndex = textIndex;
    String prefix = "";
    while(true) {
      if(textIndex == textLength) error("\" expected");
      char character = text.charAt(textIndex);
      if(character == '\"') break;
      if(character == '\\') {
        textIndex++;
        if(textIndex == textLength) error("escape sequence expected");
        character = text.charAt(textIndex);
        switch(character) {
          case '\\':
          case '\"':
          case 'n':
            prefix += text.substring(startIndex, textIndex - 1)
                + (character == 'n' ? "\n" : character); 
            startIndex = textIndex + 1;
            break;
          case '(':
            sequence.addObject(new CasString(prefix + text.substring(startIndex
                , textIndex - 1)));
            prefix = "";
            sequence.add('+');
            textIndex++;
            sequence.addObject(readFunction(null));
            readExpectedSymbol(')');
            sequence.add('+');
            startIndex = textIndex;
            continue;
        }
      }
      textIndex++;
    }
    sequence.addObject(new CasString(prefix + text.substring(startIndex
        , textIndex)));
    textIndex++;
  }
  
  public static void error(String message) {
    if(textIndex == textLength) message = "Reached end of file while " + message;
    Base.parserError(message + " in line " + currentLine + " column "
        + (textIndex - lineStart));
  }
}
