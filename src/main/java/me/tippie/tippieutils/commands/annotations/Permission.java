package me.tippie.tippieutils.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	String permission();

	String noPermissionMessage() default "§cYou do not have the required permission to execute this command.";
}
