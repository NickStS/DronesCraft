package com.example.basicdrone.network;

import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class LandDronePacket {

    public static void encode(LandDronePacket p, net.minecraft.network.FriendlyByteBuf b) {}
    public static LandDronePacket decode(net.minecraft.network.FriendlyByteBuf b) {
        return new LandDronePacket();
    }

    public static void handle(LandDronePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            DroneEntity d = DroneEntity.getForPlayer(player);
            if (d != null && d.isFlying()) d.land();
        });
        ctx.get().setPacketHandled(true);
    }
}
