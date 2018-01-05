package casplan;

import casplan.template.*;
import casplan.ChunkSequence.Chunk;
import casplan.template.CreateObject.Entry;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Parser extends Base {
  public static void main(String[] args) {
    executeModule("main.cas");
  }
  
  static StringBuffer text = null;
  static Source currentSource;
  static int textIndex, textLength, currentLine, lineStart;
  static final HashMap<String, ParsingFunction> parsingFunctions = new HashMap<>();
  public static LinkedList<UserFunctionTemplate> modules = new LinkedList<>();
  public static UserFunctionTemplate currentFunction;
  
  static {
    ParsingFunction.init();
  }
  
  public static void executeModule(String fileName) {
    workingDirectory = new File(fileName).getParent() + "/";
    importModule(fileName);
  }
    
  public static void importModule(String fileName) {
    for(Source source : sources) {
      if(source.fileName.equals(fileName)) return;
    }
    
    Source oldSource = currentSource;
    
    Position pos = new Position();
    
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
      text = new StringBuffer();
      while(true) {      
        String str = reader.readLine();
        if(str == null) break;
        text.append(str.replaceAll("\t", "  ")).append("\n");
      }
      textIndex = 0;
      textLength = text.length();
      
      reader.close();
    } catch (UnsupportedEncodingException ex) {
      Base.parserError("Unsupported encoding");
    } catch (IOException ex) {
      Base.parserError("Cannot read " + fileName);
    }
    
    currentSource = new Source(fileName, text);
    sources.add(currentSource);
    
    UserFunctionTemplate oldFunction = currentFunction; 
    
    currentFunction = new UserFunctionTemplate(new LinkedList<>());
    currentFunction.codeBlock = readCodeBlock(true);
    modules.add(currentFunction);

    pos.set();
    
    currentFunction = oldFunction;
    currentSource = oldSource;
    if(currentSource != null) {
      text = currentSource.text;
      textLength = text.length();
    }    
  }  


  
  static CodeBlockTemplate readCodeBlock(boolean multipleLines) {
    LinkedList<FunctionTemplate> code = new LinkedList<>();
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

      FunctionTemplate call = readFunctionCall();
      if(call == null) continue;
      code.add(call);
      if(!multipleLines) break;
    }    
    
    return new CodeBlockTemplate(code);
  }
  
  static LinkedList<ObjectTemplate>readParams() {
    LinkedList<ObjectTemplate> params = new LinkedList<>();
    while(true) {
      if(readSymbol(')')) return params;
      ObjectTemplate param = readFunction();
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
  
  static <E extends FunctionTemplate> E init(E func, Position pos) {
    func.line = pos.posLine;
    func.column = pos.posTextIndex - pos.posLineStart;
    func.startingTextIndex = pos.posTextIndex;
    func.textLength = textIndex - pos.posTextIndex;
    func.source = currentSource;
    return func;
  }
  
  static FunctionTemplate readFunctionCall() {
    skipEmptyCharacters();
    
    while(readSymbol('#')) {
      switch(readId()) {
        case "import":
          String name = readLine();
          importModule(name.contains(".") ? name : "modules/" + name
              + "/main.cas");            
          break;
        case "alias":
          name = readId();
          readExpectedSymbol('=');
          LinkedList<String> chunks = new LinkedList<>();
          while(true) {
            chunks.add(readId());
            if(!readSymbol('.')) break;
          }
          aliases.put(name, chunks);
          break;
      }
      skipEmptyCharacters();
    }
    
    Position startingPos = new Position();
    String id = readId();
    
    ParsingFunction parsingFunc = parsingFunctions.get(id);
    if(parsingFunc != null) return parsingFunc.create(startingPos);
    
    skipEmptyCharacters();
    if(textIndex == textLength) return null;
    char character = text.charAt(textIndex);
      
    switch(character) {
      case '{':
        return null;
      case '}':
        return null;
      case '(':
        textIndex++;
        UserFunctionTemplate func = readFunctionDeclaration();
        if(func != null) {
          func.name = id;
          userFunctions.put(id, func);
          return null;
        }
      default:
        startingPos.set();
        Chunk chunk = readChunkSequence().compile();
        FunctionTemplate call = chunk.call;
        if(call == null) error("Function call expected");
        return init(call, startingPos);
    }
  }
  
  static ParameterTemplate getParameter(LinkedList<ParameterTemplate> list
      , String parameterName) {
    return null;
  }
  
  static ParameterTemplate addLocalVariable(String id) {
    for(ParameterTemplate parameter : currentFunction.parameters) {
      if(parameter.name.equals(id)) return parameter;
    }
    
    ParameterTemplate param = currentFunction.localVariables.get(id);
    if(param != null) return param;
    
    param = new ParameterTemplate(0, id, false, null);
    currentFunction.localVariables.put(id, param);
    return param;
  }
  
  
  
  static UserFunctionTemplate readFunctionDeclaration() {
    final Position startingPos = new Position();
    final LinkedList<ParameterTemplate> parameters = new LinkedList<>();
    int index = -1;
    while(true) {
      String varId = readId();
      boolean isThis = false;
      if(!varId.isEmpty()) {
        if(varId.equals("this")) {
          isThis = true;
          readExpectedSymbol('.');
          varId = readId();
        }
        
        ObjectTemplate defaultValue = null;
        if(readSymbol('=')) {
          if("=<>".indexOf(text.charAt(textIndex)) >= 0) break;
          defaultValue = readFunction();
        }
        
        index++;
        parameters.add(new ParameterTemplate(index, varId, isThis
            , defaultValue));
        
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

      UserFunctionTemplate oldFunction = currentFunction;
      UserFunctionTemplate func = new UserFunctionTemplate(parameters);
      currentFunction = func;
      init(func, startingPos);

      if(shortFunction) {
        skipEmptyCharacters();
        func.codeBlock = new CodeBlockTemplate();
        Position pos = new Position();
        FunctionTemplate ret = new FunctionTemplate(1, "return");
        ret.params[0] = readFunction();
        func.codeBlock.code.add(init(ret, pos));
      } else {
        func.codeBlock = readCodeBlock(false);
      }
      
      currentFunction = oldFunction;
      return func;
    }
    startingPos.set();
    return null;
  }
  
  
  
  static ObjectTemplate readFunction() {
    ObjectTemplate func = readChunkSequence().compile().value;
    return func;
  }
  
  static CreateObject readObject(ChunkSequence sequence
      , ObjectTemplate[] constructorParams) {
    CreateObject creator = new CreateObject(constructorParams);
    while(true) {
      String fieldId = readId();

      if(readSymbol('(')) {
        // object function or constructor
        UserFunctionTemplate func = readFunctionDeclaration();
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
              UserFunctionTemplate func = readFunctionDeclaration();
              if(func != null) {
                sequence.addObject(func);
                continue;
              }
              
              // function inside brackets like "(a + b)"
              ObjectTemplate object = readFunction();
              FunctionTemplate func2 = (FunctionTemplate) object;
              if(func2 != null) func2.inBrackets = true;
              sequence.addObject(object);
              readExpectedSymbol(')');
              continue;
            }
            
            ObjectTemplate[] params
                = readParams().toArray(new ObjectTemplate[0]);
            
            // function / constructor call
            FunctionTemplate func = readSymbol('{') ? readObject(sequence
                , params) : new FunctionTemplate("params");
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
              LinkedList<ObjectTemplate> values = new LinkedList<>();
              while(true) {
                ObjectTemplate value = readFunction();
                if(value == null) {
                  readExpectedSymbol(']');
                  sequence.addObject(new CreateListTemplate(values));
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
      
      if(!id.isEmpty()) {
        sequence.add(id);
        id = "";
      }
      
      if(sequence.last != null && sequence.last.separator == null) {
        return sequence;
      }
      
      if(character >= '0' && character <= '9') {
        boolean negative = false;
        Chunk last = sequence.last;
        String separator = last == null ? null : last.separator;
        if(separator != null && separator.endsWith("-")) {
          if(separator.length() > 1) {
            last.separator = separator.substring(0, separator.length() - 1);
            negative = true;
          } else if(last.prevChunk == null) {
            sequence.remove(last);
            negative = true;
          }
        }
        sequence.addObject(readNumber(negative));
      } else {
        id = readId();
      }
    }
  }
  
  static void skipEmptyCharacters() {
    while(true) {  
      char character;
      if(textIndex == textLength) return;
      character = text.charAt(textIndex);
      
      switch(character) {
        case '\n':
          currentLine++;
          lineStart = textIndex;
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
  
  static String readLine() {
    int startIndex = textIndex;
    while(true) {
      if(textIndex == textLength) break;
      char character = text.charAt(textIndex);
      if(character == '\n') break;
      textIndex++;
    }
    return text.substring(startIndex, textIndex).trim();
  }
  
  static ObjectTemplate readNumber(boolean negative) {
    int startIndex = textIndex;
    while(true) {
      if(textIndex == textLength) break;
      char character = text.charAt(textIndex);
      if(character < '0' || character > '9') break;
      textIndex++;
    }
    IntegerTemplate integer = new IntegerTemplate(Integer.parseInt(
        text.substring(startIndex, textIndex)) * (negative ? -1 : 1));
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
            sequence.addObject(new StringTemplate(prefix + text.substring(startIndex
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
    sequence.addObject(new StringTemplate(prefix + text.substring(startIndex
        , textIndex)));
    textIndex++;
  }
  
  public static void error(String message) {
    if(textIndex == textLength) message = "Reached end of file while " + message;
    Base.parserError(message + " in line " + currentLine + " column "
        + (textIndex - lineStart) + " of \"" + currentSource.fileName + "\"");
  }
}
