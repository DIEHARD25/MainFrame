/*
 *  Copyright (c) 2011, Lucre capital
 *  All rights reserved.
 *  This software is a property of Lucre Capital LLC.
 *  Any usage of this code requires a direct permision from the owner.
 *  contact legal@lucrecapital.com for questions on usage.
 *
 */
package com.lucrecapital.mfeed.parser;

import com.lucrecapital.mfeed.events.PriceLevel;
import com.lucrecapital.mfeed.events.PriceLevels;
import com.lucrecapital.mfeed.events.Quote;
import com.lucrecapital.mfeed.events.Trade;

/**
 *
 * @author kham
 */
public interface EventListener
{

  void onQuote (Quote quote);

  void onTrade (Trade trade);

  void onPriceLevel (PriceLevel priceLevel);

  PriceLevels onPriceLevels (PriceLevels priceLevels);

  void onFeedStop (String group);
}
