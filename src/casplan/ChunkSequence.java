package casplan;

import casplan.function.object.CreateList;
import casplan.object.*;
import casplan.function.*;
import casplan.function.operator.*;
import casplan.function.object.*;
import casplan.function.bool.*;
import external.function.object.CreateWindow;
import external.function.object.LoadTexture;
import java.util.HashMap;

  
public class ChunkSequence extends Base {
  static final HashMap<String, Function> separators = new HashMap<>();
  public static final HashMap<String, Function> external = new HashMap<>();
  static final ChunkSequence empty = new ChunkSequence();
  
  public static void addFunction(String address, Function function) {
    external.put(address, function);
    functionAddresses.put(function.getClass(), address);
  }
  
  static {
    addFunction("texture.load", new LoadTexture());
    addFunction("gui.window", new CreateWindow());
    
    separators.put(">=", new MoreOrEqual());
    separators.put("==", new Equal());
    separators.put("!=", new NotEqual());
    separators.put("<=", new LessOrEqual());
    separators.put("&&", new And());
    separators.put("+", new Addition());
    separators.put("-", new Subtraction());
    separators.put("*", new Multiplication());
    separators.put("/", new Division());
    separators.put("<", new LessThan());
    separators.put(">", new MoreThan());
    separators.put("=", new SetVariable());
    separators.put("++", new IncrementVariable());
    separators.put(".", new ObjectField());
    separators.put("[", new ItemAtIndex());
    separators.put("(", new FunctionCall());
    separators.put("..<", new Range());
  }
  
  static class Chunk {
    public static Chunk empty = new Chunk(null, null, null, null);
    
    String id, separator;
    CasObject value;
    Function call, separatorFunc = null;
    Chunk prevChunk, nextChunk;
    int line, column;
    
    public Chunk(String id, String separator, CasObject value, Function call) {
      this.id = id;
      this.separator = separator;
      this.value = value;
      this.call = call;
      this.line = Parser.currentLine;
      this.column = Parser.textIndex - Parser.lineStart;
    }
    
    public CasObject getValue() {
      return id == null ? value : getVariable(id);
    }
    
    @Override
    public String toString() {
      if(id != null) return id;
      if(separator != null) return separator;
      if(value != null) return value.toString();
      return "(error)";
    }
    
    public void error(String message) {
      Base.parserError(message + " in line " + line + " column "
          + column);
    }
  }

  Chunk first, last = null;
  int length = 0;

  void add(String id) {
    switch(id) {
      case "null":
        addObject(Null.instance);
        return;
      case "this":
        addObject(This.instance);
        return;
      case "List":
        addObject(new CreateList(null));
        return;
      case "stop":
        addObject(new Stop());
        return;
    }
    add(new Chunk(id, null, null, null));
  }

  void add(char separator) {
    if(last != null && last.separator != null) {
      last.separator += String.valueOf(separator);
    } else {
      add(new Chunk(null, String.valueOf(separator), null, null));
    }
  }

  void addObject(CasObject value) {
    add(new Chunk(null, null, value, null));
  }

  void addCall(Function call) {
    add(new Chunk(null, null, call, call));
  }

  void add(Chunk chunk) {
    length++;
    if(last == null) {
      first = chunk;
    } else {
      last.nextChunk = chunk;
      chunk.prevChunk = last;
    }
    last = chunk;  
  }

  void replace(Chunk startingChunk, int quantity, Chunk newChunk) {
    Chunk endingChunk = startingChunk;
    for(int n = 1; n < quantity; n++) endingChunk = endingChunk.nextChunk;
    if(startingChunk == first) {
      first = newChunk;
    } else {
      startingChunk.prevChunk.nextChunk = newChunk;
    }
    if(endingChunk == last) {
      last = newChunk;
    } else {
      endingChunk.nextChunk.prevChunk = newChunk;
    }
    newChunk.prevChunk = startingChunk.prevChunk;
    newChunk.nextChunk = endingChunk.nextChunk;
  }

  boolean isEmpty() {
    return last == null;
  }

  Chunk chunkAt(int index) {
    Chunk chunk = first;
    for(int i = 1; i < index; i++) chunk = chunk.nextChunk;
    return chunk;
  }

