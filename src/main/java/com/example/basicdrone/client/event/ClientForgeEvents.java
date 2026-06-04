package com.example.basicdrone.client.event;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.client.KeyBindings;
import com.example.basicdrone.network.NetworkHandler;
import com.example.basicdrone.network.ToggleDronePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = BasicDroneMod.MODID,
        value = Dist.CLIENT,
        bus   = Mod.EventBusSubscriber.Bus.FORGE
)
public class ClientForgeEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent ev) {
        if (KeyBindings.TOGGLE_DRONE.consumeClick()) {
            NetworkHandler.CHANNEL.sendToServer(new ToggleDronePacket());
        }
    }
}
