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
class Event
{

  private long sequenceNumber = 0;
  private int timestamp = 0;
  private short mills = 0;
  public byte[] exchange = new byte[2];
  private byte[] symbol = new byte[15];
  protected ByteBuffer buffer = null;
  private String exchangeStr;
  private String symbolStr;
  public long parseTime = 0;
  public int exchangeIndex;
  public double margin = 0.0; //Price inflation/deflation or margin value. Will be applied before price is sent to taker.
  public boolean isSpread = false; //Indicates if price event spread/margin value is final.

  protected Event ()
  {
  }

  protected Event (ByteBuffer event)
  {
    parseEvent(event);
  }

  protected final void parseEvent (ByteBuffer event)
  {
    buffer = event;
    //buffer.order(ByteOrder.LITTLE_ENDIAN);
    //buffer.get();
    timestamp = buffer.getInt();
    mills = buffer.getShort();
    buffer.get(exchange, 0, exchange.length);
    exchangeIndex = buffer.getShort(buffer.position() - 2);
    buffer.get(symbol, 0, symbol.length);
    exchangeStr = null;
    symbolStr = null;
    parseTime = System.nanoTime();
  }

  public final int getTimestamp ()
  {
    return timestamp;
  }

  public final short getMills ()
  {
    return mills;
  }

  public final String getExchange ()
  {
    if (exchangeStr == null)
    {
      exchangeStr = new String(exchange).trim();
    }

    return exchangeStr;
  }

  public final int getExchangeIndex ()
  {
    return exchangeIndex;
  }

  public final byte[] getExchangeByte ()
  {
    return exchange;
  }

  public final String getSymbol ()
  {
    if (symbolStr == null)
    {
      symbolStr = new String(symbol).trim();
    }

    return symbolStr;
  }

  public final byte[] getSymbolByte ()
  {
    return symbol;
  }

  @Override
  public String toString ()
  {
    return "" + timestamp + "." + mills + " " + getExchange() + "/" + getSymbol();
  }

  public final long getSequenceNumber ()
  {
    return sequenceNumber;
  }

  public final void setSequenceNumber (long sequenceNumber)
  {
    this.sequenceNumber = sequenceNumber;
  }

  public final void setSymbolByte (String pair)
  {
    this.symbol = pair.getBytes();
  }

  public final void setSymbol (String pair)
  {
    this.symbolStr = pair;
  }

  public final long getAge ()
  {
    return (System.nanoTime() - parseTime);
  }

  public final long getParseTime ()
  {
    return parseTime;
  }

  public final boolean exchangeEquals (byte[] ex)
  {
    if ((exchange[0] == ex[0]) && (exchange[1] == ex[1]))
    {
      return true;
    }
    else
    {
      return false;
    }
  }
}
