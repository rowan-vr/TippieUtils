package me.tippie.tippieutils.guis;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class GuiManager implements Listener {
    final HashMap<Player, OpenGUI> openGuis = new HashMap<>();

    /**
     * Creates a new GuiManager for the given plugin.
     * @param plugin The plugin to create the GuiManager for.
     */
    public GuiManager(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void onInventoryInteract(InventoryInteractEvent e) {
        OpenGUI gui = openGuis.get((Player) e.getWhoClicked());
        if (gui == null) return;

        e.setCancelled(true);
        if (e instanceof InventoryClickEvent) {
            InventoryClickEvent clickEvent = (InventoryClickEvent) e;
            gui.performClick(clickEvent.getRawSlot(), clickEvent);
        }
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        this.onInventoryInteract(e);
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent e) {
        this.onInventoryInteract(e);
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        OpenGUI gui = openGuis.get((Player) e.getPlayer());
        if (gui == null) return;

        if (gui.isCancelNextClose()) {
            gui.setCancelNextClose(false);
            return;
        }

        gui.onClose(e);

        if (!gui.isCancelNextClose())
            openGuis.remove((Player) e.getPlayer());
        else
            gui.setCancelNextClose(false);
    }

    /**
     * Gets an {@link OpenGUI} for the given player.
     * @param player The player to get the {@link OpenGUI} for. If the player has no GUI open for this manager, null is returned.
     * @return The {@link OpenGUI} for the given player.
     */
    public OpenGUI getOpenGUI(Player player) {
        return openGuis.get(player);
    }


    void openMenu(Player viewer, OpenGUI gui) {
        viewer.openInventory(gui.getInventory());
        openGuis.put(viewer, gui);
    }
}
