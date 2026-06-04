package com.example.basicdrone.network;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class DroneNetUtil {

    private DroneNetUtil() {}

    public static Optional<DroneEntity> findOrSpawn(ServerPlayer p) {

        DroneEntity d = DroneEntity.getForPlayer(p);
        if (d != null) return Optional.of(d);

        BlockPos base = p.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(
                base.offset(-2, -1, -2),
                base.offset( 2,  2,  2))) {

            if (p.level.getBlockEntity(pos) instanceof DroneBlockEntity) {
                p.level.removeBlockEntity(pos);
                p.level.removeBlock(pos, false);

                d = Registries.DRONE_ENTITY.get().create(p.level);
                if (d == null) break;

                d.moveTo(pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
                        p.getYRot(), p.getXRot());
                p.level.addFreshEntity(d);
                d.startFlying(p);
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }
}
