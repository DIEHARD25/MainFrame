/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.generator;

import com.lucrecapital.mfeed.events.PriceLevels;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.tableOf;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class MFeedController extends TwoArgFunction
{

  private List<MFeedGenerator> generators = new ArrayList<MFeedGenerator>();
  private static final Logger LOG = Logger.getLogger("MFeedController");

  class CreateGenerator extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue address, LuaValue luaPort)
    {
      int result = -1;
      String groupAddress = address.toString();
      int port = luaPort.toint();
      LOG.debug(String.format("Creating MCastGenerator[%s:%d]", groupAddress, port));
      try
      {
        MFeedGenerator generator = new MFeedGenerator(groupAddress, port);
        generators.add(generator);
        result = generators.indexOf(generator);
      }
      catch (IOException e)
      {
        LOG.error(null, e);
      }
      return LuaInteger.valueOf(result);
    }
  }

  class StartGenerator extends OneArgFunction
  {

    @Override
    public LuaValue call (LuaValue arg)
    {
      return LuaBoolean.valueOf(generators.get(arg.toint()).start());
    }
  }

  class SendSnapshot extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue genID, LuaValue snap)
    {
      boolean result = false;
      try
      {
        LuaTable snapshot = (LuaTable) snap;
        String symbol = snapshot.get("symbol").toString();
        byte[] exchange = snapshot.get("exchange").toString().getBytes();
        PriceLevels levels = new PriceLevels();
        LuaTable sellBook = (LuaTable) snapshot.get("sellbook");
        LuaTable buyBook = (LuaTable) snapshot.get("buybook");

        levels.sellPos = sellBook.length();
        for (int i = 0; i < sellBook.length(); i++)
        {
          LuaTable lvl = (LuaTable) sellBook.get(i);
          levels.sellBook[i].price = lvl.get("price").todouble();
          levels.sellBook[i].size = lvl.get("size").toint();
          levels.sellBook[i].setSymbol(symbol);
          levels.sellBook[i].exchange = exchange;
        }

        levels.buyPos = buyBook.length();
        for (int i = 0; i < buyBook.length(); i++)
        {
          LuaTable lvl = (LuaTable) buyBook.get(i);
          levels.buyBook[i].price = lvl.get("price").todouble();
          levels.buyBook[i].size = lvl.get("size").toint();
          levels.buyBook[i].setSymbol(symbol);
          levels.buyBook[i].exchange = exchange;
        }

        //            while (arg.)
        generators.get(genID.toint()).sendBook(levels);
//                System.out.println("Sent snapshot" + levels);
        result = true;
      }
      catch (Exception e)
      {
      }
      return LuaBoolean.valueOf(result);
    }
  }

  class StopGenerator extends OneArgFunction
  {

    @Override
    public LuaValue call (LuaValue arg)
    {
      boolean result = false;
      try
      {
        generators.get(arg.toint()).stop();
        result = true;
      }
      catch (IOException e)
      {
      }
      return LuaBoolean.valueOf(result);
    }
  }

  /**
   * Lua code to load library functions
   *
   * @param arg1
   * @param arg2
   * @return
   */
  @Override
  public LuaValue call (LuaValue modname, LuaValue env)
  {
    LuaValue library = tableOf();
    library.set("create", new CreateGenerator());
    library.set("start", new StartGenerator());
    library.set("send", new SendSnapshot());
    library.set("stop", new StopGenerator());
    env.set("mgen", library);
    return library;

  }
}
