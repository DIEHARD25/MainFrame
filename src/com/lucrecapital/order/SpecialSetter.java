package com.lucrecapital.order;

import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;


/**
 *
 * @author koleschenko
 */


public class SpecialSetter extends LuaTable {

    public void set(int key, LuaTable value) {
        if (m_metatable == null || !rawget(key).isnil() || !settable(this, LuaInteger.valueOf(key), value)) {
            rawset(key, value);
        }
    }

}
