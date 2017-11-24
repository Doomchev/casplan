package casplan;

import casplan.value.*;
import casplan.structure.*;
import casplan.object.*;
import casplan.function.object.*;
import casplan.ChunkSequence.Chunk;
import casplan.function.SetVariable;
import casplan.function.object.CreateList;
import casplan.function.object.CreateObject.Entry;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;

public class Parser extends Base {
  public static void main(String[] args) throws IOException {
    executeCodeBlock(readModule("main.cas"));
  }
  
  static String text;
  static int textIndex, textLength, currentLine, lineStart;
  
  
  public static Function[] readModule(String fileName) {
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
    } catch (UnsupportedEncodingException ex) {
      Base.parserError("Unsupported encoding");
    } catch (IOException ex) {
      Base.parserError("Cannot read " + fileName);
    }
    
    return readCodeBlock(true);
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
  
  static Function[] readCodeBlock(boolean multipleLines) {
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
      
      Function call = readFunctionCall();
      if(call == null) continue;
      code.add(call);
      if(!multipleLines) break;
    }    
    
    return code.toArray(new Function[0]);
  }
  
  static CasObject[] readParams() {
    LinkedList<CasObject> params = new LinkedList<>();
    while(true) {
      if(readSymbol(')')) {
        return params.toArray(new CasObject[0]);
      }
      CasObject param = readFunction();
      params.add(param);
      readSymbol(',');
      if(params.size() > 100) stop(null);
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
  
  static If currentIf = null;
  static Function readFunctionCall() {
    Position startingPos = new Position();
    String id = readId();
    
    if(id.equals("else")) {
      if(currentIf == null) error("Else without if");
      If ifBlock = currentIf;
      currentIf = null;
      ifBlock.elseCode = readCodeBlock(false);
      return readFunctionCall();
    }
    currentIf = null;
    
    switch(id) {
      case "let":
        id = readId();
        readExpectedSymbol('=');
        SetVariable setVariable = new SetVariable();
        setVariable.let = true;
        setVariable.params = new CasObject[2];
        setVariable.params[1] = readFunction();
        setVariable.params[0] = addParameter(id);
        return setVariable;
      case "break":
        return Break.instance;
      case "continue":
        return Continue.instance;
      case "return":
        return new Return(readFunction());
      case "do":
        return new Do(readCodeBlock(false));
      case "if":
        readExpectedSymbol('(');
        CasObject ifCondition = readParams()[0];
        Function[] thenCode = readCodeBlock(false);
        If ifBlock = new If(ifCondition, thenCode);
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
            Parameter var2 = addParameter(id2);
            
            if(!readId().equals("in")) error("\"in\" expected");
            CasObject object = readFunction();
            readExpectedSymbol(')');
            return new ForIn(var1, var2, object, readCodeBlock(false));
          case "in":
            object = readFunction();
            readExpectedSymbol(')');
            return new ForIn(var1, null, object, readCodeBlock(false));
          case "indexin":
            object = readFunction();
            readExpectedSymbol(')');
            return new ForIn(null, var1, object, readCodeBlock(false));
        }
        
        if(readSymbol('=')) {
          Range range = readFunction().toRange();
          if(range != null) {
            readExpectedSymbol(')');
            return new ForInRange(var1, range, readCodeBlock(false));
          }
        }
        
        bracketPos.set();
        Function init = readFunctionCall();
        readExpectedSymbol(';');
        CasObject forCondition = readFunction();
        readExpectedSymbol(';');
        Function increment = readFunctionCall();
        readExpectedSymbol(')');
        return new For(init, forCondition, increment, readCodeBlock(false));
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
          return call;
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
          defaultValues.add(readFunction());
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

      if(shortFunction) {
        func.code = new Function[1];
        func.code[0] = new Return(readFunction());
      } else {
        func.code = readCodeBlock(false);
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
  
  
  
  static CasObject readFunction() {
    return readChunkSequence().compile().value;
  }
  
  static CreateObject readObject(ChunkSequence sequence, CasObject[] constructorParams) {
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
      creator.entries.add(new Entry(Field.get(fieldId), readFunction()));
    }
  }
  
  static ChunkSequence readChunkSequence() {
    String id = readId();
    ChunkSequence sequence = new ChunkSequence();
    
    while(true) {
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
              CasObject object = readFunction();
              Function func2 = (Function) object;
              if(func2 != null) func2.inBrackets = true;
              sequence.addObject(object);
              readExpectedSymbol(')');
              continue;
            }
            
            CasObject[] params = readParams();
            
            // function / constructor call
            Function func = readSymbol('{') ? readObject(sequence, params)
                : new Function();
            func.params = params;
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
                CasObject value = readFunction();
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
              sequence.addObject(readFunction());
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
      
      switch(character) {
        case '\n':
          newLine();
        case ' ':
        case '\t':
          textIndex++;
          break;
        default:
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
  }
  
  static void newLine() {
    /*System.out.println(currentLine + ": " +text.substring(lineStart + 1
        , textIndex));*/
    currentLine++;
    lineStart = textIndex;
  }
  
  static String readId() {
    int startIndex = -1;
    while(true) {  
      char character;
      if(textIndex == textLength) {
        if(startIndex < 0) return "";
        character = ' ';
      } else {
        character = text.charAt(textIndex);
      }
      
      switch(character) {
        case '\n':
          newLine();
        case ' ':
        case '\t':
          if(startIndex >= 0) {
            textIndex++;
            return text.substring(startIndex, textIndex - 1).trim();
          }
          break;
        default:
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
    while(true) {
      if(textIndex == textLength) return false;
      char character = text.charAt(textIndex);
      switch(character) {
        case '\n':
          newLine();
        case ' ':
        case '\t':
          break;
        default:
          if(character == symbol) textIndex++;
          return character == symbol;
      }
      textIndex++;
    }        
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
            sequence.addObject(readFunction());
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
