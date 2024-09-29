package me.tippie.tippieutils.plugin;

import me.tippie.tippieutils.functions.TippieLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class TippiePlugin extends JavaPlugin {
    private TippieLogger logger;

    // ** Configurable Parameters ** //
    protected int prefixHex = 0xFF0000;

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    public final void onEnable() {
        super.onEnable();
    }

    @Override
    public final void onDisable() {
        super.onDisable();
    }

    @Override
    public final void onLoad() {
        logger = new TippieLogger(this, prefixHex);
    }

    public void load() {}

    public void enable() {}

    public void disable() {}
}
