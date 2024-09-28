package me.tippie.tippieutils.storage.impl;

import com.google.common.collect.ImmutableSet;
import lombok.NoArgsConstructor;
import me.tippie.tippieutils.dependencies.Dependency;
import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.dependencies.classloader.IsolatedClassLoader;
import me.tippie.tippieutils.storage.SQLTypeImplementation;
import me.tippie.tippieutils.storage.StorageCredentials;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

@NoArgsConstructor
public class H2Impl implements SQLTypeImplementation {
    private Constructor<?> connectionConstructor;
    private static boolean initialized = false;


    private static final Set<Dependency> DEPENDENCIES = new HashSet<>(Set.of(Dependency.of("com.h2database",
            "h2",
            "2.1.214",
            "1iPNwPYdIYz1SajQnxw5H/kQlhFrIuJHVHX85PvnK9A=",
            Dependency.Properties.builder().autoload(false).build())));

    public static void addDependencyToClassPath(Dependency dependency){
        if (initialized)
            throw new IllegalStateException("Cannot add dependency to classpath after initialization");
        DEPENDENCIES.add(dependency);
    }

    private String url;
    private String username;
    private String password;

    @Override
    public void init(StorageCredentials cred , DependencyManager dependencyManager) {
        username = cred.getUsername();
        password = cred.getPassword();
        url = "jdbc:h2:file:" + cred.getAddress() + "";

        dependencyManager.loadDependencies(DEPENDENCIES);
        IsolatedClassLoader classLoader = dependencyManager.obtainClassLoaderWith(DEPENDENCIES);
        try {
            Class<?> connectionClass = classLoader.loadClass("org.h2.jdbc.JdbcConnection");
            this.connectionConstructor = connectionClass.getConstructor(String.class, Properties.class, String.class, Object.class, boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        initialized = true;
    }

    @Override
    public void close() {
        try {
            if (cache != null) cache.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Connection cache;

    @Override
    public Connection getConnection() {
        try {
            if (cache != null && !cache.isClosed()) return cache;
            cache = (Connection) this.connectionConstructor.newInstance(url, new Properties(), username, password, false);
            return cache;
        } catch (ReflectiveOperationException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * This a modified version adopted of https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/
     * licensed under the MIT License.
     *
     *  Copyright (c) lucko (Luck) <luck@lucko.me>
     *  Copyright (c) contributors
     */
    /**
     * Migrates the old (version 1) H2 database to version 2.
     * <p>
     * See <a href="http://www.h2database.com/html/migration-to-v2.html">here</a> for more info.
     */
    public final class MigrateH2ToVersion2 {
        private static final Set<Dependency> LEGACY_DEPENDENCIES = ImmutableSet.of(Dependency.of("com.h2database",
                "h2",
                "1.4.199",
                "MSWhZ0O8a0z7thq7p4MgPx+2gjCqD9yXiY95b5ml1C4=",
                Dependency.Properties.builder().autoload(false).build()));
        private final Plugin plugin;
        private final File file;
        private final DependencyManager dependencyManager;

        public MigrateH2ToVersion2(Plugin plugin, File file, DependencyManager dependencyManager) {
            this.plugin = plugin;
            this.file = file;
            this.dependencyManager = dependencyManager;
        }

        public void run() throws Exception {
            Path oldDatabase = new File(file.getParentFile(), file.getName() + ".h2.db").toPath();

            if (!Files.exists(oldDatabase)) {
                return;
            }

            Path tempMigrationFile = this.file.getParentFile().toPath().resolve("migration.sql");

            this.plugin.getLogger().log(Level.WARNING, "[DB Upgrade] Found an old (v1) H2 database file. LuckPerms will now attempt to upgrade it to v2 (this is a one time operation).");

            this.plugin.getLogger().info("[DB Upgrade] Stage 1: Exporting the old database to an intermediary file...");
            Constructor<?> constructor = getConnectionConstructor();
            try (Connection c = getConnection(constructor, file,username,password)) {
                try (Statement stmt = c.createStatement()) {
                    stmt.execute(String.format("SCRIPT TO '%s'", tempMigrationFile));
                }
            }

            this.plugin.getLogger().info("[DB Upgrade] Stage 2: Importing the intermediary file into the new database...");
            try (Connection c = H2Impl.this.getConnection()) {
                try (Statement stmt = c.createStatement()) {
                    stmt.execute(String.format("RUNSCRIPT FROM '%s'", tempMigrationFile));
                }
            }

            this.plugin.getLogger().info("[DB Upgrade] Stage 3: Tidying up...");
            Files.deleteIfExists(tempMigrationFile);
            Files.move(oldDatabase, oldDatabase.getParent().resolve(file.getName()+".backup"));

            this.plugin.getLogger().info("[DB Upgrade] All done!");
        }

        private Constructor<?> getConnectionConstructor() {
            dependencyManager.loadDependencies(LEGACY_DEPENDENCIES);
            IsolatedClassLoader classLoader = dependencyManager.obtainClassLoaderWith(LEGACY_DEPENDENCIES);
            try {
                Class<?> connectionClass = classLoader.loadClass("org.h2.jdbc.JdbcConnection");
                return connectionClass.getConstructor(String.class, Properties.class);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        private Connection getConnection(Constructor<?> constructor, File file, String username, String password) {
            try {
                Properties properties = new Properties();
                properties.setProperty("user", username);
                properties.setProperty("password", password);

                return (Connection) constructor.newInstance("jdbc:h2:" + file.getAbsolutePath(), properties);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
