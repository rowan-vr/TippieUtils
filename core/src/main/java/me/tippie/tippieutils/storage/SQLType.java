package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.storage.impl.H2Impl;
import me.tippie.tippieutils.storage.impl.MySQLImpl;

public enum SQLType {
    H2(H2Impl.class),
    MySQL(MySQLImpl.class);



    final Class<? extends SQLTypeImplementation> implClass;

    SQLType(Class<? extends SQLTypeImplementation> implClass) {
        this.implClass = implClass;
    }
}
