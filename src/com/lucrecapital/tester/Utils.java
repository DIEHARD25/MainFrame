package com.lucrecapital.tester;

import java.util.concurrent.TimeUnit;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import static org.luaj.vm2.LuaValue.tableOf;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

public class Utils extends TwoArgFunction {

    private static final long startMs = System.currentTimeMillis();
    private static final long startNs = System.nanoTime();

// --------------------------------------------------------------------------------------------------------------------
    public LuaValue call(LuaValue modname, LuaValue environment) {
        LuaValue library = tableOf();
        library.set("sleep", new Sleep());
        library.set("time", new Time());
        library.set("fulltime", new FullTime());
        library.set("nanotime", new Nanotime());
        library.set("exec", new Execute());
        environment.set("utils", library);
        return library;
    }

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
    class Sleep extends OneArgFunction {
// --------------------------------------------------------------------------------------------------------------------

        public LuaValue call(LuaValue arg) {
            try {
                TimeUnit.MILLISECONDS.sleep(arg.tolong());
            } catch (Exception x) {
            }

            return LuaValue.NIL;
        }
    };

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
    class Execute extends OneArgFunction {
// --------------------------------------------------------------------------------------------------------------------

        public LuaValue call(LuaValue arg) {
            LuaTable result = new LuaTable();
            try {
                final StringBuilder outText = new StringBuilder();
                final StringBuilder errText = new StringBuilder();
                CommandLine cl = CommandLine.parse(arg.toString());
                DefaultExecutor executor = new DefaultExecutor();
                executor.setStreamHandler(new AbstractStreamHandler() {
                    public void onErrorLine(String line) {
                        errText.append(line).append("\n");
                    }

                    public void onOutputLine(String line) {
                        outText.append(line).append("\n");
                    }
                });
                int execCode = executor.execute(cl);
                result.set("exitCode", execCode);
                result.set("stdOut", outText.toString());
                result.set("stdErr", errText.toString());
            } catch (ExecuteException x) {
                result.set("exitCode", x.getExitValue());
            } catch (Exception x) {
            }

            return result;
        }
    };

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
    class Time extends ZeroArgFunction {
// --------------------------------------------------------------------------------------------------------------------

        public LuaValue call() {
            return LuaValue.valueOf((long) (System.currentTimeMillis() - startMs));
        }
    };

// --------------------------------------------------------------------------------------------------------------------
// --------------------------------------------------------------------------------------------------------------------
    class Nanotime extends ZeroArgFunction {
// --------------------------------------------------------------------------------------------------------------------

        public LuaValue call() {
            return LuaValue.valueOf((long) (System.nanoTime() - startNs));
        }
    };

    class FullTime extends ZeroArgFunction {
// --------------------------------------------------------------------------------------------------------------------

        public LuaValue call() {
            return LuaValue.valueOf((long) (System.currentTimeMillis()));
        }
    };
// --------------------------------------------------------------------------------------------------------------------
}
