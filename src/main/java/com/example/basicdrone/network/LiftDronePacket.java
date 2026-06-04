package com.example.basicdrone.network;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.example.basicdrone.entity.DroneEntity;
import com.example.basicdrone.item.RemoteControllerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class LiftDronePacket {

    public static void encode(LiftDronePacket p, net.minecraft.network.FriendlyByteBuf b) {}
    public static LiftDronePacket decode(net.minecraft.network.FriendlyByteBuf b) {
        return new LiftDronePacket();
    }

    public static void handle(LiftDronePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof RemoteControllerItem)) {
                stack = player.getOffhandItem();
                if (!(stack.getItem() instanceof RemoteControllerItem)) return;
            }

            DroneEntity d = DroneEntity.getForPlayer(player);

            if (d != null && d.isFlying()) {
                return;
            }

            if (stack.getOrCreateTag().contains(RemoteControllerItem.TAG_LINKED_DRONE_POS)) {
                BlockPos pos = BlockPos.of(stack.getOrCreateTag().getLong(RemoteControllerItem.TAG_LINKED_DRONE_POS));
                if (player.level.getBlockEntity(pos) instanceof DroneBlockEntity be) {

                    int battery = be.getBatteryTicks();

                    player.level.removeBlockEntity(pos);
                    player.level.removeBlock(pos, false);

                    d = Registries.DRONE_ENTITY.get().create(player.level);
                    if (d != null) {
                        d.setOriginPos(pos);
                        d.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                player.getYRot(), player.getXRot());
                        d.setBatteryTicks(battery);
                        player.level.addFreshEntity(d);
                        d.startFlying(player);

                        stack.getOrCreateTag().putUUID(RemoteControllerItem.TAG_LINKED_DRONE_UUID, d.getUUID());
                        stack.getOrCreateTag().remove(RemoteControllerItem.TAG_LINKED_DRONE_POS);
                    }
                }
            }

            if (d == null) {
                BlockPos base = player.blockPosition();
                for (BlockPos pos : BlockPos.betweenClosed(
                        base.offset(-2, -1, -2),
                        base.offset(2, 2, 2))) {

                    if (player.level.getBlockEntity(pos) instanceof DroneBlockEntity be) {

                        int battery = be.getBatteryTicks();

                        player.level.removeBlockEntity(pos);
                        player.level.removeBlock(pos, false);

                        d = Registries.DRONE_ENTITY.get().create(player.level);
                        if (d != null) {
                            d.setOriginPos(pos);
                            d.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                                    player.getYRot(), player.getXRot());
                            d.setBatteryTicks(battery);
                            player.level.addFreshEntity(d);
                            d.startFlying(player);

                            stack.getOrCreateTag().putUUID(RemoteControllerItem.TAG_LINKED_DRONE_UUID, d.getUUID());
                            stack.getOrCreateTag().remove(RemoteControllerItem.TAG_LINKED_DRONE_POS);
                        }
                        break;
                    }
                }
            }

            if (d != null && !d.isFlying()) d.startFlying(player);

        });
        ctx.get().setPacketHandled(true);
    }

}
