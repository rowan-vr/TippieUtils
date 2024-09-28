package me.tippie.tippieutils.reflection;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class CommandReflection {

	/**
	 * Gets the command map of Bukkit using reflection.
	 * @return Bukkit's command map
	 * @throws CommandMapException when there is an error getting the command map
	 */
	@NotNull
	public static CommandMap getCommandMap() throws CommandMapException {
		CommandMap commandMap = null;

		try {
			Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
			f.setAccessible(true);

			commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			throw new CommandMapException("Could not get command map!", e);
		}

		return commandMap;
	}

	/**
	 * Get a PluginCommand without it being registered in the plugin.yml, this will auto register by default
	 * @param name The name of the command
	 * @param plugin The plugin it belongs to.
	 * @return The created and registered command
	 * @throws PluginCommandException when there is an error creating the command.
	 */
	@NotNull
	public static PluginCommand getCommand(@NotNull String name, @NotNull Plugin plugin) throws PluginCommandException {
		return getCommand(name, plugin, true);
	}

	/**
	 * Get a PluginCommand without it being registered in the plugin.yml
	 * @param name The name of the command
	 * @param plugin The plugin it belongs to.
	 * @param autoRegister Whether to automatically register the command.
	 * @return The created command
	 * @throws PluginCommandException when there is an error creating the command.
	 */
	@NotNull
	public static PluginCommand getCommand(@NotNull String name, @NotNull Plugin plugin, boolean autoRegister) throws PluginCommandException {
		PluginCommand command = Bukkit.getPluginCommand(name);
		if (command != null) return command;

		try {

			Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			c.setAccessible(true);

			command = c.newInstance(name, plugin);

			if (autoRegister) getCommandMap().register(plugin.getName(), command);
		} catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | CommandMapException e) {
			throw new PluginCommandException("Could not create " + name + " for " + plugin.getName() + "!", e);
		}
		return command;
	}
}
