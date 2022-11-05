package me.tippie.tippieutils.commands;

import lombok.Getter;
import me.tippie.tippieutils.commands.annotations.Args;
import me.tippie.tippieutils.commands.annotations.Permission;
import me.tippie.tippieutils.commands.annotations.PlayerSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a TippieCommand which you can extend to use.
 */
@Getter
public class TippieCommand implements TabExecutor {
	/**
	 * The registered subcommands of this command.
	 * @return The list of subcommands registered.
	 */
	@Getter private final List<TippieCommand> subCommands = new ArrayList<>();
	
	/**
	 * The name of this command.
	 * @return The name of this command.
	 */
	protected String name = null;
	
	/**
	 * The description of this command used in generated help messages.
	 * @return The description of this command.
	 * @see TippieCommand#sendHelpMessage(CommandSender, String, String) 
	 */
	protected String description = null;

	/**
	 * The permission needed to execute this command
	 * @return The permission needed to execute this command
	 */
	protected String permission = null;

	/**
	 * The sublevel of this command. This indicates how deep the command is within subcommands. For example, if this command is the root command, then this value is 0. If this command is a subcommand of another command, then this value is 1.
	 * @return The sublevel of this command.
	 */
	protected int subLevel = 0;
	
	/**
	 * The prefix used in the help message.
	 * @return The prefix used in the help message.
	 * @see TippieCommand#sendHelpMessage(CommandSender, String, String) 
	 */
	protected String prefix = "";

	/**
	 * @hidden
	 */
	@Override @ApiStatus.Internal
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

	/**
	 * @hidden
	 */
	@Nullable @Override @ApiStatus.Internal
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

	/**
	 * Called when the command is tab completed.
	 * @param sender The sender of the command.
	 * @param command The command.
	 * @param alias The alias of the command. This is the same as the {@link #name} of the command.
	 * @param args The arguments of the command. This does not include the subcommand.Example the command '/test subcommand argument' would only have 'argument' here
	 * @return The list of completions
	 */
	public List<String> completes(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<>();
	}

	/**
	 * Called when the command is tab executed.
	 * @param sender The sender of the command.
	 * @param command The command.
	 * @param label The alias of the command. This is the same as the {@link #name} of the command.
	 * @param args The arguments of the command. This does not include the subcommand.Example the command '/test subcommand argument' would only have 'argument' here
	 * @see me.tippie.tippieutils.commands.annotations   
	 */
	public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) throws NoSuchMethodException {
		throw new NoSuchMethodException("Command does not implement executes()");
	}

	/**
	 * Sends a help message about the subcommands of this command when called.
	 * @param sender The command sender to send the help message to.
	 * @param label The label of the command.
	 * @param prefix The prefix used in the help message.
	 */
	protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
		sender.sendMessage((prefix.equals("") ? "" : prefix + " ")  + "§6" + label.replaceFirst(String.valueOf(label.charAt(0)),String.valueOf(label.charAt(0)).toUpperCase()) + " help");
		sender.sendMessage("");
		for (TippieCommand subCommand : subCommands) {
			if (subCommand.permission == null || sender.hasPermission(subCommand.permission)) sender.sendMessage("§7 - §f/" + label + " " + subCommand.name + "§7: " + subCommand.description);
		}
		sender.sendMessage("");
	}
}
