package me.tippie.tippieutils.commands;

import lombok.Getter;
import me.tippie.tippieutils.commands.annotations.Args;
import me.tippie.tippieutils.commands.annotations.Permission;
import me.tippie.tippieutils.commands.annotations.PlayerSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TippieCommand implements TabExecutor {
	@Getter private final List<TippieCommand> subCommands = new ArrayList<>();
	protected String name = null;
	protected String description = null;
	protected String permission = null;
	protected int subLevel = 0;
	protected String prefix = "";

	@Override
	public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		
		if (subCommands.isEmpty()) {
			try {
				Method executes = this.getClass().getMethod("executes", CommandSender.class, Command.class, String.class, String[].class);

				PlayerSender playerSender = executes.getAnnotation(PlayerSender.class);
				if (playerSender != null && (playerSender.needsPlayer()) && !(sender instanceof Player)) {
					sender.sendMessage("This command can only be used as a player.");
					return true;
				}

				Permission permission = executes.getAnnotation(Permission.class);
				if (permission != null && !sender.hasPermission(permission.permission())) {
					sender.sendMessage(permission.noPermissionMessage());
					return true;
				}

				Args argsAnnotation = executes.getAnnotation(Args.class);
				if (argsAnnotation != null && args.length < argsAnnotation.min()) {
					sender.sendMessage(argsAnnotation.wrongArgsMessage());
					return true;
				}

				this.executes(sender, command, (subLevel == 0 ) ? label : label + " " + name, Arrays.copyOfRange(args,subLevel == 0 ? 0 : 1,args.length));
			} catch (NoSuchMethodException ignored) {
				sender.sendMessage("Command does not implement executes(). Type /help for help.");
			}
		} else {
			if (args.length < subLevel + 1) {
				sendHelpMessage(sender, ((subLevel == 0) ? "" : label + " ") + name, prefix);
			} else {
				for (TippieCommand subCommand : subCommands) {
					if (subCommand.name.equalsIgnoreCase(args[subLevel])) {
						try {
							Method executes = subCommand.getClass().getMethod("executes", CommandSender.class, Command.class, String.class, String[].class);

							PlayerSender playerSender = executes.getAnnotation(PlayerSender.class);
							if (playerSender != null && (playerSender.needsPlayer()) && !(sender instanceof Player)) {
								sender.sendMessage("This command can only be used as a player.");
								return true;
							}

							Permission permission = executes.getAnnotation(Permission.class);
							if ((subCommand.permission != null && !sender.hasPermission(subCommand.permission)) || (permission != null && !sender.hasPermission(permission.permission()))) {
								sender.sendMessage((permission != null) ? permission.noPermissionMessage() : "§cYou do not have the required permission to execute this command.");
								return true;
							}

							Args argsAnnotation = executes.getAnnotation(Args.class);
							if (argsAnnotation != null && args.length < argsAnnotation.min()) {
								sender.sendMessage(argsAnnotation.wrongArgsMessage());
								return true;
							}

							subCommand.onCommand(sender, command, ((subLevel == 0) ? "" : label + " ") + name, Arrays.copyOfRange(args,subLevel == 0 ? 0 : 1,args.length)); //TODO: Test this
						} catch (NoSuchMethodException ignored) {
							sender.sendMessage("Command does not implement executes(). Type /help for help.");
						}
						return true;
					}
				}
				sendHelpMessage(sender, ((subLevel == 0) ? "" : label + " ") + name, prefix);
			}
		}
		return true;
	}

	@Nullable @Override
	public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		List<String> complete = null;
		if (subCommands.isEmpty()) {
			complete = completes(sender, command, alias + " " + name, args);
		} else if (args.length >= 2) {
			for (TippieCommand subCommand : subCommands) {
				if (subCommand.name.equalsIgnoreCase(args[0])) {
					if (subCommand.permission != null && !sender.hasPermission(subCommand.permission))
						complete = new ArrayList<>();
					else
						complete = subCommand.onTabComplete(sender, command, ((subLevel == 0) ? "" : alias + " ") + name, Arrays.copyOfRange(args, 1, args.length));
					break;
				}
			}
		} else {
			complete = subCommands.stream().filter(cmd -> cmd.permission == null || sender.hasPermission(cmd.permission)).map(cmd -> cmd.name).collect(Collectors.toList());
		}
		if (complete == null) return null;
		return complete.stream().filter(str -> str.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());

	}

	public List<String> completes(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<>();
	}

	public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
		throw new NoSuchMethodException("Command does not implement executes()");
	}
	
	protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
		sender.sendMessage((prefix.equals("") ? "" : prefix + " ")  + "§6" + label.replaceFirst(String.valueOf(label.charAt(0)),String.valueOf(label.charAt(0)).toUpperCase()) + " help");
		sender.sendMessage("");
		for (TippieCommand subCommand : subCommands) {
			if (subCommand.permission == null || sender.hasPermission(subCommand.permission)) sender.sendMessage("§7 - §f/" + label + " " + subCommand.name + "§7: " + subCommand.description);
		}
		sender.sendMessage("");
	}
}
