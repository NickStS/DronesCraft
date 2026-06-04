package com.example.basicdrone;

import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.stream.Stream;


public final class DroneSpawner {
    private DroneSpawner() {}

    public static void findOrSpawnAll(ServerPlayer player) {
        Level level = player.level;
        BlockPos center = player.blockPosition();


        try (Stream<BlockPos> positions = BlockPos.betweenClosedStream(
                center.offset(-12, -2, -12),
                center.offset( 12,  6,  12)))
        {
            positions.forEach(pos -> {
                if (!(level.getBlockEntity(pos) instanceof DroneBlockEntity be)) return;

                int battery = be.getBatteryTicks();

                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);

                DroneEntity drone = Registries.DRONE_ENTITY.get().create(level);
                if (drone == null) return;

                drone.setBatteryTicks(battery);
                drone.moveTo(
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        player.getYRot(), player.getXRot()
                );
                level.addFreshEntity(drone);

                drone.startFlying(player);
            });
        }
    }
}
