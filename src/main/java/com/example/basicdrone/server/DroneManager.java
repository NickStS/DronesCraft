package com.example.basicdrone.server;

import com.example.basicdrone.Registries;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DroneManager {
    private static final Map<UUID, DroneEntity> ACTIVE = new ConcurrentHashMap<>();

    public static DroneEntity spawnFor(ServerPlayer player) {
        if (ACTIVE.containsKey(player.getUUID())) {
            return ACTIVE.get(player.getUUID());
        }

        var lvl = player.getLevel();
        var drone = Registries.DRONE_ENTITY.get().create(lvl);
        drone.moveTo(
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                player.getYRot(),
                0f
        );
        lvl.addFreshEntity(drone);
        ACTIVE.put(player.getUUID(), drone);
        return drone;
    }

    public static void removeFor(ServerPlayer player) {
        DroneEntity drone = ACTIVE.remove(player.getUUID());
        if (drone != null && !drone.isRemoved()) {
            drone.discard();

            ItemStack stack = new ItemStack(Registries.REMOTE_CONTROLLER_ITEM.get());
            Inventory inv = player.getInventory();
            if (!inv.add(stack)) {
                player.drop(stack, false);
            }
        }
    }

    public static DroneEntity get(ServerPlayer player) {
        return ACTIVE.get(player.getUUID());
    }
}
