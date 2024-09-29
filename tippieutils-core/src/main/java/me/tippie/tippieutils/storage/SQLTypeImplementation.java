package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.storage.SQLStorage;
import me.tippie.tippieutils.storage.StorageCredentials;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLTypeImplementation {
    void init(StorageCredentials cred, DependencyManager dependencyManager);

    void close();

    Connection getConnection() throws SQLException;
}
