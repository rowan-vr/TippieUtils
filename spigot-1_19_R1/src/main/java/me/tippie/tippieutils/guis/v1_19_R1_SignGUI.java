package me.tippie.tippieutils.guis;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class v1_19_R1_SignGUI implements SignGUI.Internals {
    @Override
    public void open(SignGUI gui) {
        Player player = gui.getPlayer();
        Location location = player.getLocation();
        BlockPos pos = new BlockPos(location.getBlockX(), 255, location.getBlockZ());

        BlockData blockData = Material.OAK_SIGN.createBlockData();
        BlockState state = ((CraftBlockData) blockData).getState();
        player.sendBlockChange(new Location(player.getWorld(), pos.getX(), pos.getY(), pos.getZ()), blockData);

        SignBlockEntity signBlock = BlockEntityType.SIGN.create(pos, state);

        for (int i = 0; i < 4; i++) {
            if (gui.getText().size() <= i) break;
            signBlock.messages[i] = Component.literal(gui.getText().get(i));
        }

        ClientboundOpenSignEditorPacket openSign = new ClientboundOpenSignEditorPacket(pos);
        ClientboundBlockEntityDataPacket signData = ClientboundBlockEntityDataPacket.create(signBlock);

        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().connection.send(signData);
        cp.getHandle().connection.send(openSign);
    }

    @Override
    public void onUpdateSign(Player player, Function<String[], Boolean> consumer) {
        ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline().addBefore("packet_handler", "tippieutils_signgui", new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                if (msg instanceof ServerboundSignUpdatePacket packet) {
                    if (consumer.apply(packet.getLines()))
                        ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline().remove(this);
                }
            }
        });
    }

}
