package me.tippie.tippieutils.storage.impl;

import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.storage.SQLStorage;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLTypeImplementation {
    void init(String url, String username, String password, DependencyManager dependencyManager);

    Connection getConnection(String url, String username, String password, DependencyManager dependencyManager) throws SQLException;
}
