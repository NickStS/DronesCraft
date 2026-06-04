package com.example.basicdrone.server;

import com.example.basicdrone.Registries;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.server.level.ServerPlayer;

import java.util.WeakHashMap;
import java.util.UUID;


@Mod.EventBusSubscriber
public final class DroneKeeper {
    private static final WeakHashMap<UUID, DroneEntity> LAST_KNOWN = new WeakHashMap<>();

    public static void update(ServerPlayer player, DroneEntity drone) {
        LAST_KNOWN.put(player.getUUID(), drone);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level.isClientSide) return;
        if (event.player.tickCount % 100 != 0) return;

        if (!(event.player instanceof ServerPlayer player)) return;

        DroneEntity current = DroneEntity.getForPlayer(player);
        if (current != null && !current.isRemoved()) return;

        DroneEntity last = LAST_KNOWN.get(player.getUUID());
        if (last == null) return;

        DroneEntity resurrected = Registries.DRONE_ENTITY.get().create(player.level);
        if (resurrected == null) return;

        resurrected.setBatteryTicks(last.getBatteryTicks());
        resurrected.moveTo(
                player.getX(),
                player.getY() + 1.5,
                player.getZ(),
                player.getYRot(),
                player.getXRot()
        );
        player.level.addFreshEntity(resurrected);
        resurrected.startFlying(player);
    }
}
