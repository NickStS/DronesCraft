package com.example.basicdrone.client.event;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.Registries;
import com.example.basicdrone.block.entity.client.DroneBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = BasicDroneMod.MODID,
        bus   = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientEventHandlers {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent evt) {
        BlockEntityRenderers.register(
                Registries.DRONE_BLOCK_ENTITY.get(),
                DroneBlockRenderer::new
        );
    }
}
