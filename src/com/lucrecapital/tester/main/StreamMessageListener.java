/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester.main;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public interface StreamMessageListener
{

  public void onString (String str, String prefix);
}
