package com.example.basicdrone.block.entity;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.ChargingStationBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.concurrent.atomic.AtomicBoolean;


public class ChargingStationBlockEntity extends BlockEntity {
    private int chargingTicks = 0;
    private boolean chargingNow = false;
    private BlockPos chargingDronePos = null;
    private boolean droneWasNotFull = false;

    private boolean prevCharging = false;

    public ChargingStationBlockEntity(BlockPos pos, BlockState state) {
        super(Registries.CHARGING_STATION_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isChargingNow() {
        return chargingNow;
    }

    private void setChargingNow(boolean charging) {
        this.chargingNow = charging;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChargingStationBlockEntity tile) {
        if (level.isClientSide) return;

        AtomicBoolean needPowered = new AtomicBoolean(false);
        for (Direction dir : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
            if (neighbor == null) continue;
            neighbor.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite())
                    .ifPresent(cap -> {
                        if (cap.getEnergyStored() > 0) {
                            needPowered.set(true);
                        }
                    });
        }

        boolean charging = false;
        BlockPos above = pos.above();
        BlockEntity be = level.getBlockEntity(above);

        if (be instanceof com.example.basicdrone.block.entity.DroneBlockEntity droneBe) {
            int maxTicks = com.example.basicdrone.block.entity.DroneBlockEntity.MAX_BATTERY_TICKS;

            if (tile.chargingDronePos == null || !tile.chargingDronePos.equals(above)) {
                tile.chargingTicks = 0;
                tile.chargingDronePos = above.immutable();
                tile.droneWasNotFull = droneBe.getBatteryTicks() < maxTicks;
            }

            int totalEnergyToFull = 10000;
            int ticksToFull      = 5 * 20;
            int energyPerTick    = totalEnergyToFull / ticksToFull;

            boolean energyReceived = false;

            for (Direction dir : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(pos.relative(dir));
                if (neighbor == null) continue;

                LazyOptional<IEnergyStorage> capOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
                if (capOpt.isPresent()) {
                    IEnergyStorage storage = capOpt.orElse(null);
                    if (storage != null && storage.getEnergyStored() >= energyPerTick) {
                        int extracted = storage.extractEnergy(energyPerTick, false);
                        if (extracted >= energyPerTick) {
                            energyReceived = true;
                            break;
                        }
                    }
                }
            }

            if (energyReceived && droneBe.getBatteryTicks() < maxTicks) {
                tile.chargingTicks++;
                charging = true;
            }

            if (droneBe.getBatteryTicks() < maxTicks) {
                tile.droneWasNotFull = true;
            }

            if (tile.chargingTicks >= ticksToFull) {
                if (tile.droneWasNotFull && droneBe.getBatteryTicks() < maxTicks) {
                    droneBe.setBatteryTicks(maxTicks);
                    level.playSound(null,
                            above,
                            Registries.DRONE_CHARGED.get(),
                            SoundSource.PLAYERS,
                            1.0f, 1.0f
                    );
                }
                tile.chargingTicks = 0;
                tile.chargingDronePos = null;
                tile.droneWasNotFull = false;
            }
        } else {
            tile.chargingTicks = 0;
            tile.chargingDronePos = null;
            tile.droneWasNotFull = false;
        }

        tile.setChargingNow(charging);

        boolean shouldBePowered = charging || needPowered.get();
        if (shouldBePowered != state.getValue(ChargingStationBlock.POWERED)) {
            level.setBlock(pos, state.setValue(ChargingStationBlock.POWERED, shouldBePowered), 3);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ChargingStationBlockEntity tile) {
        if (!level.isClientSide) return;

        boolean powered = state.getValue(ChargingStationBlock.POWERED);
        BlockEntity be = level.getBlockEntity(pos.above());
        boolean shouldCharge = false;

        if (powered && be instanceof com.example.basicdrone.block.entity.DroneBlockEntity droneBe) {
            int maxTicks = com.example.basicdrone.block.entity.DroneBlockEntity.MAX_BATTERY_TICKS;
            shouldCharge = droneBe.getBatteryTicks() < maxTicks;
        }

        if (shouldCharge && !tile.prevCharging) {
            level.playLocalSound(
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    Registries.CHARGER_WORKING.get(),
                    SoundSource.AMBIENT,
                    1.0f, 1.0f,
                    false
            );
        }

        tile.prevCharging = shouldCharge;
    }
}
