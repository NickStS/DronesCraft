package com.example.basicdrone.item.client;

import net.minecraftforge.client.IItemRenderProperties;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public class DroneItemClient {
    public static IItemRenderProperties initProps() {
        return new IItemRenderProperties() {
            private final BlockEntityWithoutLevelRenderer renderer = new DroneItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return renderer;
            }
        };
    }
}