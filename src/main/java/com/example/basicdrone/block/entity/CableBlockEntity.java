package com.example.basicdrone.block.entity;

import com.example.basicdrone.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class CableBlockEntity extends BlockEntity {
    private static final int CAPACITY = 1024;
    private static final int TRANSFER = 256;

    private final EnergyStorage storage = new EnergyStorage(CAPACITY, TRANSFER, TRANSFER);
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> storage);

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(Registries.CABLE_BLOCK_ENTITY.get(), pos, state);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, CableBlockEntity cable) {
        if (level.isClientSide) return;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            neighborBE.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).ifPresent(ext -> {
                int canReceive = cable.storage.receiveEnergy(TRANSFER, true);
                if (canReceive > 0) {
                    int extracted = ext.extractEnergy(canReceive, false);
                    if (extracted > 0) {
                        cable.storage.receiveEnergy(extracted, false);
                        cable.setChanged();
                    }
                }
            });
        }

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            int canExtract = cable.storage.extractEnergy(TRANSFER, true);
            if (canExtract <= 0) continue;

            neighborBE.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).ifPresent(ext -> {
                int received = ext.receiveEnergy(canExtract, false);
                if (received > 0) {
                    cable.storage.extractEnergy(received, false);
                    cable.setChanged();
                }
            });
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            net.minecraftforge.common.capabilities.Capability<T> capRequest,
            Direction side) {
        if (capRequest == CapabilityEnergy.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(capRequest, side);
    }
}
