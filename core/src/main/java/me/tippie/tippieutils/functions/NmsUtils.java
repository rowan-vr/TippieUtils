package me.tippie.tippieutils.functions;

import me.tippie.tippieutils.guis.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class NmsUtils {
    @ApiStatus.Internal
    private static final Internals internals;

    static {
        Internals possibleInternals;
        try {
            final String packageName = SignGUI.class.getPackage().getName();
            final String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            possibleInternals = (Internals) Class.forName(packageName + "." + internalsName + "_NmsUtils").getDeclaredConstructor().newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException |
                       InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.SEVERE, "TippieUtils could not find a implementation for this server version for the SignGUI utility.", e);
            possibleInternals = null;
        }
        internals = possibleInternals;
    }

    /**
     * Show a book to a player without them actually having the book
     * @param player the player to show the book to
     * @param book the book to show to the player
     * @throws IllegalArgumentException if the given itemstack is not a written book
     */
    public static void showBook(Player player, ItemStack book) {
        if (!book.getType().equals(Material.WRITTEN_BOOK))
            throw new IllegalArgumentException("Only a written book can be shown to the player using showBook()");
        internals.showBook(player, book);
    }

    interface Internals{
        void showBook(Player player, ItemStack book);
    }
}
