package com.lucrecapital.order;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.ClientID;
import quickfix.field.CumQty;
import quickfix.field.Currency;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.FutSettDate;
import quickfix.field.HandlInst;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MDEntryID;
import quickfix.field.MDEntryOriginator;
import quickfix.field.MDEntryPositionNo;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MinQty;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SecurityExchange;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix42.OrderCancelRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class OrderHandler
{
  private LuaFunction handler   = null;
  public  SessionID   sessionID = null;

// --------------------------------------------------------------------------------------------------------------------
  private static String getString (Message message, int field, String defaultValue)
  {
    try
    {
      return message.getString(field);
    }
    catch (Exception x)
    {
      return defaultValue;
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  private static char getChar (Message message, int field, char defaultValue)
  {
    try
    {
      return message.getChar(field);
    }
    catch (Exception x)
    {
      return defaultValue;
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  private static double getDouble (Message message, int field, double defaultValue)
  {
    try
    {
      return message.getDouble(field);
    }
    catch (Exception x)
    {
      return defaultValue;
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  public OrderHandler (LuaFunction handler)
  {
    this.handler = handler;
  }

// --------------------------------------------------------------------------------------------------------------------
  public void send (LuaValue table)
  {
    try
    {
      String type = table.get("msgType").toString();
      Message message = type.equals(MsgType.ORDER_SINGLE)         ? buildOrder(table)     :
                        type.equals(MsgType.ORDER_CANCEL_REQUEST) ? buildCancel(table)    :
                        type.equals(MsgType.EXECUTION_REPORT)     ? buildExecution(table) : 
                        null;
      if (message != null) quickfix.Session.sendToTarget(message, sessionID);
    }
    catch (Exception x)
    {
      System.out.println("handler send exception: " + x);
      x.printStackTrace();
    }
  }
  
    public void receiveFullSnapshot(int id, SessionID sid, MarketDataSnapshotFullRefresh message) {
        try {
            LuaTable order = new LuaTable();
            

            order.set("msgType", message.getHeader().getString(quickfix.field.MsgType.FIELD));
            order.set("symbol", getString(message, quickfix.field.Symbol.FIELD, ""));
            
                        
            NoMDEntries noMDEntries = new NoMDEntries();
            message.get(noMDEntries);
            
            order.set("noMDEntries", noMDEntries.getValue()); // 268
            System.out.print(noMDEntries.getValue());
            SpecialMDClass group = new SpecialMDClass();

            for (int i = 1; i <= noMDEntries.getValue(); i++) {
                SpecialSetter tmp = new SpecialSetter();
                message.getGroup(i, group);
               // group.get(MDEntryType);
                tmp.set("MDEntryType", group.getMDEntryType().getValue());
               // group.get(MDEntryPx);
                tmp.set("MDEntryPx", group.getMDEntryPx().getValue());
               // group.get(MDEntrySize);
                tmp.set("MDEntrySize", group.getMDEntrySize().getValue());
              //  group.get(MDEntryPositionNo);
                tmp.set("MDEntryPositionNo", group.getMDEntryPositionNo().getValue());
              //  group.get(MDEntryOriginator);
                tmp.set("MDEntryID", group.getMDEntryID().getValue());
                String tmp1 = group.getMDEntryID().getValue();
                order.set("snapshotNum", Long.parseLong(tmp1.substring(tmp1.indexOf("_") + 1),Character.MAX_RADIX));
                tmp.set("MDEntryOriginator", group.getMDEntryOriginator().getValue());
                order.set(i, tmp);
            }
            
            
            LuaValue[] args = {LuaValue.valueOf(id), LuaValue.valueOf(sid.toString()), order};
            handler.invoke(args);
        } catch (Exception x) {
            System.out.println("handler receive order exception: " + x);
            x.printStackTrace();
        }
    }
  
  
// --------------------------------------------------------------------------------------------------------------------
  public void receiveOrder (int id, SessionID sid, NewOrderSingle message) throws FieldNotFound
  {
    try
    {
      LuaTable order = new LuaTable();
      order.set("msgType",      message.getHeader().getString(quickfix.field.MsgType.FIELD));
      order.set("clOrdID",      getString(message, quickfix.field.ClOrdID.FIELD,     ""));
      order.set("symbol",       getString(message, quickfix.field.Symbol.FIELD,      ""));
      order.set("price",        getDouble(message, quickfix.field.Price.FIELD,       0.0));
      order.set("size",         getDouble(message, quickfix.field.OrderQty.FIELD,    0.0));
      order.set("orderQty",     getDouble(message, quickfix.field.OrderQty.FIELD,    0.0));
      order.set("side",         getChar  (message, quickfix.field.Side.FIELD,        Side.BUY));
      order.set("ordType",      getChar  (message, quickfix.field.OrdType.FIELD,     OrdType.LIMIT));
      order.set("transactTime", message.getTransactTime().getValue().getTime());
      order.set("currency",     getString(message, quickfix.field.Currency.FIELD,    ""));
      order.set("minSize",      getDouble(message, quickfix.field.MinQty.FIELD,      0.0));
      order.set("minQty",       getDouble(message, quickfix.field.MinQty.FIELD,      0.0));
      order.set("timeInForce",  getChar  (message, quickfix.field.TimeInForce.FIELD, TimeInForce.FILL_OR_KILL));
      order.set("clientID",     getString(message, quickfix.field.ClientID.FIELD,    ""));     
              
             

      LuaValue[] args = {LuaValue.valueOf(id), LuaValue.valueOf(sid.toString()), order};
      handler.invoke(args);
    }
    catch (Exception x)
    {
      System.out.println("handler receive order exception: " + x);
      x.printStackTrace();
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  public void receiveCancel (int id, SessionID sid, OrderCancelRequest message) throws FieldNotFound
  {
    try
    {
      LuaTable cancel = new LuaTable();
      cancel.set("msgType",      message.getHeader().getString(quickfix.field.MsgType.FIELD));
      cancel.set("origClOrdID",  getString(message, quickfix.field.OrigClOrdID.FIELD, ""));
      cancel.set("clOrdID",      getString(message, quickfix.field.ClOrdID.FIELD,     ""));
      cancel.set("symbol",       getString(message, quickfix.field.Symbol.FIELD,      ""));
      cancel.set("size",         getDouble(message, quickfix.field.OrderQty.FIELD,    0.0));
      cancel.set("orderQty",     getDouble(message, quickfix.field.OrderQty.FIELD,    0.0));
      cancel.set("side",         getChar  (message, quickfix.field.Side.FIELD,        Side.BUY));
      cancel.set("transactTime", message.getTransactTime().getValue().getTime());

      LuaValue[] args = {LuaValue.valueOf(id), LuaValue.valueOf(sid.toString()), cancel};
      handler.invoke(args);
    }
    catch (Exception x)
    {
      System.out.println("handler receive cancel exception: " + x);
      x.printStackTrace();
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  public void receiveExecution (int id, SessionID sid, ExecutionReport message) throws FieldNotFound
  {
    try
    {
      LuaTable report = new LuaTable();
      report.set("msgType",       message.getHeader().getString(quickfix.field.MsgType.FIELD));
      report.set("orderID",       getString(message, quickfix.field.OrderID.FIELD,       ""));
      report.set("execID",        getString(message, quickfix.field.ExecID.FIELD,        ""));
      report.set("clOrdID",       getString(message, quickfix.field.ClOrdID.FIELD,       ""));
      report.set("clientID",      getString(message, quickfix.field.ClientID.FIELD,      ""));
      report.set("account",       getString(message, quickfix.field.Account.FIELD,       ""));
      report.set("execTransType", getChar  (message, quickfix.field.ExecTransType.FIELD, ExecTransType.NEW));
      report.set("execType",      getChar  (message, quickfix.field.ExecType.FIELD,      ExecType.NEW));
      report.set("ordType",       getChar  (message, quickfix.field.OrdType.FIELD,       OrdType.LIMIT));
      report.set("ordStatus",     getChar  (message, quickfix.field.OrdStatus.FIELD,     OrdStatus.NEW));
      report.set("symbol",        getString(message, quickfix.field.Symbol.FIELD,        ""));
      report.set("price",         getDouble(message, quickfix.field.Price.FIELD,         0.0));
      report.set("size",          getDouble(message, quickfix.field.OrderQty.FIELD,      0.0));
      report.set("orderQty",      getDouble(message, quickfix.field.OrderQty.FIELD,      0.0));
      report.set("side",          getChar  (message, quickfix.field.Side.FIELD,          Side.BUY));
      report.set("currency",      getString(message, quickfix.field.Currency.FIELD,      ""));
      report.set("minSize",       getDouble(message, quickfix.field.MinQty.FIELD,        0.0));
      report.set("minQty",        getDouble(message, quickfix.field.MinQty.FIELD,        0.0));
      report.set("leavesQty",     getDouble(message, quickfix.field.LeavesQty.FIELD,     0.0));
      report.set("cumQty",        getDouble(message, quickfix.field.CumQty.FIELD,        0.0));
      report.set("avgPx",         getDouble(message, quickfix.field.AvgPx.FIELD,         0.0));
      report.set("lastQty",       getDouble(message, quickfix.field.LastQty.FIELD,       0.0));
      report.set("lastPx",        getDouble(message, quickfix.field.LastPx.FIELD,        0.0));
      report.set("timeInForce",   getChar  (message, quickfix.field.TimeInForce.FIELD,   TimeInForce.FILL_OR_KILL));
      report.set("futSettDate",   getString(message, quickfix.field.FutSettDate.FIELD,   ""));
      report.set("text",          getString(message, quickfix.field.Text.FIELD,          ""));
      
      LuaValue[] args = {LuaValue.valueOf(id), LuaValue.valueOf(sid.toString()), report};
      handler.invoke(args);
    }
    catch (Exception x)
    {
      System.out.println("handler receive execution exception: " + x);
      x.printStackTrace();
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  private NewOrderSingle buildOrder (LuaValue table)
  {
    String symbol = table.get("symbol").toString();
    NewOrderSingle message = new NewOrderSingle(new ClOrdID(table.get("clOrdID").toString()),
                                                new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC),
                                                new Symbol(symbol),
                                                new Side(table.get("side").tochar()),
                                                new TransactTime(),
                                                new OrdType(table.get("ordType").tochar()));
    LuaValue currency = table.get("currency");
    message.set(new Currency(currency != null && !currency.isnil() ?
                             currency.toString() : symbol.substring(0, symbol.indexOf('/'))));
    message.set(new Price(table.get("price").todouble()));

    LuaValue size = table.get("size");
    if (size == null || size.isnil()) size = table.get("orderQty");
    message.set(new OrderQty(size.todouble()));

    LuaValue minSize = table.get("minSize");
    if (minSize == null ||  minSize.isnil()) minSize = table.get("minQty");
    if (minSize != null && !minSize.isnil()) message.set(new MinQty(minSize.todouble()));

    message.set(new TimeInForce(table.get("timeInForce").tochar()));
    return message;
  }

// --------------------------------------------------------------------------------------------------------------------
  private OrderCancelRequest buildCancel (LuaValue table)
  {
    String symbol = table.get("symbol").toString();
    OrderCancelRequest message = new OrderCancelRequest(new OrigClOrdID(table.get("origClOrdID").toString()),
                                                        new ClOrdID(table.get("clOrdID").toString()),
                                                        new Symbol(symbol),
                                                        new Side(table.get("side").tochar()),
                                                        new TransactTime());
    LuaValue size = table.get("size");
    if (size == null || size.isnil()) size = table.get("orderQty");
    message.set(new OrderQty(size.todouble()));
    return message;
  }

// --------------------------------------------------------------------------------------------------------------------
  private ExecutionReport buildExecution (LuaValue table)
  {
    String symbol = table.get("symbol").toString();
    ExecutionReport message = new ExecutionReport(new OrderID(table.get("orderID").toString()),
                                                  new ExecID(table.get("execID").toString()),
                                                  new ExecTransType(table.get("execTransType").tochar()),
                                                  new ExecType(table.get("execType").tochar()),
                                                  new OrdStatus(table.get("ordStatus").tochar()),
                                                  new Symbol(symbol),
                                                  new Side(table.get("side").tochar()),
                                                  new LeavesQty(table.get("leavesQty").todouble()),
                                                  new CumQty(table.get("cumQty").todouble()),
                                                  new AvgPx(table.get("avgPx").todouble()));
    message.set(new ClOrdID(table.get("clOrdID").toString()));

    LuaValue size = table.get("size");
    if (size == null || size.isnil()) size = table.get("orderQty");
    message.set(new OrderQty(size.todouble()));

    message.set(new OrdType(table.get("ordType").tochar()));
    message.set(new TimeInForce(table.get("timeInForce").tochar()));

    LuaValue currency = table.get("currency");
    message.set(new Currency(currency != null && !currency.isnil() ?
                             currency.toString() : symbol.substring(0, symbol.indexOf('/'))));
    LuaValue minSize = table.get("minSize");
    if (minSize == null ||  minSize.isnil()) minSize = table.get("minQty");
    if (minSize != null && !minSize.isnil()) message.set(new MinQty(minSize.todouble()));

    LuaValue account = table.get("account");
    if (account != null && !account.isnil()) message.set(new Account(account.toString()));

    LuaValue price = table.get("price");
    if (price != null && !price.isnil()) message.set(new Price(price.todouble()));

    LuaValue lastQty = table.get("lastQty");
    if (lastQty != null && !lastQty.isnil()) message.setField(new LastQty(lastQty.todouble()));

    LuaValue lastPx = table.get("lastPx");
    if (lastPx != null && !lastPx.isnil()) message.set(new LastPx(lastPx.todouble()));

    LuaValue futSettDate = table.get("futSettDate");
    if (futSettDate != null && !futSettDate.isnil()) message.set(new FutSettDate(futSettDate.toString()));

    LuaValue text = table.get("text");
    if (text != null && !text.isnil()) message.set(new Text(text.toString()));
    
    LuaValue securityExchange = table.get("securityExchange");
    if (securityExchange != null && !securityExchange.isnil()) message.set(new SecurityExchange(securityExchange.toString()));
    
    LuaValue clientID = table.get("clientID");
    if (clientID != null && !clientID.isnil()) message.set(new ClientID(clientID.toString()));
        
    return message;
  }
};
