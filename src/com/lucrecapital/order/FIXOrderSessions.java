package com.lucrecapital.order;

import java.util.HashMap;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.tableOf;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class FIXOrderSessions extends TwoArgFunction
{
  private HashMap<Integer, FIXOrderParser> parsers = new HashMap<Integer, FIXOrderParser>();

// --------------------------------------------------------------------------------------------------------------------
  public LuaValue call (LuaValue modname, LuaValue environment)
  {
    LuaValue library = tableOf();
    library.set("createInitiator", new Create(true));
    library.set("createAcceptor",  new Create(false));
    library.set("start",           new Start());
    library.set("stop",            new Stop());
    library.set("send",            new Send());
    environment.set("orderSession", library);
    return library;
  }

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class Create extends TwoArgFunction
  {
    private boolean isInitiator = false;

// --------------------------------------------------------------------------------------------------------------------
    public Create (boolean isInitiator)
    {
      this.isInitiator = isInitiator;
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call (LuaValue configFile, LuaValue function)
    {
      int id = 0;
      while (parsers.containsKey(id)) id++;

      System.out.println("create: " + id + " " + configFile + " " + function);
      FIXOrderParser parser = new FIXOrderParser(configFile.toString(), id, isInitiator, !isInitiator);
      parser.setHandler(new OrderHandler((LuaFunction)function));
      parsers.put(id, parser);
      return LuaValue.valueOf(id);
    }
  }

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class Start extends OneArgFunction
  {
// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call (LuaValue id)
    {
      System.out.println("start: " + id);
      FIXOrderParser parser = parsers.get(id.toint());
      parser.start();
      return LuaValue.NIL;
    }
  }

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class Stop extends OneArgFunction
  {
// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call (LuaValue id)
    {
      System.out.println("stop: " + id);
      FIXOrderParser parser = parsers.get(id.toint());
      if (parser != null) parser.stop();
      return LuaValue.NIL;
    }
  }

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class Send extends TwoArgFunction
  {
// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call (LuaValue id, LuaValue message)
    {
      FIXOrderParser parser = parsers.get(id.toint());
      if (parser != null && parser.getHandler() != null) parser.getHandler().send(message);
      return LuaValue.NIL;
    }
  }
};
