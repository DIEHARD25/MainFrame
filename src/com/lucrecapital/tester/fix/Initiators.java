/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester.fix;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.tableOf;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MDEntrySize;
import quickfix.field.NoMDEntries;
import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class Initiators extends TwoArgFunction {

    private Map<Integer, FIX42Initiator> initiators = new HashMap<Integer, FIX42Initiator>();
    private static final Logger LOG = Logger.getLogger("Initiators");
    private int idCounter = 0;

    class MDFullSnapshotListener {

        private LuaFunction handler;

        public MDFullSnapshotListener(LuaFunction handler) {
            this.handler = handler;
        }

        public void invoke(int id, SessionID sid, MarketDataSnapshotFullRefresh message) throws FieldNotFound {
            try {
                LuaValue[] args = new LuaValue[3];

                LuaTable msg = new LuaTable();

                msg.set(Symbol.FIELD, message.getString(Symbol.FIELD));

                MarketDataSnapshotFullRefresh.NoMDEntries entries = new MarketDataSnapshotFullRefresh.NoMDEntries();
                int count = message.getNoMDEntries().getValue();

                for (int i = 1; i <= count; i++) {
                    message.getGroup(i, entries);
                    msg.set(100, LuaValue.valueOf(entries.get(new MDEntrySize()).getValue()));
                }

                args[0] = LuaValue.valueOf(id);
                args[1] = LuaValue.valueOf(sid.toString());
                args[2] = msg;
                handler.invoke(args);
            } catch (Exception e) {
                LOG.error(null, e);
            }
        }
    }

    class Create extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue configFile) {
            System.out.println(configFile);
            int id = idCounter++;
            FIX42Initiator i = new FIX42Initiator(configFile.toString(), id);
            initiators.put(id, i);
            return LuaValue.valueOf(id);
        }
    }

    class Start extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue id) {
            FIX42Initiator i = initiators.get(id.toint());
            i.start();
            return LuaValue.NIL;
        }
    }

    class Stop extends OneArgFunction {

        @Override
        public LuaValue call(LuaValue id) {
            FIX42Initiator i = initiators.get(id.toint());
            i.stop();
            return LuaValue.NIL;
        }
    }

    class SetMDSnapshotFullRefreshHandler extends TwoArgFunction {

        @Override
        public LuaValue call(LuaValue initiator, LuaValue function) {
            int id = initiator.toint();
            FIX42Initiator i = initiators.get(id);
            i.setMessageHandler(new MDFullSnapshotListener((LuaFunction) function));
            return LuaValue.NIL;
        }
    }

    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaValue library = tableOf();
        library.set("create", new Create());
        library.set("start", new Start());
        library.set("stop", new Stop());
        library.set("set_md_snap_handler", new SetMDSnapshotFullRefreshHandler());
        env.set("initiator", library);
        return library;
    }

    public static void main(String[] args) throws InvalidMessage, FieldNotFound, ConfigError {
//        String msg = "8=FIX.4.2^A9=670^A35=W^A34=16^A49=GATE^A52=20130815-14:44:47.067^A56=Client01^A"
//                + "55=EUR/USD^A268=20^A269=B^A270=2.05^A271=2007^A290=1^A269=B^A270=2.04^A271=2007^A"
//                + "290=2^A269=B^A270=2.03^A271=2007^A290=3^A269=B^A270=2.02^A271=2007^A290=4^A269=B^A"
//                + "270=2.01^A271=2007^A290=5^A269=B^A270=2^A271=2007^A290=6^A269=B^A270=1.99^A271=2007^A"
//                + "290=7^A269=B^A270=1.98^A271=2007^A290=8^A269=B^A270=1.97^A271=2007^A290=9^A269=B^A"
//                + "270=1.96^A271=2007^A290=10^A269=S^A270=2^A271=2007^A290=1^A269=S^A270=1.99^A271=2007^A"
//                + "290=2^A269=S^A270=1.98^A271=2007^A290=3^A269=S^A270=1.97^A271=2007^A290=4^A269=S^A270=1.96^A"
//                + "271=2007^A290=5^A269=S^A270=1.95^A271=2007^A290=6^A269=S^A270=1.94^A271=2007^A290=7^A269=S^A"
//                + "270=1.93^A271=2007^A290=8^A269=S^A270=1.92^A271=2007^A290=9^A269=S^A270=1.91^A271=2007^A290=10^A10=241^A";
////        msg = "^A";
//        msg = msg.replaceAll("\\^A", "\1");
//        Message fixMsg = new MarketDataSnapshotFullRefresh();
//        fixMsg.fromString(msg, new DataDictionary("/tmp/FIX42.xml"), false);
//        Iterator<Field<?>> fields = fixMsg.iterator();
//        for (Field<?> field = fields.next(); fields.hasNext(); field = fields.next()) {
//            System.out.println("Found field " + field.toString());
//        }
//
//        List<Group> groups = fixMsg.getGroups(TINT)
//
//        Iterator<Integer> groupIterator = fixMsg.groupKeyIterator();
//        for (Integer key = groupIterator.next(); groupIterator.hasNext(); key = groupIterator.next()) {
//            System.out.println(fixMsg.getGroupCount(key));
//        }

        MarketDataSnapshotFullRefresh f = new MarketDataSnapshotFullRefresh(new Symbol("EUR/USD"));
        MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
        group.set(new MDEntrySize(10000000));
        f.addGroup(group);
        System.out.println(f.toString());
    }
}
