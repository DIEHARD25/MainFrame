/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester.main;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class TestLuaLib extends TwoArgFunction
{

  class hello extends OneArgFunction
  {

    @Override
    public LuaValue call (LuaValue arg)
    {
      System.out.println("Hello from java: " + arg + "!!!!");
      return NIL;
    }
  }

  @Override
  public LuaValue call (LuaValue modname, LuaValue env)
  {
    LuaValue library = tableOf();
    library.set("hello", new hello());
    env.set("test", library);
    return library;
  }
}
