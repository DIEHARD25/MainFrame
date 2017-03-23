/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class InputStreamHandler implements Runnable
{

  private static volatile long counter = 0;
  private final long id;
  private InputStream myStream;
  private Thread myThread;
  private AbstractStreamHandler handler;
  private volatile boolean running;
  private BufferedReader reader;
  private static final Logger LOG = Logger.getLogger("InputStreamHandler");

  public InputStreamHandler (InputStream is, AbstractStreamHandler handler)
  {
    this.myStream = is;
    this.handler = handler;
    this.id = counter++;
//        LOG.debug("My ID " + id);
  }

  public void start ()
  {
    LOG.debug("Starting InputStreamHandler");
    myThread = new Thread(this);
    running = true;
    reader = new BufferedReader(new InputStreamReader(myStream));
    myThread.start();
  }

  public void stop ()
  {
//        LOG.debug("Stop called");
    try
    {
      while (reader.ready())
      {
        TimeUnit.MILLISECONDS.sleep(100);
      }
    }
    catch (IOException e)
    {
      LOG.error(null, e);
    }
    catch (InterruptedException e)
    {
      LOG.error(null, e);
    }
    running = false;
    myThread.interrupt();
  }

  @Override
  public void run ()
  {
    try
    {
      while (running || reader.ready())
      {
        try
        {
          String line = reader.readLine();
          if (line != null)
          {
            handler.onLine(line, this);
          }
        }
        catch (IOException e)
        {
          LOG.error(null, e);
        }
      }
//            LOG.debug("Stopped InputStreamHandler");
    }
    catch (IOException e)
    {
      LOG.error(null, e);
    }
  }

  @Override
  public int hashCode ()
  {
    int hash = 7;
    hash = 67 * hash + (int) (this.id ^ (this.id >>> 32));
    return hash;
  }

  @Override
  public boolean equals (Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final InputStreamHandler other = (InputStreamHandler) obj;
    if (this.id != other.id)
    {
      return false;
    }
    return true;
  }
}
