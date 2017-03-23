/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.tester.main;

import com.lucrecapital.tester.AbstractStreamHandler;
import com.lucrecapital.tester.Test;
import com.lucrecapital.tester.TestResult;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

/**
 *
 * @author Stanislav Kogut <skogut@sistyma.net>
 */
public class Main implements StreamMessageListener
{
  public static void main (String[] args) throws IOException, InterruptedException
  {
    Main m = new Main();

    File tests = new File("ltests");
    File[] environments = tests.listFiles(new FileFilter()
    {
      @Override
      public boolean accept (File pathname)
      {
        return pathname.isDirectory();
      }
    });

    ExecuteWatchdog watchdog = new ExecuteWatchdog(30);
    for (final File environment : environments)
    {
      System.out.println("Found test environment " + environment);

      CommandLine cl = CommandLine.parse("./startup.sh");
      DefaultExecutor executor = new DefaultExecutor();
      executor.setWorkingDirectory(environment);
      executor.setWatchdog(watchdog);
      executor.setStreamHandler(new AbstractStreamHandler()
      {
        @Override
        public void onErrorLine (String line)
        {
          System.out.println("ERROR>>>" + line);
        }

        @Override
        public void onOutputLine (String line)
        {
          System.out.println("OUTPUT>>" + line);
        }
      });
      executor.execute(cl, new ExecuteResultHandler()
               {
                 @Override
                 public void onProcessComplete (int exitValue)
                 {
                   System.out.println("Testing application exited with exit value " + exitValue);
                 }

                 @Override
                 public void onProcessFailed (ExecuteException e)
                 {
                   e.printStackTrace();
                 }
      });
      TimeUnit.SECONDS.sleep(1);
      System.out.println("Testing application started, beginning tests");
      File testDir = new File(environment, "tests");
      System.out.println(testDir);
      if (testDir.exists())
      {
        File[] list = testDir.listFiles(new FileFilter()
        {
          @Override
          public boolean accept (File pathname)
          {
            return pathname.getName().matches(".*\\.lua");
          }
        });
        Arrays.sort(list);
        for (File test : list)
        {
          System.out.println("Running test script: " + test);
          LuaValue lv = JsePlatform.standardGlobals();
          lv.get("dofile").call(test.getPath());
          System.out.println("Script " + test + " done");
        }
      }

//            System.out.println("Done");
    }

    System.out.println("/======================================================================================\\");
    System.out.println("||                              Test results:                                         ||");
    System.out.println("========================================================================================");
    System.out.printf("||%30s||%10s||%-40s||\n", "Name", "Success?", "Message");
    System.out.println("========================================================================================");
    for (Map.Entry<String, TestResult> result : Test.getTests().entrySet())
    {
      System.out.printf("||%30s||%10s||%-40s||\n", result.getKey(), result.getValue().isSuccessful(), result.getValue().getMessage());
    }
    System.out.println("\\======================================================================================/");
    // Find directory with tests
    // Load/inderx any "modules" in here.
    // Select every environment
    System.exit(0);
  }

  @Override
  public void onString (String prefix, String str)
  {
    System.out.println(prefix + str);
  }
}
