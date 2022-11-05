package me.tippie.tippieutils.functions;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class v1_19_R1_NmsUtils implements NmsUtils.Internals {
    @Override
    public void showBook(Player player, ItemStack book) {
        ServerPlayer sp = ((CraftPlayer) player).getHandle();
        ClientboundContainerSetSlotPacket packet = new ClientboundContainerSetSlotPacket(
                0, 0,
                player.getInventory().getHeldItemSlot() + 36,
                CraftItemStack.asNMSCopy(book));
        sp.connection.send(packet);
        ClientboundOpenBookPacket packet2 = new ClientboundOpenBookPacket(InteractionHand.MAIN_HAND);
        sp.connection.send(packet2);
        ClientboundContainerSetSlotPacket packet3 = new ClientboundContainerSetSlotPacket(
                0, 0,
                player.getInventory().getHeldItemSlot() + 36,
                CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand()));
        sp.connection.send(packet3);
    }
}
