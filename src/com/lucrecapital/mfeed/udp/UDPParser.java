/*
 * Copyright (c) 2011, Lucre capital
 * All rights reserved.
 *  This software is a property of Lucre Capital LLC.
 *  Any usage of this code requires a direct permision from the owner.
 *  contact legal@lucrecapital.com for questions on usage.
 */
package com.lucrecapital.mfeed.udp;

import com.lucrecapital.mfeed.parser.EventListener;
import java.io.IOException;

/**
 *
 * @author kham
 */
public class UDPParser
{

  private UDPReader reader = null;
  private Parser parser;

  public UDPParser (String host, int port, EventListener eventListener) throws IOException
  {
    parser = new Parser(eventListener, host);
    reader = new UDPReader(host, port, parser);
  }

  public void start ()
  {
    reader.start();
  }

  public void stop ()
  {
    reader.finish();
  }
}
