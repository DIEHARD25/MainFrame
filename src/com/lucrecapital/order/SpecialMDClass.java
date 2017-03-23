package com.lucrecapital.order;

import quickfix.FieldNotFound;
import quickfix.field.MDEntryID;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

/**
 *
 * @author koleschenko
 */


public class SpecialMDClass extends MarketDataSnapshotFullRefresh.NoMDEntries {

    public MDEntryID getMDEntryID() throws FieldNotFound {
        quickfix.field.MDEntryID value = new quickfix.field.MDEntryID();
        getField(value);

        return value;
    }
}
