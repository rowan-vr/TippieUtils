package me.tippie.tippieutils.guis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.BiConsumer;

@Getter @Setter @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class OpenGUI {
    /**
     * The name of this GUI
     * @implNote This is not the title of the inventory, this can be used internally in a plugin.
     */
    @NotNull private String name;

    /**
     * The inventory of the GUI.
     */
    private Inventory inventory;

    /**
     * The player viewing this GUI.
     */
    private final Player viewer;

    /**
     * A hashmap containing the slot specific consumers for each slot set.
     * @see GuiBuilder#setSlot(int, ItemStack, BiConsumer)
     */
    private final HashMap<Integer, BiConsumer<InventoryClickEvent,OpenGUI>> slots = new HashMap<>();

    /**
     * The consumer ran when the inventory is closed
     */
    private BiConsumer<InventoryCloseEvent,OpenGUI> onClose = null;

    /**
     * The general click consumer for this open GUI.
     * @see OpenGUI#setAlwaysRunGeneralConsumer(boolean)
     * @see GuiBuilder#setGeneralClickConsumer(BiConsumer)
     */
    private BiConsumer<InventoryClickEvent, OpenGUI> generalClickConsumer = null;

    /**
     * Whether the generalClickConsumer of this OpenGUI must always run even when there is a slot specific consumer.
     * @see OpenGUI#setGeneralClickConsumer(BiConsumer)
     * @see OpenGUI#setSlot(int, ItemStack, BiConsumer)
     * @see GuiBuilder#setGeneralClickConsumer(BiConsumer)
     * @see GuiBuilder#setAlwaysRunGeneralConsumer(boolean)
     * @see GuiBuilder#setSlot(int, ItemStack, BiConsumer)
     */
    private boolean alwaysRunGeneralConsumer = false;

    /**
     * The {@link GuiManager} tracking this GUI.
     */
    private final GuiManager manager;

    /**
     * Whether the next time this inventory attempts to close it should be kept open or not.
     * @param cancelNextClose Whether the next time this inventory attempts to close it should be kept open or not.
     * @return if the next time this inventory attempts to close it should be kept open or not.
     */
    private boolean cancelNextClose = false;

    void performClick(int slot, InventoryClickEvent e) {
        BiConsumer<InventoryClickEvent,OpenGUI> function = slots.get(slot);
        if (function != null) function.accept(e, this);
        if ((function == null || alwaysRunGeneralConsumer) && generalClickConsumer != null) generalClickConsumer.accept(e, this);
    }

    void onClose(InventoryCloseEvent e){
        if (onClose != null) onClose.accept(e, this);
    }

    void updateInventory(Inventory inventory) {
        viewer.openInventory(inventory);
        manager.openGuis.put(viewer, this);
    }

    void updateName(String name) {
        this.name = name;
    }

    void updateSlots(HashMap<Integer, BiConsumer<InventoryClickEvent,OpenGUI>> slots) {
        this.slots.clear();
        this.slots.putAll(slots);
    }

    /**
     * Sets the item in the slot to the item.
     * @param slot The slot to set the item in.
     * @param item The item to set.
     * @param consumer The consumer to run when the item is clicked.
     * @see GuiBuilder#setSlot(int, ItemStack, BiConsumer)
     */
    public void setSlot(int slot, ItemStack item, BiConsumer<InventoryClickEvent,OpenGUI> consumer) {
        slots.put(slot, consumer);
        inventory.setItem(slot, item);
    }

    /**
     * Sets the title of the open GUI.
     * @param title The title to set.
     */
    public void setTitle(String title) {
        Inventory newInventory = Bukkit.createInventory(inventory.getHolder(), inventory.getSize(), title);
        newInventory.setContents(inventory.getContents());
        this.cancelNextClose = true;
        inventory = newInventory;
        viewer.openInventory(inventory);
    }
}
