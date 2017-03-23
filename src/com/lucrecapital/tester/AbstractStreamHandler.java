/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.exec.ExecuteStreamHandler;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public abstract class AbstractStreamHandler implements ExecuteStreamHandler
{

  private InputStreamHandler stdOutHandler;
  private InputStreamHandler stdErrHandler;

  @Override
  public void setProcessInputStream (OutputStream os) throws IOException
  {
    //
  }

  @Override
  public void setProcessErrorStream (InputStream is) throws IOException
  {
    stdErrHandler = new InputStreamHandler(is, this);
  }

  @Override
  public void setProcessOutputStream (InputStream is) throws IOException
  {
    stdOutHandler = new InputStreamHandler(is, this);
  }

  @Override
  public void start () throws IOException
  {
    stdOutHandler.start();
    stdErrHandler.start();
  }

  @Override
  public void stop ()
  {
    stdOutHandler.stop();
    stdErrHandler.stop();
  }

  public void onLine (String line, InputStreamHandler handler)
  {
    if (handler.equals(stdOutHandler))
    {
      onOutputLine(line);
    }
    else
    {
      System.out.println("Not StdOut");
      onErrorLine(line);
    }
  }

  public abstract void onErrorLine (String line);

  public abstract void onOutputLine (String line);
}
