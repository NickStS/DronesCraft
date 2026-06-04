package com.example.basicdrone.block.entity;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.GeneratorBlock;
import com.example.basicdrone.menu.GeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratorBlockEntity extends BlockEntity implements Container, MenuProvider {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private final ContainerData fuelData = new SimpleContainerData(2);

    private static final int MAX_ENERGY = 10000;
    private static final int MAX_TRANSFER = 256;
    private final EnergyStorage storage = new EnergyStorage(MAX_ENERGY, MAX_TRANSFER, MAX_TRANSFER);
    private final LazyOptional<net.minecraftforge.energy.IEnergyStorage> energyCap =
            LazyOptional.of(() -> storage);

    private int burnTime = 0;
    private int burnTotal = 0;
    private boolean prevLit = false;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(Registries.GENERATOR_BLOCK_ENTITY.get(), pos, state);
    }


    public static <T extends BlockEntity> void tick(
            Level level, BlockPos pos, BlockState state, GeneratorBlockEntity tile
    ) {
        if (level.isClientSide) return;

        if (tile.burnTime > 0) {
            tile.burnTime--;
            tile.storage.receiveEnergy(40, false);
        }
        else if (!tile.items.get(0).isEmpty()) {
            int time = ForgeHooks.getBurnTime(tile.items.get(0), null);
            if (time > 0) {
                tile.burnTotal = tile.burnTime = time;
                tile.items.get(0).shrink(1);
            }
        }

        tile.fuelData.set(0, tile.burnTime);
        tile.fuelData.set(1, tile.burnTotal);
        boolean lit = tile.burnTime > 0;
        if (state.getValue(GeneratorBlock.LIT) != lit) {
            level.setBlock(pos, state.setValue(GeneratorBlock.LIT, lit), 3);
        }

        for (Direction d : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(d));
            if (neighbor == null) continue;

            neighbor.getCapability(CapabilityEnergy.ENERGY, d.getOpposite()).ifPresent(ext -> {
                int canSend = tile.storage.extractEnergy(MAX_TRANSFER, true);
                if (canSend > 0) {
                    int sent = ext.receiveEnergy(canSend, false);
                    tile.storage.extractEnergy(sent, false);
                    tile.setChanged();
                }
            });
        }
    }

    public static <T extends BlockEntity> void clientTick(
            Level level, BlockPos pos, BlockState state, GeneratorBlockEntity tile
    ) {
        if (!level.isClientSide) return;

        boolean lit = state.getValue(GeneratorBlock.LIT);
        if (lit && !tile.prevLit) {
            level.playLocalSound(
                    pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5,
                    Registries.GENERATOR_WORKING.get(),
                    SoundSource.BLOCKS,
                    1.0f, 1.0f, false
            );
        }
        tile.prevLit = lit;
    }

    @Override public int getContainerSize()                   { return items.size(); }
    @Override public boolean isEmpty()                        { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot)              { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int amt)  { return ContainerHelper.removeItem(items, slot, amt); }
    @Override public ItemStack removeItemNoUpdate(int slot)   { return ContainerHelper.takeItem(items, slot); }
    @Override public void setItem(int slot, ItemStack stack)  { items.set(slot, stack); setChanged(); }
    @Override public boolean stillValid(Player p)             { return true; }
    @Override public void clearContent()                      { items.clear(); }

    public ContainerData getContainerData() { return fuelData; }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new GeneratorMenu(id, inv, this);
    }

    @Override public Component getDisplayName() {
        return new TranslatableComponent("container.basicdrone.generator");
    }

    @Override @NotNull
    public <U> LazyOptional<U> getCapability(
            @NotNull net.minecraftforge.common.capabilities.Capability<U> cap,
            @Nullable net.minecraft.core.Direction side
    ) {
        if (cap == CapabilityEnergy.ENERGY) return energyCap.cast();
        return super.getCapability(cap, side);
    }
}
