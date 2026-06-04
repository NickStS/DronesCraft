package com.example.basicdrone.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DroneStatusPacket {
    private final int batteryTicks;
    private final boolean flying;

    public DroneStatusPacket(int batteryTicks, boolean flying) {
        this.batteryTicks = batteryTicks;
        this.flying = flying;
    }

    public static void encode(DroneStatusPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.batteryTicks);
        buf.writeBoolean(msg.flying);
    }

    public static DroneStatusPacket decode(FriendlyByteBuf buf) {
        return new DroneStatusPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(DroneStatusPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        });
        ctx.get().setPacketHandled(true);
    }
}
