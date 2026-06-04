package com.example.basicdrone.block.entity;

import com.example.basicdrone.Registries;
import com.example.basicdrone.menu.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;


public class EnergyStorageBlockEntity extends BlockEntity implements MenuProvider {
    private static final int MAX_ENERGY   = 100_000;
    private static final int MAX_TRANSFER = 256;

    private final ContainerData containerData = new SimpleContainerData(2);

    private final EnergyStorage storage = new EnergyStorage(MAX_ENERGY, MAX_TRANSFER, MAX_TRANSFER);
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> storage);

    public EnergyStorageBlockEntity(BlockPos pos, BlockState state) {
        super(Registries.ENERGY_STORAGE_BLOCK_ENTITY.get(), pos, state);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, EnergyStorageBlockEntity tile) {
        if (level.isClientSide) return;

        tile.containerData.set(0, tile.storage.getEnergyStored());
        tile.containerData.set(1, tile.storage.getMaxEnergyStored());

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            neighborBE.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).ifPresent(ext -> {
                int canReceive = tile.storage.receiveEnergy(MAX_TRANSFER, true);
                if (canReceive > 0) {
                    int extracted = ext.extractEnergy(canReceive, false);
                    if (extracted > 0) {
                        tile.storage.receiveEnergy(extracted, false);
                        tile.setChanged();
                    }
                }
            });
        }

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockEntity neighborBE = level.getBlockEntity(neighborPos);
            if (neighborBE == null) continue;

            int canExtract = tile.storage.extractEnergy(MAX_TRANSFER, true);
            if (canExtract <= 0) continue;

            neighborBE.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).ifPresent(ext -> {
                int received = ext.receiveEnergy(canExtract, false);
                if (received > 0) {
                    tile.storage.extractEnergy(received, false);
                    tile.setChanged();
                }
            });
        }
    }


    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(
            @NotNull net.minecraftforge.common.capabilities.Capability<T> capRequest,
            @Nullable Direction side) {
        if (capRequest == CapabilityEnergy.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(capRequest, side);
    }

    public ContainerData getContainerData() {
        return containerData;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new EnergyStorageMenu(id, inv, this);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.basicdrone.energy_storage");
    }
}
