package me.tippie.tippieutils.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for commands that require a player to be the sender.
 * @see me.tippie.tippieutils.commands.TippieCommand
 */
@Target(ElementType.METHOD) @Retention(RetentionPolicy.RUNTIME)
public @interface PlayerSender {
	boolean needsPlayer() default true;
}
