/*
 * This inspired from of https://github.com/LuckPerms/LuckPerms/blob/master/common/src/main/java/me/lucko/luckperms/common/storage/implementation/sql/connection/hikari/HikariConnectionFactory.java
 * licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */

package me.tippie.tippieutils.storage.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import me.tippie.tippieutils.dependencies.Dependency;
import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.dependencies.relocations.Relocation;
import me.tippie.tippieutils.storage.SQLTypeImplementation;
import me.tippie.tippieutils.storage.StorageCredentials;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public abstract class AbstractHikariImpl implements SQLTypeImplementation {
    private final Set<Dependency> DEPENDENCIES = Set.of(Dependency.of(
            "com{}zaxxer",
            "HikariCP",
            "5.1.0",
            "pHpu5iN5aU7lLDADbwkxty+a7iqAHVkDQe2CvYOeITQ=",
            Dependency.Properties.builder().autoload(true).build(),
            Relocation.of("hikari", "com{}zaxxer{}hikari")));

    protected StorageCredentials cred;
    private final String defaultPort;
    private HikariDataSource hikari;

    protected abstract void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password);

    @Override
    public void init(StorageCredentials cred, DependencyManager dependencyManager) {
        this.cred = cred;
        dependencyManager.loadDependencies(DEPENDENCIES);

        HikariConfig config = new HikariConfig();

        String[] addressSplit = this.cred.getAddress().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : defaultPort;

        configureDatabase(config, address, port, this.cred.getDatabase(), this.cred.getUsername(), this.cred.getPassword());

        for (Map.Entry<String, String> property : this.cred.getProperties().entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }

        config.setMaximumPoolSize(this.cred.getMaxPoolSize());
        config.setMinimumIdle(this.cred.getMinIdleConnections());
        config.setMaxLifetime(this.cred.getMaxLifetime());
        config.setKeepaliveTime(this.cred.getKeepAliveTime());
        config.setConnectionTimeout(this.cred.getConnectionTimeout());

        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public void close() {
        this.hikari.close();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }
}
