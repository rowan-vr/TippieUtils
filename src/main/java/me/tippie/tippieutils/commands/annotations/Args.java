package me.tippie.tippieutils.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to set the minimum amount of arguments a command needs to have.
 * @deprecated This is a work in progress annotation.
 * @see me.tippie.tippieutils.commands.TippieCommand
 */
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME) @Deprecated
public @interface Args {
	int min();

	String wrongArgsMessage() default "&cCommand used incorrectly! This command needs more arguments";
}
