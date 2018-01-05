package casplan;


import casplan.target.Target.ChunkFunction;
import casplan.template.*;
import casplan.vm.Command;
import external.*;
import java.util.HashMap;
import java.util.LinkedList;

  
public class ChunkSequence extends Parser {
  public static final HashMap<String, Command> external = new HashMap<>();
  static final ChunkSequence empty = new ChunkSequence();
  
  public static void addFunction(String address, Command command) {
    external.put(address, command);
    functionAddresses.put(command.getClass(), address);
  }
  
  static {
    addFunction("math.randomInteger", new RandomInteger());
    addFunction("dialog.enterString", new EnterString());
    addFunction("dialog.showMessage", new ShowMessage());
  }
  
  static class Chunk {
    public static Chunk empty = new Chunk(null, null, null, null);
    
    String id, separator;
    ObjectTemplate value;
    FunctionTemplate call, separatorFunc = null;
    Chunk prevChunk, nextChunk;
    int line, column;
    
    public Chunk(String id, String separator, ObjectTemplate value
        , FunctionTemplate call) {
      this.id = id;
      this.separator = separator;
      this.value = value;
      this.call = call;
      this.line = Parser.currentLine;
      this.column = Parser.textIndex - Parser.lineStart;
    }
    
    public ObjectTemplate getValue() {
      return id == null ? value : addLocalVariable(id);
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
          + column + " of \"" + currentSource.fileName + "\"");
    }
  }

  Chunk first, last = null;
  int length = 0;
  
  void add(String id) {
    switch(id) {
      case "null":
        addObject(target.nullObject);
        return;
      case "this":
        addObject(target.thisObject);
        return;
      case "List":
        addObject(new CreateListTemplate(null));
        return;
    }
    
    // resolving aliases
    if(last == null || last.separator == null || !last.separator.equals(".")) {
      LinkedList<String> alias = aliases.get(id);
      if(alias != null) {
        boolean notFirst = false;
        for(String aliasChunk : alias) {
          if(notFirst) add('.');
          add(new Chunk(aliasChunk, null, null, null));
          notFirst = true;
        }
        return;
      }
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

  void addObject(ObjectTemplate value) {
    add(new Chunk(null, null, value, null));
  }

  void addCall(FunctionTemplate call) {
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
  
  void remove(Chunk chunk) {
    Chunk chunk2 = first;
    while(chunk2 != chunk) {
      if(chunk == null) return;
      chunk = chunk.nextChunk;
    }
    if(chunk.prevChunk == null) {
      first = chunk.nextChunk;
    } else {
      chunk.prevChunk.nextChunk = chunk.nextChunk;
    }
    if(chunk.nextChunk == null) {
      last = chunk.prevChunk;
    } else {
      chunk.nextChunk.prevChunk = chunk.prevChunk;
    }
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
      first.value = addLocalVariable(first.id);
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
        Chunk newChunk = new Chunk(null, null
            , new ExternalFunctionTemplate(address), null);
        replace(chunk, adLength, newChunk);
        chunk = newChunk;
      }
      chunk = chunk.nextChunk;
    }
      
    // resolving separators and filling priority array
    chunk = first;
    while(chunk != null) {
      if(chunk.separator != null) {
        ChunkFunction chunkFunction = target.chunkFunctions.get(chunk.separator);
        if(chunkFunction == null) {
          chunk.error("Invalid separator");
          System.exit(1);
        }
        if(chunkFunction.priority >= 0) {
          boolean increment = chunkFunction.separator.equals("++");
          chunk.separatorFunc = new FunctionTemplate(increment ? 1 : 2
              , chunkFunction);
          priority[chunkFunction.priority] = true;
        }
      }
      chunk = chunk.nextChunk;
    }

    for(int n = 17; n >= 3; n--) {
      if(n == 4) processTernaryOperator(first);
      if(!priority[n]) continue;
      chunk = first.nextChunk;
      while(chunk != null) {
        FunctionTemplate value = chunk.separatorFunc;
        if(value != null && value.func.getPriority() == n) {
          value.line = chunk.line;
          value.column = chunk.column;
          value.source = currentSource;
          value.params[0] = n == 3 ? (chunk.prevChunk.id == null
              ? chunk.prevChunk.value : addLocalVariable(chunk.prevChunk.id))
              : chunk.prevChunk.getValue();

          if(value.params.length > 1) {
            value.params[1] = n == 17 && chunk.nextChunk.id != null 
              ? Field.get(chunk.nextChunk.id) : chunk.nextChunk.getValue();
          }

          Chunk newChunk = new Chunk(null, null, value, value);
          replace(chunk.prevChunk, value.params.length == 1 ? 2 : 3, newChunk);
          chunk = newChunk;
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

        FunctionTemplate conditional = new FunctionTemplate(3, "?");
        conditional.params[0] = chunk.prevChunk.getValue();
        conditional.params[1] = chunk.nextChunk.getValue();
        conditional.params[2] = semicolonChunk.nextChunk.getValue();
        replace(chunk.prevChunk, 5, new Chunk(null, null, conditional, null));
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

