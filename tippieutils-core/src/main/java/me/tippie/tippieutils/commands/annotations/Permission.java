package me.tippie.tippieutils.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for commands that require a permission.
 * @see me.tippie.tippieutils.commands.TippieCommand
 */
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	/**
	 * The permission required to execute the command.
	 * @return the permission required to execute the command.
	 */
	String permission();

	/**
	 * The message to display if the player does not have the permission.
	 * @return the message to display if the player does not have the permission.
	 */
	String noPermissionMessage() default "§cYou do not have the required permission to execute this command.";
}
