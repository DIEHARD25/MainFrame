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
public class Trade extends Event
{

  private double price = 0;
  private int size = 0;
  private long volume = 0;
  private int seqNum = 0;
  private char indicator = 0;
  private char tickIndicator = 0;
  private byte flags1 = 0;
  private byte flags2 = 0;

  public Trade (ByteBuffer event)
  {
    super(event);
    price = buffer.getDouble();
    size = buffer.getInt();
    volume = buffer.getLong();
    seqNum = buffer.getInt();
    indicator = (char) buffer.get();
    tickIndicator = (char) buffer.get();
    flags1 = buffer.get();
    flags2 = buffer.get();
  }

  public double getPrice ()
  {
    return price;
  }

  public int getSize ()
  {
    return size;
  }

  public long getVolume ()
  {
    return volume;
  }

  public int getSeqNum ()
  {
    return seqNum;
  }

  public char getIndicator ()
  {
    return indicator;
  }

  public char getTickIndicator ()
  {
    return tickIndicator;
  }

  public byte getFlags1 ()
  {
    return flags1;
  }

  public byte getFlags2 ()
  {
    return flags2;
  }

  @Override
  public String toString ()
  {
    return "Trade: " + super.toString() + " price=" + price
           + " size=" + size + " volume=" + volume + " seqNum=" + seqNum
           + " indicator=" + indicator + " tickIndicator=" + tickIndicator
           + " flags1=" + flags1 + " flags2=" + flags2;
  }
}
