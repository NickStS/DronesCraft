package com.example.basicdrone.item;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class DroneItem extends BlockItem {

    public DroneItem(Block block) {
        super(block, new Item.Properties()
                .stacksTo(1)
                .tab(BasicDroneMod.TAB)
        );
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        if (!stack.getOrCreateTag().contains("Battery")) {
            stack.getOrCreateTag().putInt("Battery", DroneEntity.MAX_BATTERY_TICKS);
        }
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int b = stack.getOrCreateTag().getInt("Battery");
        return Math.round(13f * b / DroneEntity.MAX_BATTERY_TICKS);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00;
    }

    @Override
    public Component getName(ItemStack stack) {
        int b = stack.getOrCreateTag().getInt("Battery");
        MutableComponent name = new TranslatableComponent(this.getDescriptionId(stack));
        name.append(new TextComponent(" [" + b + "/" + DroneEntity.MAX_BATTERY_TICKS + "]"));
        return name;
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                @Nullable Level world,
                                List<Component> tooltip,
                                TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        int b = stack.getOrCreateTag().getInt("Battery");
        int pct = Math.round(b * 100f / DroneEntity.MAX_BATTERY_TICKS);
        tooltip.add(new TextComponent("Заряд: " + pct + "%"));
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        super.onCraftedBy(stack, world, player);
        stack.getOrCreateTag().putInt("Battery", DroneEntity.MAX_BATTERY_TICKS);
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == BasicDroneMod.TAB) {
            ItemStack full = new ItemStack(this);
            full.getOrCreateTag().putInt("Battery", DroneEntity.MAX_BATTERY_TICKS);
            items.add(full);
            ItemStack empty = new ItemStack(this);
            empty.getOrCreateTag().putInt("Battery", 0);
            items.add(empty);
        }
    }
}