  boolean idEquals(int index, String id) {
    return chunkAt(index).id.equals(id);
  }

  Chunk compile(int from) {
    for(int i = 1; i < from; i++) first = first.nextChunk;
    first.prevChunk = null;
    return compile();
  }

  Chunk compile() {
    if(last == null) return Chunk.empty;
    if(first == last && first.id != null) {
      first.value = getVariable(first.id);
      return first;
    }
    boolean[] priority = new boolean[18];

    // resolving addresses for external functions
    Chunk chunk = first;
    while(chunk != null) {
      if(chunk.id != null && chunk.id.equals("external")) {
        String address = "";
        int adLength = 1;
        Chunk chunk2 = chunk.nextChunk;
        while(chunk2 != null) {
          if(!chunk2.separator.equals(".")) break;
          chunk2 = chunk2.nextChunk;
          if(chunk2.id == null) {
            chunk2.error("External address identifier expected");
          }
          address += (address.isEmpty() ? "" : ".") + chunk2.id;
          adLength += 2;
          chunk2 = chunk2.nextChunk;
        }
        CasObject object = external.get(address);
        if(object == null) {
          chunk.error("External address \"" + address + "\" is not found");
        }
        Chunk newChunk = new Chunk(null, null, object, object.toFunction());
        replace(chunk, adLength, newChunk);
        chunk = newChunk;
      }
      chunk = chunk.nextChunk;
    }
      
    // resolving separators and filling priority array
    chunk = first;
    while(chunk != null) {
      if(chunk.id == null) {
        chunk.separatorFunc = separators.get(chunk.separator);
        if(chunk.separatorFunc != null) {
          priority[chunk.separatorFunc.getPriority()] = true;
        }
      }
      chunk = chunk.nextChunk;
    }

    for(int n = 17; n >= 3; n--) {
      if(n == 4) processTernaryOperator(first);
      if(!priority[n]) continue;
      chunk = first.nextChunk;
      while(chunk != null) {
        if(chunk.separatorFunc != null
            && chunk.separatorFunc.getPriority() == n) {
          try {
            Function value = chunk.separatorFunc.getClass().newInstance();
            value.line = chunk.line;
            value.column = chunk.column;
            boolean increment = chunk.separator.equals("++");
            value.params = new CasObject[increment ? 1 : 2];
            value.params[0] = n == 3 ? (chunk.prevChunk.id == null
                ? chunk.prevChunk.value : getVariable(chunk.prevChunk.id))
                : chunk.prevChunk.getValue();
            if(!increment) {
              value.params[1] = n == 17 && chunk.nextChunk.id != null 
                ? Field.get(chunk.nextChunk.id) : chunk.nextChunk.getValue();
            }

            Chunk newChunk = new Chunk(null, null, value, value);
            replace(chunk.prevChunk, increment ? 2 : 3, newChunk);
            chunk = newChunk;
          } catch (InstantiationException ex) {
            chunk.error("Instantiation exception");
          } catch (IllegalAccessException ex) {
            chunk.error("Illegal access exception");
          }
        }
        chunk = chunk.nextChunk;
      }
    }

    if(first != last) first.error("Invalid syntax");

    return first;
  }

  void processTernaryOperator(Chunk startingChunk) {
    Chunk chunk = startingChunk.nextChunk;
    while(chunk != null) {
      if(chunk.separator != null && chunk.separator.equals("?")) {
        processTernaryOperator(chunk);
        Chunk semicolonChunk = chunk.nextChunk.nextChunk;
        if(!semicolonChunk.separator.equals(":")) {
          semicolonChunk.error("\":\" expected");
        }

        Chunk newChunk = new Chunk(null, null
            , new Conditional(chunk.prevChunk.getValue()
            , chunk.nextChunk.getValue()
            , semicolonChunk.nextChunk.getValue()), null);
        replace(chunk.prevChunk, 5, newChunk);
      }
      chunk = chunk.nextChunk;
    }
  }

  @Override
  public String toString() {
    Chunk chunk = first;
    String str = "";
    while(chunk != null) {
      if(!str.isEmpty()) str += " ";
      str += chunk.toString();
      chunk = chunk.nextChunk;
    }
    return str;
  }
}

