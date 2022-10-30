package me.tippie.tippieutils.reflection;

/**
 * Exception thrown when something went wrong getting the command map of the server.
 */
public class CommandMapException extends Exception {
	CommandMapException(String message, Throwable cause) {
		super(message, cause);
	}
}
