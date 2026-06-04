package com.example.basicdrone.server;

import com.example.basicdrone.Registries;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DroneEntityManager extends SavedData {
    private static final String DATA_NAME = "basicdrone_drone_manager";

    private final Map<UUID, DroneEntity> active = new ConcurrentHashMap<>();

    private final ServerLevel level;

    private DroneEntityManager(ServerLevel level) {
        this.level = level;
    }

    public static DroneEntityManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(
                (CompoundTag nbt) -> {
                    return new DroneEntityManager(level);
                },
                () -> new DroneEntityManager(level),
                DATA_NAME
        );
    }

    public void toggleFlightFor(ServerPlayer player) {
        UUID id = player.getUUID();
        if (active.containsKey(id)) {
            DroneEntity drone = active.remove(id);
            if (drone != null && !drone.isRemoved()) {
                drone.discard();
            }
        } else {
            DroneEntity drone = Registries.DRONE_ENTITY.get().create(level);
            drone.moveTo(
                    player.getX(),
                    player.getY() + 1.5,
                    player.getZ(),
                    player.getYRot(),
                    player.getXRot()
            );
            level.addFreshEntity(drone);
            drone.startFlying(player);
            active.put(id, drone);
        }
        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return tag;
    }
}
