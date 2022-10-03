package me.tippie.tippieutils.storage.annotations;

import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.function.Function;

/**
 * Annotation to define a method as database query.
 * @see me.tippie.tippieutils.storage.SQLStorage#prepareStatement(Function)
 */
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface SqlQuery {
	/**
	 * The SQL Query that will be executed.
	 * @return The SQL Query.
	 */
	String value();

	/**
	 * The result set type
	 * @return The result set type
	 * @see java.sql.Connection#prepareStatement(String, int, int, int)
	 */
	@MagicConstant(intValues = {ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE}) int resultSetType() default ResultSet.TYPE_FORWARD_ONLY;

	/**
	 * The result set concurrency
	 * @return The result set concurrency
	 * @see java.sql.Connection#prepareStatement(String, int, int, int)
	 */
	@MagicConstant(intValues = {ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE}) int resultSetConcurrency() default ResultSet.CONCUR_READ_ONLY;

	/**
	 * The result set holdability
	 * @return The result set holdability
	 * @see java.sql.Connection#prepareStatement(String, int, int, int)
	 */
	@MagicConstant(intValues = {ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CLOSE_CURSORS_AT_COMMIT}) int resultSetHoldability() default ResultSet.HOLD_CURSORS_OVER_COMMIT;

	/**
	 * The auto generated keys configuration or -1 when not set.
	 * @return The auto generated keys configuration or -1 when not set.
	 * @see java.sql.Connection#prepareStatement(String, int)
	 */
	@MagicConstant(intValues = {Statement.RETURN_GENERATED_KEYS, Statement.NO_GENERATED_KEYS}) int generatedKeys() default -1;
}
