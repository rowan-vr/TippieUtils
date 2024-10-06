package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.storage.impl.H2Impl;
import me.tippie.tippieutils.storage.impl.MariaDBImpl;
import me.tippie.tippieutils.storage.impl.MySQLImpl;
import org.bukkit.configuration.ConfigurationSection;

public enum SQLType {
    H2(H2Impl.class),
    MySQL(MySQLImpl.class),
    MariaDB(MariaDBImpl.class);

    /**
     * Parse a {@link ConfigurationSection} as {@link StorageCredentials}
     * @param section the {@link ConfigurationSection} to parse
     * @return the resulting {@link StorageCredentials}
     */
    public static StorageCredentials parseConfiguration(ConfigurationSection section) {
        var builder = StorageCredentials.builder()
                .address(section.getString("address", "localhost:3306"))
                .database(section.getString("database","default"))
                .username(section.getString("username", "root"))
                .password(section.getString("password", ""))
                .maxPoolSize(section.getInt("options.maxpoolsize",10))
                .minIdleConnections(section.getInt("options.minidleconnections", 10))
                .maxLifetime(section.getInt("options.maxlifetime", 1800000))
                .keepAliveTime(section.getInt("options.keepalivetime", 0))
                .connectionTimeout(section.getInt("options.timeout", 5000));

        ConfigurationSection properties = section.getConfigurationSection("options.properties");
        if (properties != null) {
            for (String property : properties.getKeys(false)) {
                builder = builder.propertyIfAbsent(property, properties.getString(property));
            }
        }

        return builder.build();
    }


    final Class<? extends SQLTypeImplementation> implClass;

    SQLType(Class<? extends SQLTypeImplementation> implClass) {
        this.implClass = implClass;
    }
}
