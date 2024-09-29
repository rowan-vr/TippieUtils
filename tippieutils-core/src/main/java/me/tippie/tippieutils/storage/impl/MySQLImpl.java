package me.tippie.tippieutils.storage.impl;

import com.zaxxer.hikari.HikariConfig;
import me.tippie.tippieutils.dependencies.Dependency;
import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.dependencies.relocations.Relocation;
import me.tippie.tippieutils.storage.StorageCredentials;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;
import java.sql.Driver;

public class MySQLImpl extends AbstractHikariImpl {
    private final Set<Dependency> DEPENDENCIES = Set.of(Dependency.of("mysql",
            "mysql-connector-java",
            "8.0.23",
            "/31bQCr9OcEnh0cVBaM6MEEDsjjsG3pE6JNtMynadTU=",
            Dependency.Properties.builder().autoload(true).build(),
            Relocation.of("mysql", "com{}mysql")));

    public MySQLImpl() {
        super("3306");
    }

    @Override
    public void init(StorageCredentials cred, DependencyManager dependencyManager) {
        dependencyManager.loadDependencies(DEPENDENCIES);
        cred = cred.toBuilder()
                .propertyIfAbsent("cachePrepStmts", "true")
                .propertyIfAbsent("prepStmtCacheSize", "250")
                .propertyIfAbsent("prepStmtCacheSqlLimit", "204800")
                .propertyIfAbsent("useServerPrepStmts", "true")
                .propertyIfAbsent("useLocalSessionState", "true")
                .propertyIfAbsent("rewriteBatchedStatements", "true")
                .propertyIfAbsent("cacheResultSetMetadata", "true")
                .propertyIfAbsent("cacheServerConfiguration", "true")
                .propertyIfAbsent("elideSetAutoCommits", "true")
                .propertyIfAbsent("maintainTimeStats", "false")
                .propertyIfAbsent("alwaysSendSetIsolation", "false")
                .propertyIfAbsent("cacheCallableStmts", "true")
                .build();
        super.init(cred, dependencyManager);

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getName().equals("com.mysql.cj.jdbc.Driver")) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    protected void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password) {
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + databaseName);
        config.setUsername(username);
        config.setPassword(password);
    }
}
