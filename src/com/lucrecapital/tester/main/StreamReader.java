/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class StreamReader implements Runnable
{

  private InputStream inputStream;
  private boolean running = true;
  private StreamMessageListener listener;
  private BufferedReader reader;
  private static final Logger LOG = Logger.getLogger("StreamReader");
  private final String prefix;

  public StreamReader (InputStream stream, StreamMessageListener listener, String prefix)
  {
    this.prefix = prefix;
    this.inputStream = stream;
    reader = new BufferedReader(new InputStreamReader(stream));
    this.listener = listener;
  }

  @Override
  public void run ()
  {
    while (running)
    {
      try
      {
        String line = reader.readLine();
        if (line != null)
        {
          listener.onString(prefix, line);
        }
      }
      catch (IOException ex)
      {
        LOG.error(null, ex);
      }
    }
  }

  public void stop ()
  {
    this.running = false;
  }
}
