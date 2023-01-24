package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.dependencies.DependencyManager;
import me.tippie.tippieutils.storage.annotations.SqlQuery;
import me.tippie.tippieutils.storage.impl.H2Impl;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class that can be used as base for a SQL storage class.
 *
 * @since 1.2.0
 */
public class SQLStorage {
    private final Plugin plugin;
    private final SQLTypeImplementation implementation;

    private final DependencyManager DEPENDENCY_MANAGER;

    /**
     * Initialise the SQLStorage base for an embedded database.
     *
     * @param plugin            The plugin that is using this storage.
     * @param dependencyManager The dependency manager used to load the required dependencies.
     * @param type              The type of database to use.
     * @param file              The file where the database should be embedded.
     */
    public SQLStorage(Plugin plugin, DependencyManager dependencyManager, SQLType type, File file) {
        this.plugin = plugin;
        this.DEPENDENCY_MANAGER = dependencyManager;
        File v2File = new File(file.getParentFile(), file.getName() + "-2");

        try {
            this.implementation = (SQLTypeImplementation) type.implClass.getDeclaredConstructors()[0].newInstance();
            StorageCredentials cred = StorageCredentials.builder()
                    .address(v2File.getAbsolutePath())
                    .username("SA")
                    .password("password")
                    .build();

            this.implementation.init(cred, DEPENDENCY_MANAGER);
            if (this.implementation instanceof H2Impl impl){
                impl.new MigrateH2ToVersion2(plugin,file,dependencyManager).run();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Initialise the SQLStorage base for an embedded database.
     *
     * @param plugin            The plugin that is using this storage.
     * @param dependencyManager The dependency manager used to load the required dependencies.
     * @param type              The type of database to use.
     * @param url               The location of the database such as 255.123.129.521:3306.
     * @param database          The name of the database.
     * @param username          The username of the database user
     * @param password          The password of the database user
     */
    public SQLStorage(Plugin plugin, DependencyManager dependencyManager, SQLType type, String url, String database, String username, String password) {
        this.plugin = plugin;
        this.DEPENDENCY_MANAGER = dependencyManager;

        try {
            this.implementation = (SQLTypeImplementation) type.implClass.getDeclaredConstructors()[0].newInstance();
            StorageCredentials cred = StorageCredentials.builder()
                    .address(url)
                    .database(database)
                    .username(username)
                    .password(password)
                    .build();
            this.implementation.init(cred,DEPENDENCY_MANAGER);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialise the SQLStorage base for a custom SQL implementation.
     *
     * @param plugin            The plugin that is using this storage.
     * @param dependencyManager The dependency manager used to load the required dependencies.
     * @param impl              The custom implementation of the {@link SQLTypeImplementation} interface.
     *
     * @implNote The custom implementation is given null as the {@link StorageCredentials} object.
     */
    public SQLStorage(Plugin plugin, DependencyManager dependencyManager, SQLTypeImplementation impl) {
        this.plugin = plugin;
        this.DEPENDENCY_MANAGER = dependencyManager;
        this.implementation = impl;

        try {
            this.implementation.init(null,DEPENDENCY_MANAGER);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Run a sql script from the resources of this plugin.
     *
     * @param file The name of the file in the resources.
     * @throws SQLException When a {@code SQLException} occurs whilst running the script.
     * @throws IOException  When a IOException occurs whilst reading the script.
     */
    protected void runResourceScript(@NotNull String file) throws SQLException, IOException {
        String setup;
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(file)) {
            setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", e);
            throw e;
        }
        String[] queries = setup.split(";");
        for (String query : queries) {
            if (query.trim().isEmpty()) continue;
            try (Connection conn = this.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", e);
                throw e;
            }
        }
    }

    /**
     * Get a prepared statement from the query in the {@link SqlQuery} annotation.
     * Example of correct usage of this method inside a class that extends {@link SQLStorage}:
     * <pre>
     *     {@literal @}SqlQuery("SELECT * FROM table WHERE id = ?") // In this case the 'test' column will have an int value.
     *     public CompletableFuture&lt;Optional&lt;Integer&gt;&gt; getRow(int id) {
     *     		return prepareStatement((stmt) -> {
     *     		try {
     *     			 stmt.setInt(1, id);
     *     			 ResultSet rs = stmt.executeQuery();
     *     			 if (rs.next()) {
     *     			 	return Optional.of(rs.getInt("TEST"));
     *                 } else {
     *     			    return Optional.empty();
     *                 }
     *                 } catch (SQLException e) {
     *     			 	throw new RuntimeException(e);
     *                 }
     *            });
     *     }
     * </pre>
     *
     * @param function A function from a {@link PreparedStatement} to a {@link T}.
     * @param <T>      The return value of the consumer
     * @return A {@link CompletableFuture} that will be completed when function is finished
     */
    protected <T> CompletableFuture<T> prepareStatement(@NotNull Function<PreparedStatement, T> function) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[2];
        Optional<Method> optionalMethod = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(m -> m.getName().equals(caller.getMethodName()))
                .findAny();
        if (optionalMethod.isEmpty()) {
            RuntimeException ex = new RuntimeException("Could not find method " + caller.getMethodName() + "! Please make sure you have the @SqlQuery annotation and only call this in a class that extends SQLStorage.");
            plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", ex);
            throw ex;
        }
        Method method = optionalMethod.get();
        SqlQuery query = method.getAnnotation(SqlQuery.class);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = this.getConnection();
                 PreparedStatement stmt = query.generatedKeys() < 0 ?
                         conn.prepareStatement(query.value(), query.resultSetType(), query.resultSetConcurrency(), query.resultSetHoldability()) :
                         conn.prepareStatement(query.value(), query.generatedKeys())) {
                return function.apply(stmt);
            } catch (SQLException e) {
                RuntimeException ex = new RuntimeException("Could not PrepareStatement", e);
                plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", ex);
                throw ex;
            }
        });
    }

    @NotNull
    protected Connection getConnection() {
        try {
            return implementation.getConnection();
        } catch (SQLException e) {
            RuntimeException ex = new RuntimeException("Database connection failed!", e);
            plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!", ex);
            throw ex;
        }
    }
}
