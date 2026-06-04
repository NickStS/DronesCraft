package com.example.basicdrone.network;

import com.example.basicdrone.Registries;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import java.util.function.Supplier;

public class ToggleDronePacket {
    public ToggleDronePacket() {}

    public static void encode(ToggleDronePacket msg, FriendlyByteBuf buf) {}

    public static ToggleDronePacket decode(FriendlyByteBuf buf) {
        return new ToggleDronePacket();
    }

    public static void handle(ToggleDronePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            DroneEntity d = DroneEntity.getForPlayer(player);

            if (d == null) {
                d = Registries.DRONE_ENTITY.get().create(player.level);
                if (d == null) return;

                d.moveTo(player.getX(), player.getY()+1.5, player.getZ(),
                        player.getYRot(), player.getXRot());
                player.level.addFreshEntity(d);
                d.startFlying(player);
            } else {
                d.toggleFlying(player);
            }

            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new DroneStatusPacket(d.getBatteryTicks(), d.isFlying())
            );
        });
        ctx.get().setPacketHandled(true);
    }

}