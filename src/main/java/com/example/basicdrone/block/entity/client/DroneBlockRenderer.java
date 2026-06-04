package com.example.basicdrone.block.entity.client;

import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class DroneBlockRenderer extends GeoBlockRenderer<DroneBlockEntity> {
    public DroneBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, new DroneBlockModel());
    }

    @Override
    public RenderType getRenderType(DroneBlockEntity animatable,
                                    float partialTicks,
                                    PoseStack stack,
                                    MultiBufferSource buffer,
                                    VertexConsumer builder,
                                    int packedLight,
                                    ResourceLocation texture) {
        return RenderType.entityTranslucent(this.getTextureLocation(animatable));
    }
}
