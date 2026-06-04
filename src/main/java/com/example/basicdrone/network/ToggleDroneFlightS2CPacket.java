package com.example.basicdrone.network;

import com.example.basicdrone.server.DroneManager;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleDroneFlightS2CPacket {
    public ToggleDroneFlightS2CPacket() {}

    public static void encode(ToggleDroneFlightS2CPacket pkt, net.minecraft.network.FriendlyByteBuf buf) {
    }

    public static ToggleDroneFlightS2CPacket decode(net.minecraft.network.FriendlyByteBuf buf) {
        return new ToggleDroneFlightS2CPacket();
    }

    public static void handle(ToggleDroneFlightS2CPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            DroneEntity drone = DroneManager.get(player);
            if (drone != null) {
                drone.toggleFlying(player);
            } else {
                DroneEntity newDrone = DroneManager.spawnFor(player);
                newDrone.startFlying(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
