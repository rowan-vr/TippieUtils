# TippieUtils
This is a useful utilities libary for Spigot plugins which currently holds a command system and command reflection to add commands at runtime

# How to use

First you need to include the latest version in your pom.xml as dependency:
```xml
        <dependency>
            <groupId>me.tippie</groupId>
            <artifactId>tippieutils</artifactId>
            <version>1.0.3</version>
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

import me.tippie.tippieutils.v1_0_3.commands.TippieCommand;
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

### TippieCommands Sub Commands
