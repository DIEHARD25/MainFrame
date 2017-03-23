/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester.fix;

import org.apache.log4j.Logger;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.MessageCracker;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class FIX42Initiator extends MessageCracker implements Application {

    private static final Logger LOG = Logger.getLogger("FIXInitiator");
    private Initiator initiator;
    private Initiators.MDFullSnapshotListener mdSnapshotFullRefreshHandler;
    private int id;

    public FIX42Initiator(String config, int myID) {
        try {
            MessageStoreFactory storeFactory = new MemoryStoreFactory();
            SessionSettings settings = new SessionSettings(config);
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            initiator = new ThreadedSocketInitiator(this, storeFactory, settings, logFactory, messageFactory);
            this.id = myID;
        } catch (ConfigError e) {
            LOG.error(null, e);
        }
    }

    public void start() {
        try {
            initiator.start();
        } catch (ConfigError ex) {
            LOG.error(null, ex);
        } catch (RuntimeError ex) {
            LOG.error(null, ex);
        }
    }

    public void stop() {
        initiator.stop(true);
    }

    public void setMessageHandler(Initiators.MDFullSnapshotListener fixMessageListener) {
        this.mdSnapshotFullRefreshHandler = fixMessageListener;
    }

    @Override
    public void onCreate(SessionID sid) {
    }

    @Override
    public void onLogon(SessionID sid) {
    }

    @Override
    public void onLogout(SessionID sid) {
    }

    @Override
    public void toAdmin(Message msg, SessionID sid) {
    }

    @Override
    public void fromAdmin(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
    }

    @Override
    public void toApp(Message msg, SessionID sid) throws DoNotSend {
    }

    @Override
    public void fromApp(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        crack(msg, sid);
    }

    @Override
    public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        if (mdSnapshotFullRefreshHandler != null) {
            mdSnapshotFullRefreshHandler.invoke(id, sessionID, message);
        }
    }
}
