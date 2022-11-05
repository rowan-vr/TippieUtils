package me.tippie.tippieutils.guis.prompts;

import me.tippie.tippieutils.guis.GuiBuilder;
import me.tippie.tippieutils.guis.GuiManager;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public abstract class PromptGUI<T> {
    protected Player player;
    protected GuiManager manager;

    PromptGUI(Player player, GuiManager manager){
        this.player = player;
        this.manager = manager;
    }

    public abstract CompletableFuture<T> prompt();
}
