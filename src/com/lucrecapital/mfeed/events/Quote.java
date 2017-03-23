/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.events;

import java.nio.ByteBuffer;

/**
 *
 * @author kham
 */
public final class Quote extends Event
{

  public double bidPrice = 0;
  public int bidSize = 0;
  public double askPrice = 0;
  public int askSize = 0;
  private byte[] askExchange = new byte[2];
  private byte[] bidExchange = new byte[2];
  private char indicator = 0;
  private char tickIndicator = 0;

  public Quote ()
  {
  }

  public Quote (ByteBuffer event)
  {
    super(event);
    parseQuote(event);
  }

  private void copySome (Quote qt)
  {
    //super.copySome(qt)nice;
    bidPrice = qt.bidPrice;
    bidSize = qt.bidSize;
    askSize = qt.askSize;
    askPrice = qt.askPrice;
    /*askExchange=new byte[2];
     bidExchange=new byte[2];
     System.arraycopy(qt.askExchange, qt.askExchange.length, askExchange, 0, qt.askExchange.length);
     System.arraycopy(qt.bidExchange, qt.bidExchange.length, bidExchange, 0, qt.bidExchange.length);*/

    askExchange[0] = qt.askExchange[0];
    askExchange[1] = qt.askExchange[1];

    bidExchange[0] = qt.bidExchange[0];
    bidExchange[1] = qt.bidExchange[1];
  }

  public final void parseQuote (ByteBuffer event)
  {
    super.parseEvent(event);
    bidSize = buffer.getInt();
    askSize = buffer.getInt();
    bidPrice = buffer.getDouble();
    askPrice = buffer.getDouble();
    buffer.get(askExchange);
    indicator = (char) buffer.get();
    tickIndicator = (char) buffer.get();
    buffer.get(bidExchange);

  }

  public final double getBidPrice ()
  {
    return bidPrice;
  }

  public final int getBidSize ()
  {
    return bidSize;
  }

  public final double getAskPrice ()
  {
    return askPrice;
  }

  public final int getAskSize ()
  {
    return askSize;
  }

  public final String getAskExchange ()
  {
    return new String(askExchange).trim();
  }

  public final String getBidExchange ()
  {
    return new String(bidExchange).trim();
  }

  public final char getIndicator ()
  {
    return indicator;
  }

  public final char getTickIndicator ()
  {
    return tickIndicator;
  }

  @Override
  public String toString ()
  {
    return "Quote: " + super.toString() + " bidPrice=" + bidPrice
           + " bidSize=" + bidSize + " bidExchange=" + getBidExchange()
           + " askPrice=" + askPrice + " askSize=" + askSize + " askExchange=" + getAskExchange()
           + " indicator=" + indicator + " tickIndicator=" + tickIndicator;
  }
}
