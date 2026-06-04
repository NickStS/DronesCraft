package com.example.basicdrone.menu;

import com.example.basicdrone.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class RemoteControllerMenu extends AbstractContainerMenu {
    private final int batteryTicks;
    private final boolean flying;


    public RemoteControllerMenu(int windowId,
                                Inventory inv,
                                FriendlyByteBuf buf) {
        super(Registries.REMOTE_CONTROLLER_MENU.get(), windowId);
        this.batteryTicks = buf.readInt();
        this.flying      = buf.readBoolean();
    }

    public RemoteControllerMenu(int windowId,
                                Inventory inv,
                                int batteryTicks,
                                boolean flying) {
        super(Registries.REMOTE_CONTROLLER_MENU.get(), windowId);
        this.batteryTicks = batteryTicks;
        this.flying       = flying;
    }

    public int  getBatteryTicks() { return batteryTicks; }
    public int  getMaxBatteryTicks() { return com.example.basicdrone.entity.DroneEntity.MAX_BATTERY_TICKS; }
    public boolean isFlying()      { return flying; }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player p) {
        return true;
    }
}
