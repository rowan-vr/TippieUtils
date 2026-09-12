package me.tippie.tippieutils.functions;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * Utility class with utilities that has something to do with items
 */
public class ItemUtils {

    /**
     * Create a player head with a base64 texture
     * @param base64 The base64 texture of the player head
     * @return The itemstack of the playerhead
     */
    @NotNull
    public static ItemStack getSkull(String base64) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        mutateItemMeta(meta, base64);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create an itemstack with given material, title and lore
     * @param icon The material this item stack should be of
     * @param title The title of the item stack
     * @param lore The lore of the itemstack
     * @return The created itemstack
     */
    public static ItemStack getTextItem(Material icon, String title, String... lore) {
        return getTextItem(new ItemStack(icon), title, lore);
    }

    /**
     * Create an itemstack with given material, title and lore
     * @param icon The material this item stack should be of
     * @param title The title of the item stack
     * @param lore The lore of the itemstack
     * @return The created itemstack
     */
    public static ItemStack getTextItem(ItemStack icon, String title, String... lore) {
        ItemMeta iconMeta = icon.getItemMeta();
        iconMeta.setDisplayName(title);
        iconMeta.setLore(List.of(lore));
        icon.setItemMeta(iconMeta);
        return icon;
    }

    private static Method metaSetProfileMethod;
    private static Field metaProfileField;

    private static void mutateItemMeta(SkullMeta meta, String b64) {
        try {
            if (metaSetProfileMethod == null) {
                metaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                metaSetProfileMethod.setAccessible(true);
            }
            metaSetProfileMethod.invoke(meta, makeProfile(b64));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            // if in an older API where there is no setProfile method,
            // we set the profile field directly.
            try {
                if (metaProfileField == null) {
                    metaProfileField = meta.getClass().getDeclaredField("profile");
                    metaProfileField.setAccessible(true);
                }
                metaProfileField.set(meta, makeProfile(b64));

            } catch (NoSuchFieldException | IllegalAccessException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    private static GameProfile makeProfile(@NotNull String b64) {
        // random uuid based on the b64 string
        UUID id = new UUID(
                b64.substring(b64.length() - 20).hashCode(),
                b64.substring(b64.length() - 10).hashCode()
        );
        GameProfile profile = new GameProfile(id, "Player");
        profile.getProperties().put("textures", new Property("textures", b64));
        return profile;
    }
}
