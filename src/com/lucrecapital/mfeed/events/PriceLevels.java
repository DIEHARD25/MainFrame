/*
 Copyright (c) 2011, Lucre capital
 All rights reserved.
 *  This software is a property of Lucre Capital LLC.
 *  Any usage of this code requires a direct permision from the owner.
 *  contact legal@lucrecapital.com for questions on usage.
 */
package com.lucrecapital.mfeed.events;

import org.luaj.vm2.LuaValue;

/**
 *
 * @author Alex Spodinets <aspodinets@lucrecapital.com>
 */
public final class PriceLevels
{

  public static final char END_OF_BOOK_MARKER = 'T';
  public PriceLevel buyBook[] = new PriceLevel[defDepth];
  public PriceLevel sellBook[] = new PriceLevel[defDepth];
  public int buyPos;
  public int sellPos;
  public boolean isNew = false;
  private static final int defDepth = 30;

  public PriceLevels ()
  {
    for (int i = 0; i < buyBook.length; i++)
    {
      buyBook[i] = new PriceLevel();
    }

    for (int i = 0; i < sellBook.length; i++)
    {
      sellBook[i] = new PriceLevel();
    }
  }

  public final String getSymbol ()
  {
    if (buyPos > 0)
    {
      return buyBook[0].getSymbol();
    }
    else if (sellPos > 0)
    {
      return sellBook[0].getSymbol();
    }

    return "NOTHING!";//Cannot return NULL, just make sure it's not a currency pair
  }

  @Override
  public String toString ()
  {
    StringBuilder sb = new StringBuilder("Snapshot: " + getSymbol() + "; ");
    sb.append("Sell\n");
    for (int i = 0; i < sellPos; i++)
    {
      sb.append(sellBook[i].size + "@" + sellBook[i].price + "\n");
    }
    sb.append("Buy\n");
    for (int i = 0; i < buyPos; i++)
    {
      sb.append(buyBook[i].size + "@" + buyBook[i].price + "\n");
    }
    return sb.toString();

  }

  public String getExchangeCode ()
  {
    if (buyPos > 0)
    {
      return buyBook[0].getExchange();
    }
    else if (sellPos > 0)
    {
      return sellBook[0].getExchange();
    }

    return "--";//Cannot return NULL, just make sure it's not a currency pair
  }
}
