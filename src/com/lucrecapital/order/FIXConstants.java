package com.lucrecapital.order;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

public class FIXConstants extends TwoArgFunction
{
// --------------------------------------------------------------------------------------------------------------------
  public LuaValue call (LuaValue modname, LuaValue environment)
  {
    LuaValue library = tableOf();
    library.set("MsgType",       new MsgTypeConstants());
    library.set("Side",          new SideConstants());
    library.set("OrdType",       new OrdTypeConstants());
    library.set("OrdStatus",     new OrdStatusConstants());
    library.set("ExecType",      new ExecTypeConstants());
    library.set("ExecTransType", new ExecTransTypeConstants());
    library.set("TimeInForce",   new TimeInForceConstants());
    environment.set("FIX", library);
    return library;
  }


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class MsgTypeConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public MsgTypeConstants()
    {
      table.set("ORDER_SINGLE",                 MsgType.ORDER_SINGLE);
      table.set("ORDER_CANCEL_REQUEST",         MsgType.ORDER_CANCEL_REQUEST);
      table.set("ORDER_CANCEL_REPLACE_REQUEST", MsgType.ORDER_CANCEL_REPLACE_REQUEST);
      table.set("ORDER_CANCEL_REJECT",          MsgType.ORDER_CANCEL_REJECT);
      table.set("EXECUTION_REPORT",             MsgType.EXECUTION_REPORT);
      table.set("MARKET_DATA_SNAPSHOT_FULL_REFRESH", MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class SideConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public SideConstants()
    {
      table.set("BUY",  Side.BUY);
      table.set("SELL", Side.SELL);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class OrdTypeConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public OrdTypeConstants()
    {
      table.set("LIMIT",  OrdType.LIMIT);
      table.set("MARKET", OrdType.MARKET);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class OrdStatusConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public OrdStatusConstants()
    {
      table.set("DONE_FOR_DAY",     OrdStatus.DONE_FOR_DAY);
      table.set("EXPIRED",          OrdStatus.EXPIRED);
      table.set("FILLED",           OrdStatus.FILLED);
      table.set("NEW",              OrdStatus.NEW);
      table.set("PARTIALLY_FILLED", OrdStatus.PARTIALLY_FILLED);
      table.set("REJECTED",         OrdStatus.REJECTED);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class ExecTypeConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public ExecTypeConstants()
    {
      table.set("DONE_FOR_DAY", ExecType.DONE_FOR_DAY);
      table.set("EXPIRED",      ExecType.EXPIRED);
      table.set("FILL",         ExecType.FILL);
      table.set("NEW",          ExecType.NEW);
      table.set("PARTIAL_FILL", ExecType.PARTIAL_FILL);
      table.set("REJECTED",     ExecType.REJECTED);
      table.set("TRADE",        ExecType.TRADE);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class ExecTransTypeConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public ExecTransTypeConstants()
    {
      table.set("CANCEL",  ExecTransType.CANCEL);
      table.set("CORRECT", ExecTransType.CORRECT);
      table.set("NEW",     ExecTransType.NEW);
      table.set("STATUS",  ExecTransType.STATUS);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };


// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
  class TimeInForceConstants extends ZeroArgFunction
  {
    private LuaTable table = new LuaTable();

// --------------------------------------------------------------------------------------------------------------------
    public TimeInForceConstants()
    {
      table.set("AT_THE_CLOSE",        TimeInForce.AT_THE_CLOSE);
      table.set("AT_THE_OPENING",      TimeInForce.AT_THE_OPENING);
      table.set("DAY",                 TimeInForce.DAY);
      table.set("FILL_OR_KILL",        TimeInForce.FILL_OR_KILL);
      table.set("GOOD_TILL_CANCEL",    TimeInForce.GOOD_TILL_CANCEL);
      table.set("GOOD_TILL_CROSSING",  TimeInForce.GOOD_TILL_CROSSING);
      table.set("GOOD_TILL_DATE",      TimeInForce.GOOD_TILL_DATE);
      table.set("IMMEDIATE_OR_CANCEL", TimeInForce.IMMEDIATE_OR_CANCEL);

      table.set("DAY", TimeInForce.DAY);
      table.set("FOK", TimeInForce.FILL_OR_KILL);
      table.set("GTC", TimeInForce.GOOD_TILL_CANCEL);
      table.set("GTD", TimeInForce.GOOD_TILL_DATE);
      table.set("IOC", TimeInForce.IMMEDIATE_OR_CANCEL);
    }

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call()
    {
      return table;
    }
  };
};
