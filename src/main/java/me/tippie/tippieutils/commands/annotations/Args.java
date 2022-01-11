package me.tippie.tippieutils.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface Args {
	int min();

	String wrongArgsMessage() default "&cCommand used incorrectly! This command needs more arguments";
}
