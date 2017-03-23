/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.tableOf;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class Test extends TwoArgFunction
{

  private static Map<String, TestResult> tests = new LinkedHashMap<String, TestResult>();

  class setup extends OneArgFunction
  {

    @Override
    public LuaValue call (LuaValue testName)
    {
      tests.put(testName.toString(), new TestResult());
      return testName;
    }
  }

  class fail extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue testName, LuaValue message)
    {
      boolean result = false;
      TestResult test = tests.get(testName.toString());
      if (test != null)
      {
        test.fail(message.toString());
        result = true;
      }
      return LuaValue.valueOf(result);
    }
  }

  class success extends TwoArgFunction
  {

    @Override
    public LuaValue call (LuaValue testName, LuaValue message)
    {
      boolean result = false;
      TestResult test = tests.get(testName.toString());
      if (test != null)
      {
        test.success(message.toString());
        result = true;
      }
      return LuaValue.valueOf(result);
    }
  }

  @Override
  public LuaValue call (LuaValue modname, LuaValue env)
  {
    LuaValue library = tableOf();
    library.set("setup", new Test.setup());
    library.set("fail", new Test.fail());
    library.set("success", new Test.success());
    env.set("test", library);
    return library;
  }

  public static Map<String, TestResult> getTests ()
  {
    return Collections.unmodifiableMap(tests);
  }
}
