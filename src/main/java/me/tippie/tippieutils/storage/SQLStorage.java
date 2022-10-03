package me.tippie.tippieutils.storage;

import me.tippie.tippieutils.reflection.annotations.SqlQuery;
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

public class SQLStorage {
	private final Plugin plugin;
	private final String DB_CONNECTION;
	private final String DB_USER;
	private final String DB_PASSWORD;

	public SQLStorage(Plugin plugin, Driver driver, SQLType type, File file){
		this.plugin = plugin;
		if (type != SQLType.H2)
			throw new IllegalArgumentException("Only H2 is supported as embedded database");

		try {
			DriverManager.registerDriver(driver);
			DB_CONNECTION = "jdbc:h2:file:" + file.getAbsolutePath() + ";MV_STORE=false";
			DB_USER = "SA";
			DB_PASSWORD = "password";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void runResourceScript(@NotNull String file) throws SQLException, IOException {
		String setup;
		try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(file)) {
			setup = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
			throw e;
		}
		String[] queries = setup.split(";");
		for (String query : queries) {
			if (query.trim().isEmpty()) continue;
			try (Connection conn = this.getConnection();
				 PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.execute();
			}
		}
	}

	protected <T> CompletableFuture<T> prepareStatement(@NotNull Function<PreparedStatement,T> consumer) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		StackTraceElement caller = stackTraceElements[2];
		Optional<Method> optionalMethod = Arrays.stream(this.getClass().getDeclaredMethods())
				.filter(m -> m.getName().equals(caller.getMethodName()))
				.findAny();
		if (optionalMethod.isEmpty()) throw new RuntimeException("Could not find method " + caller.getMethodName() + "! Please make sure you have the @SqlQuery annotation and only call this in a class that extends SQLStorage.");
		Method method = optionalMethod.get();
		SqlQuery query = method.getAnnotation(SqlQuery.class);

		return CompletableFuture.supplyAsync(() -> {
			try (Connection conn = this.getConnection();
				 PreparedStatement stmt = query.generatedKeys() < 0 ?
						 conn.prepareStatement(query.value(), query.resultSetType(), query.resultSetConcurrency(), query.resultSetHoldability()) :
						 conn.prepareStatement(query.value(),query.generatedKeys())) {
				return consumer.apply(stmt);
			} catch (SQLException e) {
				throw new RuntimeException("Could not PrepareStatement" , e);
			}
		});
	}
	@NotNull
	private Connection getConnection() {
		try {
			Connection connection = DriverManager.getConnection(this.DB_CONNECTION, this.DB_USER,
					this.DB_PASSWORD);
			connection.setAutoCommit(true);
			return connection;
		} catch (SQLException e) {
			throw new RuntimeException("Database connection failed!",e);
		}
	}

	public enum SQLType {
		H2,
		MYSQL
	}
}
