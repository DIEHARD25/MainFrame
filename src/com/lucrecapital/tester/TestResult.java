/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class TestResult
{

  private boolean failed = true;
  private String message;

  public void fail (String msg)
  {
    failed = true;
    message = msg;
  }

  public void success (String msg)
  {
    message = msg;
    failed = false;
  }

  public String getMessage ()
  {
    return message;
  }

  public boolean isSuccessful ()
  {
    return !failed;
  }
}
