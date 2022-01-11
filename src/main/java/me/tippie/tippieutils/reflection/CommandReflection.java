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

	@NotNull
	public static PluginCommand getCommand(@NotNull String name, @NotNull Plugin plugin) throws PluginCommandException {
		return getCommand(name, plugin, true);
	}

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
