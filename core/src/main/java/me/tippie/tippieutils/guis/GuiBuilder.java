package me.tippie.tippieutils.guis;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiBuilder {
    private final Inventory inventory;
    private final HashMap<Integer, BiConsumer<InventoryClickEvent,OpenGUI>> slots = new HashMap<>();
    private BiConsumer<InventoryClickEvent, OpenGUI> generalClickConsumer = null;
    private BiConsumer<InventoryCloseEvent,OpenGUI> closeConsumer = null;
    private Integer refreshRate = null;
    private Consumer<OpenGUI> refreshFunction = null;
    @Getter private boolean alwaysRunGeneralConsumer = false;
    @Getter private String name = "null";

    /**
     * Creates a new GUIBuilder given the amount of rows, title and the inventory holder.
     * @param rows The amount of rows for the GUI.
     * @param title The title of the GUI.
     * @param holder The inventory holder of the GUI.
     */
    public GuiBuilder(int rows, String title, InventoryHolder holder) {
        inventory = Bukkit.createInventory(holder, rows * 9, title);
    }

    /**
     * Creates a new GUIBuilder given an Inventory.
     * @param inventory The inventory to use.
     */
    public GuiBuilder(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Sets the item in the slot.
     * @param slot The slot to set the item in.
     * @param item The item to set.
     * @param consumer The consumer to run when the item is clicked.
     * @return The GUIBuilder instance.
     */
    public GuiBuilder setSlot(int slot, ItemStack item, BiConsumer<InventoryClickEvent,OpenGUI> consumer) {
        slots.put(slot, consumer);
        inventory.setItem(slot, item);
        return this;
    }

    /**
     * Sets the name of the GUI.
     * This is not the title of the GUI but a value you can use internally in a plugin.
     * @param name The name of the GUI.
     * @return The GUIBuilder instance.
     */
    public GuiBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the general click consumer. This is the consumer which is ran when any slot with the inventory open is clicked. This includes slots outside of the inventory but still in the inventory view!
     * @param consumer The consumer to run.
     * @return The GUIBuilder instance.
     */
    public GuiBuilder setGeneralClickConsumer(BiConsumer<InventoryClickEvent,OpenGUI> consumer) {
        generalClickConsumer = consumer;
        return this;
    }

    /**
     * Sets the close consumer of the GUI. This is the consumer which is ran when the inventory is closed. You can cancel the close event by calling OpenGUI#setCancelNextClose(boolean).
     * @param consumer The consumer to run.
     * @return The GUIBuilder instance.
     */
    public GuiBuilder setCloseConsumer(BiConsumer<InventoryCloseEvent,OpenGUI> consumer) {
        closeConsumer = consumer;
        return this;
    }

    /**
     * Sets whether the general click consumer should always be ran even if there is another slot specific consumer set for the clicked slot.
     * @param alwaysRunGeneralConsumer Whether the general click consumer should always be ran.
     * @return The GUIBuilder instance.
     */
    public GuiBuilder setAlwaysRunGeneralConsumer(boolean alwaysRunGeneralConsumer) {
        this.alwaysRunGeneralConsumer = alwaysRunGeneralConsumer;
        return this;
    }

    public GuiBuilder setRefresh(int refreshRate, @NotNull Consumer<OpenGUI> refreshFunction) {
        this.refreshFunction = refreshFunction;
        this.refreshRate = refreshRate;
        return this;
    }

    public GuiBuilder clearRefresh() {
        this.refreshFunction = null;
        this.refreshRate = null;
        return this;
    }

    /**
     * Opens the GUI for the given player.
     * @param player The player to open the GUI for.
     * @param manager The OpenGUI manager to use.
     * @return The OpenGUI instance for the GUI.
     */
    public OpenGUI open(Player player, GuiManager manager) {
        OpenGUI openGUI = manager.openGuis.get(player);
        if (openGUI == null) {
            openGUI = new OpenGUI(name, player, manager);
            openGUI.setInventory(inventory);
            openGUI.getSlots().putAll(slots);
            manager.openMenu(player, openGUI);
        } else {
            openGUI.updateInventory(inventory);
            openGUI.updateSlots(slots);
            openGUI.updateName(name);
        }
        openGUI.setOnClose(closeConsumer);
        openGUI.setGeneralClickConsumer(generalClickConsumer);
        openGUI.setAlwaysRunGeneralConsumer(alwaysRunGeneralConsumer);
        openGUI.setRefreshFunction(refreshFunction);
        openGUI.setRefreshRate(refreshRate);
        openGUI.startRefreshTask();
        return openGUI;
    }
}
