package com.example.basicdrone.menu;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.entity.GeneratorBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

public class GeneratorMenu extends AbstractContainerMenu {
    private final GeneratorBlockEntity tile;
    private final ContainerData data;

    public GeneratorMenu(int id, Inventory playerInv, GeneratorBlockEntity tile) {
        super(Registries.GENERATOR_MENU.get(), id);
        this.tile = tile;
        this.data = tile.getContainerData();
        this.addDataSlots(this.data);

        int guiLeft = 40;
        int guiTop = 45;

        this.addSlot(new Slot(tile, 0, guiLeft + 80, guiTop + 43) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ForgeHooks.getBurnTime(stack, null) > 0;
            }
        });

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

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem().copy();
            if (index == 0) {
                if (!moveItemStackTo(stack, 1, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else {
                if (ForgeHooks.getBurnTime(stack, null) > 0) {
                    if (!moveItemStackTo(stack, 0, 1, false))
                        return ItemStack.EMPTY;
                } else if (!moveItemStackTo(stack, 1, this.slots.size(), false))
                    return ItemStack.EMPTY;
            }
            slot.set(stack.isEmpty() ? ItemStack.EMPTY : stack);
            slot.setChanged();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public int getFuelTimeRemaining() {
        return this.data.get(0);
    }

    public int getFuelTimeTotal() {
        return this.data.get(1);
    }
}
