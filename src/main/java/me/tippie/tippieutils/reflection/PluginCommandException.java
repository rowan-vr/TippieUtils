package me.tippie.tippieutils.reflection;

import org.bukkit.plugin.Plugin;

/**
 * Exception that gets thrown when something went wrong during creation of a plugin command
 * @see CommandReflection#getCommand(String, Plugin, boolean) 
 */
public class PluginCommandException extends Exception {
	PluginCommandException(String message, Throwable cause) {
		super(message, cause);
	}
}
