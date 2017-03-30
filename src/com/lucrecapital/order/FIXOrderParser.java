package com.lucrecapital.order;

import com.lucrecapital.lp.log.RotatingFileLogFactory;
import quickfix.Acceptor;
import quickfix.Application;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketAcceptor;
import quickfix.ThreadedSocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.fix42.MessageCracker;

public class FIXOrderParser extends MessageCracker implements Application
{
  private static final boolean DEBUG = !false;

  private int id = 0;

  private Initiator    initiator = null;
  private Acceptor     acceptor  = null;
  private OrderHandler handler   = null;

// --------------------------------------------------------------------------------------------------------------------
  public FIXOrderParser (String config, int id, boolean isInitiator, boolean isAcceptor)
  {
    this.id = id;
    try
    {
      SessionSettings     settings       = new SessionSettings(config);
      MessageStoreFactory storeFactory   = new FileStoreFactory(settings);
      LogFactory          logFactory     = new RotatingFileLogFactory(settings);
      MessageFactory      messageFactory = new DefaultMessageFactory();

      initiator = isInitiator ?
                  new ThreadedSocketInitiator(this, storeFactory, settings, logFactory, messageFactory) : null;
      acceptor  = isAcceptor  ?
                  new ThreadedSocketAcceptor (this, storeFactory, settings, logFactory, messageFactory) : null;
    }
    catch (Exception x)
    {
      System.out.println("parser init exception: " + x);
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  public void start()
  {
    try
    {
      if (acceptor  != null) acceptor.start();
      if (initiator != null) initiator.start();
    }
    catch (Exception x)
    {
      System.out.println("parser start exception: " + x);
    }
  }

// --------------------------------------------------------------------------------------------------------------------
  public void stop()
  {
    if (acceptor  != null) acceptor.stop(true);
    if (initiator != null) initiator.stop(true);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void setHandler (OrderHandler handler)
  {
    this.handler = handler;
  }

// --------------------------------------------------------------------------------------------------------------------
  public OrderHandler getHandler()
  {
    return handler;
  }

// --------------------------------------------------------------------------------------------------------------------
  public void onCreate (SessionID sid)
  {
    if (handler != null) handler.sessionID = sid;
  }

// --------------------------------------------------------------------------------------------------------------------
  public void onLogon (SessionID sid)
  {
    if (DEBUG) System.out.println("Logon: " + sid);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void onLogout (SessionID sid)
  {
    if (DEBUG) System.out.println("Logout: " + sid);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void toAdmin (Message message, SessionID sid)
  {
    if (DEBUG) System.out.println("To admin: " + sid + " " + message);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void fromAdmin (Message message, SessionID sid)
    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
  {
    if (DEBUG) System.out.println("From admin: " + sid + " " + message);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void toApp (Message message, SessionID sid) throws DoNotSend
  {
    if (false) System.out.println("To app: " + sid + " " + message);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void fromApp (Message message, SessionID sid)
    throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
  {
    if (false) System.out.println("From app: " + sid + " " + message);

    crack(message, sid);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void onMessage (quickfix.fix42.NewOrderSingle message, SessionID sessionID)
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
  {
    if (handler != null) handler.receiveOrder(id, sessionID, message);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void onMessage (quickfix.fix42.OrderCancelRequest message, SessionID sessionID)
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
  {
    if (handler != null) handler.receiveCancel(id, sessionID, message);
  }

// --------------------------------------------------------------------------------------------------------------------
  public void onMessage (quickfix.fix42.ExecutionReport message, SessionID sessionID)
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
  {
    if (handler != null) handler.receiveExecution(id, sessionID, message);
  }
  
  public void onMessage (quickfix.fix42.MarketDataSnapshotFullRefresh message, SessionID sessionID)
    throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
  {
    if (handler != null) handler.receiveFullSnapshot(id, sessionID, message);
  }
};
