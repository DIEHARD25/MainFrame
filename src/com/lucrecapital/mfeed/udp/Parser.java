/*
 Copyright (c) 2011, Lucre capital
 All rights reserved.
 *  This software is a property of Lucre Capital LLC.
 *  Any usage of this code requires a direct permision from the owner.
 *  contact legal@lucrecapital.com for questions on usage.
 */
package com.lucrecapital.mfeed.udp;

import com.lucrecapital.mfeed.parser.EventListener;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicReference;
import com.lucrecapital.mfeed.events.PriceLevel;
import com.lucrecapital.mfeed.events.PriceLevels;
import com.lucrecapital.mfeed.events.Quote;
import org.apache.log4j.Logger;

/**
 *
 * @author kham
 */
final class Parser implements PacketListener
{

  AtomicReference<PriceLevels> plsA;
  Quote quote = new Quote();
  PriceLevel plTmp;
  PriceLevel pl = new PriceLevel();
  private long seqNum = 0;
  private final EventListener eventListener;
  private String group = null;
  private Logger log;

  public Parser (EventListener listener, String ip)
  {
    this.eventListener = listener;
    plsA = new AtomicReference<PriceLevels>(new PriceLevels());
    this.log = Logger.getLogger(String.format("Parser[%s]", ip));
    this.group = ip;
  }

  @Override
  final public void onPacket (byte[] data, int length)
  {
    ByteBuffer buf = ByteBuffer.wrap(data, 0, length);
    buf.order(ByteOrder.LITTLE_ENDIAN);
    long newSeqNum = buf.getLong();

    if (newSeqNum > seqNum + 1 && seqNum != 0)
    {
      String msg = "Gap detected: " + (newSeqNum - seqNum - 1) + " packets were lost!";
      log.info(msg);
    }
    seqNum = newSeqNum;
    int currentPos = 8;
    int finalLength = length - 1; //
    int buyLvlCounter = 1;
    int sellLvlCounter = 1;
    PriceLevels pls = plsA.get();
    while (currentPos < finalLength)
    {
      int len = buf.getShort();
      currentPos += 2;
      byte type = buf.get();
      currentPos += 1;
      currentPos += len - 1;
      switch (type)
      {
        case 10:
        {
          pl.parsePL(buf);
          pl.margin = 0.0;

          if (pl.side == 'B')
          {
            pl.level = buyLvlCounter++;
            plTmp = pls.buyBook[pls.buyPos];
            pls.buyBook[pls.buyPos] = pl;
            pl = plTmp;
            pls.buyPos++;
          }
          else
          {
            pl.level = sellLvlCounter++;
            plTmp = pls.sellBook[pls.sellPos];
            pls.sellBook[pls.sellPos] = pl;
            pl = plTmp;
            pls.sellPos++;
          }
          break;
        }

        case 126:
          eventListener.onFeedStop(group);
          break;
        default:
          if ((type > 120) && (type < 130))
          {
            log.info("unknown message type " + type);
          }
          buf.position(currentPos);
          break;
      }
    }
    if ((pls.buyPos > 0) || (pls.sellPos > 0))
    {

      pls.sellBook[pls.sellPos].side = PriceLevels.END_OF_BOOK_MARKER;
      pls.buyBook[pls.buyPos].side = PriceLevels.END_OF_BOOK_MARKER;
      plsA.set(eventListener.onPriceLevels(pls));
      pls.setMsgSeqNum(seqNum);
    }
  }
}
