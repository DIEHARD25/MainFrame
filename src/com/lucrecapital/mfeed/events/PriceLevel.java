/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.events;

import java.nio.ByteBuffer;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public final class PriceLevel extends Event implements Comparable<PriceLevel>
{

  public double price;
  public int size;
  public char side;
  public int level;
  public String id;
  public boolean seen = false;
  private byte[] origExchange = new byte[2];

  public PriceLevel ()
  {
  }

  public PriceLevel (ByteBuffer event)
  {
    super(event);
    parsePL(event);
  }

  public final void parsePL (ByteBuffer event)
  {
    super.parseEvent(event);
    price = buffer.getDouble();
    size = buffer.getInt();
    side = (char) buffer.get();
    level = (int) buffer.get();
    // buffer.get(origExchange, 0, origExchange.length);
    /*if(System.getProperty("proto_version", "extended").equals("extended")) {

     }*/
  }

  public final void copy (PriceLevel pl)
  {
    this.price = pl.price;
    this.size = pl.size;
    this.side = pl.side;
    this.level = pl.level;
    System.arraycopy(pl.origExchange, 0, this.origExchange, 0, pl.origExchange.length);
  }

  public final byte getLevel (ByteBuffer event)
  {
    return buffer.get(14);
  }

  public final double getPrice ()
  {
    return price;
  }

  public final int getSize ()
  {
    return size;
  }

  public final char getSide ()
  {
    return side;
  }

  public final int getLevel ()
  {
    return level;
  }

  public final String getOrigExchange ()
  {
    return new String(origExchange).trim();
  }

  @Override
  public String toString ()
  {
    return "PriceLevel: " + super.toString() + " price=" + price
           + " size=" + size + " side=" + side + " level=" + level + " origExchange=" + getOrigExchange() + " parseTime=" + parseTime + " exchIdx:" + exchangeIndex;
  }

  /**
   * Local override, optimized for application needs. !!! Warning, this compareTo will only compare prices.
   *
   * @param o
   * @return
   */
  @Override
  public int compareTo (PriceLevel o)
  {
    if (o.price == this.price)
    {
      return 0;
    }
    else if (this.price > o.price)
    {
      return 1;
    }
    else
    {
      return -1;
    }
  }
}
