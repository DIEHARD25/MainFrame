/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.listener;

import java.util.HashMap;
import java.util.Map;
import com.lucrecapital.mfeed.events.PriceLevel;
import com.lucrecapital.mfeed.events.PriceLevels;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.tableOf;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class MFeedListenerController extends TwoArgFunction
{

  private Map<Integer, MFeedListener> listeners = new HashMap<Integer, MFeedListener>();
  private int counter = 0;

  class PlHandler
  {

    private LuaFunction function;

    public PlHandler (LuaFunction function)
    {
      this.function = function;
    }

    public void invoke (int id, PriceLevels levels)
    {
      LuaValue[] args = new LuaValue[2];
      args[0] = LuaValue.valueOf(id);
      LuaTable snapshot = new LuaTable();
      snapshot.set("symbol", levels.getSymbol());
      snapshot.set("exchange", levels.getExchangeCode());

      LuaTable sellBook = new LuaTable();
      for (int i = 0; i < levels.sellPos; i++)
      {
        LuaTable level = new LuaTable();
        PriceLevel pl = levels.sellBook[i];
        level.set("size", pl.size);
        level.set("price", pl.price);
        sellBook.set(i, level);
      }

      LuaTable buyBook = new LuaTable();
      for (int i = 0; i < levels.buyPos; i++)
      {
        LuaTable level = new LuaTable();
        PriceLevel pl = levels.buyBook[i];
        level.set("size", pl.size);
        level.set("price", pl.price);
        buyBook.set(i, level);
      }

      snapshot.set("sellbook", sellBook);
      snapshot.set("buybook", buyBook);

      args[1] = snapshot;
      function.invoke(args);
    }
  }

  class StopHandler
  {

    private LuaFunction function;

    public StopHandler (LuaFunction function)
    {
      this.function = function;
    }

    public void invoke (int id)
    {
      function.invoke();
    }
  }

  class CreateListener extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue ip, LuaValue port)
    {
      int id = counter++;
      MFeedListener listener = new MFeedListener(ip.toString(), port.toint(), id);
      listeners.put(id, listener);
      return LuaValue.valueOf(id);
    }
  }

  class StartListener extends OneArgFunction
  {

    @Override
    public LuaValue call (LuaValue id)
    {
      boolean result = false;
      MFeedListener listener = listeners.get(id.toint());
      if (listener != null)
      {
        result = true;
        listener.start();
      }
      return LuaValue.valueOf(result);
    }
  }

  class SetPlHandler extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue id, LuaValue function)
    {
      boolean result = false;
      MFeedListener listener = listeners.get(id.toint());
      if (listener != null)
      {
        result = true;
        listener.setHandler(new PlHandler((LuaFunction) function));
      }
      return LuaValue.valueOf(result);
    }
  }

  class SetStopHandler extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue id, LuaValue function)
    {
      boolean result = false;
      MFeedListener listener = listeners.get(id.toint());
      if (listener != null)
      {
        result = true;
        listener.setHandler(new StopHandler((LuaFunction) function));
      }
      return LuaValue.valueOf(result);
    }
  }

  class StopListener extends OneArgFunction
  {

    @Override
    public LuaValue call (LuaValue id)
    {
      boolean result = false;
      MFeedListener listener = listeners.get(id.toint());
      if (listener != null)
      {
        result = true;
        listener.stop();
      }
      return LuaValue.valueOf(result);
    }
  }

  @Override
  public LuaValue call (LuaValue modname, LuaValue env)
  {
    LuaValue library = tableOf();
    library.set("create", new MFeedListenerController.CreateListener());
    library.set("start", new MFeedListenerController.StartListener());
    library.set("set_pl_handler", new MFeedListenerController.SetPlHandler());
    library.set("set_stop_handler", new MFeedListenerController.SetStopHandler());
    library.set("stop", new MFeedListenerController.StopListener());
    env.set("mlist", library);
    return library;

  }
}
