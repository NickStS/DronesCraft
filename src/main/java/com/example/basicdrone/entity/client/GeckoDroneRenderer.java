package com.example.basicdrone.entity.client;

import com.example.basicdrone.entity.DroneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class    GeckoDroneRenderer extends GeoEntityRenderer<DroneEntity> {

    public GeckoDroneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GeckoDroneModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public RenderType getRenderType(DroneEntity animatable,
                                    float partialTicks,
                                    PoseStack stack,
                                    MultiBufferSource buffer,
                                    VertexConsumer vertexBuilder,
                                    int packedLight,
                                    ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
