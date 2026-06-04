package com.example.basicdrone.menu;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.entity.EnergyStorageBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class EnergyStorageMenu extends AbstractContainerMenu {
    private final EnergyStorageBlockEntity tile;
    private final ContainerData data;

    public EnergyStorageMenu(int id, Inventory playerInv, EnergyStorageBlockEntity tile) {
        super(Registries.ENERGY_STORAGE_MENU.get(), id);
        this.tile = tile;

        this.data = tile.getContainerData();
        this.addDataSlots(this.data);

        int guiLeft = 40;
        int guiTop = 45;

        int startX = guiLeft + 8;
        int startY = guiTop + 84;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9,
                        startX + col * 18, startY + row * 18));
            }
        }
        for (int hot = 0; hot < 9; hot++) {
            this.addSlot(new Slot(playerInv, hot,
                    startX + hot * 18, startY + 58));
        }
    }


    public int getEnergy() {
        return data.get(0);
    }

    public int getMaxEnergy() {
        return data.get(1);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            if (!this.moveItemStackTo(stack, 0, this.slots.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
            return stack;
        }
        return ItemStack.EMPTY;
    }
}
