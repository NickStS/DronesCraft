package com.example.basicdrone.client;

import com.example.basicdrone.network.NetworkHandler;
import com.example.basicdrone.network.ToggleDronePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping TOGGLE_DRONE = new KeyMapping(
            "key.basicdrone.toggle_drone",
            KeyConflictContext.IN_GAME,
            KeyModifier.NONE,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.basicdrone"
    );

    public static void register() {
        ClientRegistry.registerKeyBinding(TOGGLE_DRONE);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (TOGGLE_DRONE.consumeClick()) {
            NetworkHandler.CHANNEL.sendToServer(new ToggleDronePacket());
        }
    }
}
