package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.storage.annotations.SqlQuery;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class that can be used as base for a SQL storage class.
 * @since 1.2.0
 */
public class SQLStorage {
	private final Plugin plugin;
	private final String DB_CONNECTION;
	private final String DB_USER;
	private final String DB_PASSWORD;

	private final Driver DRIVER;

	/**
	 * Initialise the SQLStorage base for an embedded database.
	 * @param plugin The plugin that is using this storage.
	 * @param driver The SQL driver to use.
	 * @param type The type of database to use.
	 * @param file The file where the database should be embedded.
	 */
	public SQLStorage(Plugin plugin, Driver driver, SQLType type, File file){
		this.plugin = plugin;
		this.DRIVER = driver;
		if (type != SQLType.H2)
			throw new IllegalArgumentException("Only H2 is supported as embedded database");

		try {
//			DriverManager.registerDriver(driver);
			DB_CONNECTION = "jdbc:h2:file:" + file.getAbsolutePath() + "";
			DB_USER = "SA";
			DB_PASSWORD = "password";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initialise the SQLStorage base for an embedded database.
	 * @param plugin The plugin that is using this storage.
	 * @param driver The SQL driver to use.
	 * @param type The type of database to use.
	 * @param url The location of the database such as 255.123.129.521:3306/database.
	 * @param username The username of the database user
	 * @param password The password of the databuase user
	 */
	public SQLStorage(Plugin plugin, Driver driver, SQLType type, String url, String username, String password){
		this.plugin = plugin;
		this.DRIVER = driver;
		if (type != SQLType.MYSQL)
			throw new IllegalArgumentException("Only MySQL is currently supported as server database");

		try {
			DriverManager.registerDriver(driver);
			DB_CONNECTION = "jdbc:mysql://"+url;
			DB_USER = username;
			DB_PASSWORD = password;
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!",e);
			throw new RuntimeException(e);
		}
	}


	/**
	 * Run a sql script from the resources of this plugin.
	 * @param file The name of the file in the resources.
	 * @throws SQLException When a {@code SQLException} occurs whilst running the script.
	 * @throws IOException When a IOException occurs whilst reading the script.
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
			} catch (SQLException e){
				plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!",e);
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
	 *     			 } else {
	 *     			    return Optional.empty();
	 *     			 }
	 *     			 } catch (SQLException e) {
	 *     			 	throw new RuntimeException(e);
	 *     			 }
	 *     		});
	 *     }
	 * </pre>
	 * @param function A function from a {@link PreparedStatement} to a {@link T}.
	 * @return A {@link CompletableFuture} that will be completed when function is finished
	 * @param <T> The return value of the consumer
	 */
	protected <T> CompletableFuture<T> prepareStatement(@NotNull Function<PreparedStatement,T> function) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[2];
		Optional<Method> optionalMethod = Arrays.stream(this.getClass().getDeclaredMethods())
				.filter(m -> m.getName().equals(caller.getMethodName()))
				.findAny();
		if (optionalMethod.isEmpty()) {
			RuntimeException ex = new RuntimeException("Could not find method " + caller.getMethodName() + "! Please make sure you have the @SqlQuery annotation and only call this in a class that extends SQLStorage.");
			plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!",ex);
			throw ex;
		}
		Method method = optionalMethod.get();
		SqlQuery query = method.getAnnotation(SqlQuery.class);

		return CompletableFuture.supplyAsync(() -> {
			try (Connection conn = this.getConnection();
				 PreparedStatement stmt = query.generatedKeys() < 0 ?
						 conn.prepareStatement(query.value(), query.resultSetType(), query.resultSetConcurrency(), query.resultSetHoldability()) :
						 conn.prepareStatement(query.value(),query.generatedKeys())) {
				return function.apply(stmt);
			} catch (SQLException e) {
				RuntimeException ex = new RuntimeException("Could not PrepareStatement" , e);
				plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!",ex);
				throw ex;
			}
		});
	}
	@NotNull
	protected Connection getConnection() {
		try {
			Properties properties = new Properties();
			properties.setProperty("user", DB_USER);
			properties.setProperty("password", DB_PASSWORD);

			Connection connection = DRIVER.connect(this.DB_CONNECTION, properties);
			connection.setAutoCommit(true);
			return connection;
		} catch (SQLException e) {
			RuntimeException ex = new RuntimeException("Database connection failed!",e);
			plugin.getLogger().log(Level.SEVERE, "An error occured in SQLStorage!",ex);
			throw ex;
		}
	}

	public enum SQLType {
		H2,
		MYSQL
	}
}
