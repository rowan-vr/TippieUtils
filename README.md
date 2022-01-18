# TippieUtils
This is a useful utilities libary for Spigot plugins which currently holds a command system and command reflection to add commands at runtime

# How to use

First you need to include the latest version in your pom.xml as dependency:
```xml
        <dependency>
            <groupId>me.tippie</groupId>
            <artifactId>tippieutils</artifactId>
            <version>1.0.5</version>
            <scope>compile</scope>
        </dependency>
```
and the repository it is hosted on
```xml
        <repository>
            <id>tippie-public</id>
            <url>https://repo.tippie.me/repository/maven-public/</url>
        </repository>
```
Now we're ready to use it!

## TippieCommands
A TippieCommand is the command class we use for the command system, to create a TippieCommand you simply extend that class and override the exectutes and if you want tab-completion also the completes command. Such an example class could be:
```java
package me.tippie.tippieutilstest;

import me.tippie.tippieutils.v1_0_5.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestCommand extends TippieCommand {

    @Override public List<String> completes(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("Test received!");
    }
}
```
You now can use these two functions like you're used to with the spigot-api, there are multiple useful annotations you can use to make making commands easier, so can `@PlayerSender` be added to above the executes command to force the sender being a place, now there's no more need to check for `sender instanceof Player` you now can safely cast the sender to a player. You also can add the `@Permission(permission = "some.permission")` annotation above the executes method, with this permission the TippieCommand checks for you if the sender has given permission before it executes.

You also mustn't forget to set the executor of the command in your onEnable method:
```java
    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("test").setExector(new TestCommand());
    }
```

### TippieCommands Sub Commands
TippieCommands also support subcommands easily, to use such a subcommand you first need to create a base tippie command, an example for this is:
(optional) You can override the protected sendHelpMessage(Player) function to create a custom help-messages sent when a invalid or no subcommand is executed
(optional) You can also only add a prefix to the help message without overwriting it, you do this by doing `super.prefix = "your prefix"`
```java
package me.tippie.tippieutilstest;

import me.tippie.tippieutils.v1_0_5.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BaseCommand extends TippieCommand {
    new BaseCommand(){
        super.name = "test";
        super.sublevel = "0";
        super.prefix = "§c[§4TippieUtils§c]"
    }

    @Override protected void sendHelpMessage(CommandSender sender, String label, String prefix) {
        sender.sendMessage("§6[§4TippieUtils§6] §7How to use test commands: ");
        sender.sendMessage("");
        for (TippieCommand subCommand : getSubCommands()) {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) sender.sendMessage("§7 - §f/" + label + " " + subCommand.getName() + "§7: " + subCommand.getDescription());
        }
        sender.sendMessage("");
    }
}
```
Now we can create a sub command, it is important to set the sublevel to `1` or higher depending on what level this subcommand is, lets say, this is a subcommand of a subcommand of a subcommand the sublevel should be 2 (example is /test this that [args])
(optional) You can add a permission for this subcommand and every possible subcommand added to it by adding `super.permission = "some.permission"`.
(note) The args passed to the executes or completetes function does not contain the subcommands, so when a player executes `/test sub one two three` only `one two three` are passed.
```java
package me.tippie.tippieutilstest;

import me.tippie.tippieutils.v1_0_5.commands.TippieCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TestSubCommand extends TippieCommand {
    new TestSubCommand(){
        super.name = "sub";
        super.description = "This is a subcommand";
        super.sublevel = 1;
        super.permission = "some.permission";
    }
    
    @Override
    public void executes(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage("You sent me " + args.length + " arguments!");
    }
}
```
After creating this subcommand you have to add it to the first command, one way to do this is in the constructor of the super command or you can do it in the onEnable:
```java
    @Override
    public void onEnable() {
        TippieCommand testCommand = new TestCommand();
        testCommand.getSubCommands().add(new TestSubCommand());
        Bukkit.getPluginCommand("test").setExector(testCommand);
    }
```
Now the sub command is ready to use!

## CommandReflection
TippieUtils also includes a easy way to add commands on runtime without having to register them in the plugin.yml, you do this as following:
```java
    @Override
    public void onEnable() {
		try {
			CommandReflection.getCommand("test",this).setExecutor(new CommandExecutor() {
				@Override
				public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
					sender.sendMessage("abbcc");
					return true;
				}
			});
		} catch (PluginCommandException e) {
			e.printStackTrace();
		}
    }
```
`CommandReflection.getCommand(String, Plugin)` registers the command in the command map automatically, for some use cases you don't want this, for example if you want to add aliases to the command (this has to be done before registering the command). This is also possible by first getting the command with autoRegister false and registering the command yourself afterwards, an example of this below.
(note) This example has the aliases `testme` and `testalias`.
```java
    @Override
    public void onEnable() {
		try {
			PluginCommand unregisteredCommand = CommandReflection.getCommand("test",this,false);
			
			unregisteredCommand.setAliases(Arrays.asList("testme","testalias"));
			
			unregisteredCommand.setExecutor(new CommandExecutor() {
				@Override
				public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
					sender.sendMessage("abbcc");
					return true;
				}
			});
			
			CommandReflection.getCommandMap().register(this.getName().toLowerCase(),unregisteredCommand);
			
		} catch (PluginCommandException | CommandMapException e) {
			e.printStackTrace();
		}
    }
```

# Contributing
If you have any idea or improvement and would like to contribute I encourage you to do so! Fork this repository and make a pull request! If the changes are good it'll be merged and included in the next release of these utils.
## Got a nice idea but don't want to contribute?
No problem! Just create an issue and maybe someone else will add it for you!

# Found a bug?
Bugs happen... sadly... But let us know! Create an issue and it'll be fixed soon.
