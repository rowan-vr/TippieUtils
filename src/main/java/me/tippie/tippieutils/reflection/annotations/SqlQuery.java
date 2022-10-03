package me.tippie.tippieutils.reflection.annotations;

import lombok.Getter;
import org.intellij.lang.annotations.MagicConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.ResultSet;
import java.sql.Statement;

@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface SqlQuery {
	String value();
	@MagicConstant(intValues = {ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.TYPE_SCROLL_SENSITIVE}) int resultSetType() default ResultSet.TYPE_FORWARD_ONLY;
	@MagicConstant(intValues = {ResultSet.CONCUR_READ_ONLY, ResultSet.CONCUR_UPDATABLE}) int resultSetConcurrency() default ResultSet.CONCUR_READ_ONLY;
	@MagicConstant(intValues = {ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CLOSE_CURSORS_AT_COMMIT}) int resultSetHoldability() default ResultSet.HOLD_CURSORS_OVER_COMMIT;
	@MagicConstant(intValues = {Statement.RETURN_GENERATED_KEYS, Statement.NO_GENERATED_KEYS}) int generatedKeys() default -1;
}
