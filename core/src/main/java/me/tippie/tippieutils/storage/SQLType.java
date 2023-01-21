package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.storage.impl.H2Impl;
import me.tippie.tippieutils.storage.impl.SQLTypeImplementation;

public enum SQLType {
    H2(new H2Impl());



    final SQLTypeImplementation implementation;

    SQLType(SQLTypeImplementation implementation) {
        this.implementation = implementation;
    }
}
