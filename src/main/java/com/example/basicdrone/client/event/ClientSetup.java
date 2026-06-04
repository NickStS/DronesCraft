package com.example.basicdrone.client.event;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class ClientSetup {
    public static KeyMapping TOGGLE_DRONE;

    public static void onClientSetup(FMLClientSetupEvent event) {
        TOGGLE_DRONE = new KeyMapping(
                "key.basicdrone.toggle_drone",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.basicdrone"
        );
        ClientRegistry.registerKeyBinding(TOGGLE_DRONE);
    }
}