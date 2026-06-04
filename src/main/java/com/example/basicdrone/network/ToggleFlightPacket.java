package com.example.basicdrone.network;

import com.example.basicdrone.server.DroneEntityManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleFlightPacket {
    public ToggleFlightPacket() {}

    public static void encode(ToggleFlightPacket msg, FriendlyByteBuf buf) {
    }

    public static ToggleFlightPacket decode(FriendlyByteBuf buf) {
        return new ToggleFlightPacket();
    }

    public static void handle(ToggleFlightPacket msg,
                              Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ServerLevel lvl = (ServerLevel) player.getLevel();
            DroneEntityManager.get(lvl)
                    .toggleFlightFor(player);
        });
        ctx.setPacketHandled(true);
    }
}
