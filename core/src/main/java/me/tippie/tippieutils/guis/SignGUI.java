package me.tippie.tippieutils.guis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * A class that creates a Sign Prompt for a player
 */
@Setter
@Getter(AccessLevel.PACKAGE)
public class SignGUI {

    /**
     * @hidden
     */
    @ApiStatus.Internal
    private static final Internals internals;

    static {
        Internals possibleInternals;
        try {
            final String packageName = SignGUI.class.getPackage().getName();
            final String internalsName = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            possibleInternals = (Internals) Class.forName(packageName + "." + internalsName + "_SignGUI").getDeclaredConstructor().newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException |
                       InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.SEVERE, "TippieUtils could not find a implementation for this server version for the SignGUI utility.", e);
            possibleInternals = null;
        }
        internals = possibleInternals;
    }

    /**
     * The player who this GUI is made for
     */
    private final Player player;

    /**
     * The text the player will see when the sign gui is opened
     */
    private List<String> text;

    /**
     * A predicate to verify whether the input is valid or not, when this predicate returns false the GUI will be reopened.
     * @param condition The new condition for this SignGUI
     */
    private Predicate<String[]> condition = (lines) -> true;

    /**
     * Create a new SignGUI for a specific player
     * @param player the player who to create the sign gui for
     */
    public SignGUI(Player player) {
        this.player = player;
    }

    /**
     * Open this sign gui for the player linked to it.
     * @return A completable future which will be completed when the player submits a valid input given the {@link SignGUI#condition} predicate
     */
    public CompletableFuture<String[]> open() {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        internals.onUpdateSign(player, (lines) -> {
            if (condition.test(lines)) {
                future.complete(lines);
                return true;
            } else {
                internals.open(this);
                return false;
            }
        });
        internals.open(this);
        return future;
    }

    /**
     * Set the text the player will see when this GUI is opened
     * @param text The text must be 4 or less
     * @throws IllegalArgumentException when the amount of lines in the text is greater than 4
     */
    public void setText(List<String> text) {
        if (text.size() > 4) throw new IllegalArgumentException("A sign GUI cannot have more than 4 lines of text");
        this.text = text;
    }

    /**
     * @hidden
     */
    @ApiStatus.Internal
    interface Internals {
        /**
         * @hidden
         */
        @ApiStatus.Internal
        void open(SignGUI gui);

        /**
         * @hidden
         */
        @ApiStatus.Internal
        void onUpdateSign(Player player, Function<String[], Boolean> consumer);
    }
}
