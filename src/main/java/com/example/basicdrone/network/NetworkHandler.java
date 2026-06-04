package com.example.basicdrone.network;

import com.example.basicdrone.BasicDroneMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


public final class NetworkHandler {
    private static final String PROTOCOL = "1.0";
    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    ResourceLocation.parse(BasicDroneMod.MODID + ":main"),
                    () -> PROTOCOL,
                    PROTOCOL::equals,
                    PROTOCOL::equals
            );

    private static int id = 0;

    public static void init() {
        CHANNEL.messageBuilder(ToggleDronePacket.class, id++)
                .encoder((msg, buf) -> {})
                .decoder(buf -> new ToggleDronePacket())
                .consumer(ToggleDronePacket::handle)
                .add();

        CHANNEL.messageBuilder(LiftDronePacket.class, id++)
                .encoder((msg, buf) -> {})
                .decoder(buf -> new LiftDronePacket())
                .consumer(LiftDronePacket::handle)
                .add();

        CHANNEL.messageBuilder(LandDronePacket.class, id++)
                .encoder((msg, buf) -> {})
                .decoder(buf -> new LandDronePacket())
                .consumer(LandDronePacket::handle)
                .add();

        CHANNEL.messageBuilder(DroneStatusPacket.class, id++)
                .encoder(DroneStatusPacket::encode)
                .decoder(DroneStatusPacket::decode)
                .consumer(DroneStatusPacket::handle)
                .add();
    }
}
