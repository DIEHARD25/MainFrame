/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.generator;

import com.lucrecapital.mfeed.events.PriceLevels;
import com.lucrecapital.mfeed.events.PriceLevel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import org.apache.log4j.Logger;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class MFeedGenerator
{

  private final InetSocketAddress downstreamAddr;
  private final DatagramChannel downstreamChannel;
  public static final int DOWNSTREAM_MAX_MSG_SIZE = 1460;
  public static final int BOOK_SIZE = 10;
  private long sequenceNumber = 0;
  ByteBuffer bb;
  private Logger log;

  public MFeedGenerator (String address, int port) throws IOException
  {
    log = Logger.getLogger(String.format("MFeedGenerator[%s:%d]", address, port));
    downstreamAddr = new InetSocketAddress(address, port);
    downstreamChannel = DatagramChannel.open();
    downstreamChannel.connect(downstreamAddr);
    bb = ByteBuffer.allocateDirect(DOWNSTREAM_MAX_MSG_SIZE);
    bb.order(ByteOrder.LITTLE_ENDIAN);
  }

  public void sendBook (PriceLevels levels) throws IOException
  {
    // Prepare book
    try
    {
      bb.putLong(sequenceNumber++);
      int lid = 0;
      lid = fillBook(bb, 'S', levels.getSymbol(), levels.sellBook, levels.sellPos, lid);
      lid = fillBook(bb, 'B', levels.getSymbol(), levels.buyBook, levels.buyPos, lid);
      bb.limit(bb.position());
      // Send book
      bb.rewind();
      downstreamChannel.send(bb, downstreamAddr);
    }
    finally
    {
      bb.clear();
    }
  }

  private int fillBook (ByteBuffer buffer, char side, String symbol, PriceLevel[] levels, int bookSize, int startLevel)
  {

    int lid = startLevel;
    for (int i = 0; i < bookSize; i++)
    {
      PriceLevel level = levels[i];
      buffer.putShort((short) 38);
      buffer.put((byte) 10);
      long ts = System.currentTimeMillis();
      int tss = (int) (ts / 1000);
      short mss = (short) (ts - tss);
      buffer.putInt(tss);
      buffer.putShort(mss);
      buffer.put(levels[i].exchange[0]);
      buffer.put(level.exchange[1]);
      byte[] symb = new byte[15];
      System.arraycopy(symbol.getBytes(), 0, symb, 0, symbol.length());
      buffer.put(symb);
      buffer.putDouble(level.getPrice());
      buffer.putInt(level.getSize());
      buffer.put((byte) side);
      lid++;
      if (side == 'B')
      {
        buffer.put((byte) (97 + lid));
      }
      else
      {
        buffer.put((byte) (110 + lid));
      }
    }
    return lid;
  }

  public void stop () throws IOException
  {
    downstreamChannel.close();
  }

  public boolean start ()
  {
    log.debug("Started");
    return true;
  }
}
