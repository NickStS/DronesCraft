package com.example.basicdrone.event;

import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "basicdrone")
public class DronePlayerEvents {

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent ev) {
        if (!(ev.getPlayer() instanceof ServerPlayer)) return;
        ServerPlayer sp = (ServerPlayer) ev.getPlayer();
        DroneEntity d = DroneEntity.getForPlayer(sp);
        if (d != null && d.isFlying()) {
            d.forceLand();
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent ev) {
        if (!(ev.getPlayer() instanceof ServerPlayer)) return;
        ServerPlayer sp = (ServerPlayer) ev.getPlayer();
        DroneEntity d = DroneEntity.getForPlayer(sp);
        if (d != null && d.isFlying()) {
            d.forceLand();
        }
    }
}
