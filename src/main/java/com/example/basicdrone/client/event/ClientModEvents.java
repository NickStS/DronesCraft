package com.example.basicdrone.client.event;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.Registries;
import com.example.basicdrone.entity.client.GeckoDroneRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = BasicDroneMod.MODID,
        value = Dist.CLIENT,
        bus   = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientModEvents {
    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers evt) {
        evt.registerEntityRenderer(
                Registries.DRONE_ENTITY.get(),
                GeckoDroneRenderer::new
        );
    }

    @SubscribeEvent
    public static void onClientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    Registries.CABLE_BLOCK.get(),
                    RenderType.cutout()
            );
        });
    }
}
