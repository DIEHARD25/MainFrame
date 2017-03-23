/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.listener;

import com.lucrecapital.mfeed.listener.MFeedListenerController;
import java.io.IOException;
import com.lucrecapital.mfeed.parser.EventListener;
import com.lucrecapital.mfeed.events.PriceLevel;
import com.lucrecapital.mfeed.events.PriceLevels;
import com.lucrecapital.mfeed.events.Quote;
import com.lucrecapital.mfeed.events.Trade;
import com.lucrecapital.mfeed.udp.UDPParser;
import org.apache.log4j.Logger;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class MFeedListener implements EventListener
{

  private UDPParser parser;
  private Logger log;
  private final int id;
  private MFeedListenerController.StopHandler stopHanler;
  private MFeedListenerController.PlHandler plHandler;

  public MFeedListener (String ip, int port, int id)
  {
    this.id = id;
    this.log = Logger.getLogger(String.format("MFeedListener[%s:%d]", ip, port));
    try
    {
      this.parser = new UDPParser(ip, port, this);
    }
    catch (IOException ex)
    {
      log.error(null, ex);
    }
  }

  @Override
  public void onQuote (Quote quote)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void onTrade (Trade trade)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void onPriceLevel (PriceLevel priceLevel)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public PriceLevels onPriceLevels (PriceLevels priceLevels)
  {
    plHandler.invoke(id, priceLevels);
    priceLevels.buyPos = 0;
    priceLevels.sellPos = 0;
    return priceLevels;
  }

  @Override
  public void onFeedStop (String group)
  {
    stopHanler.invoke(id);
  }

  public void start ()
  {
    parser.start();
  }

  public void stop ()
  {
    parser.stop();
  }

  public void setHandler (MFeedListenerController.PlHandler plHandler)
  {
    this.plHandler = plHandler;
  }

  public void setHandler (MFeedListenerController.StopHandler stopHandler)
  {
    this.stopHanler = stopHandler;
  }
}
