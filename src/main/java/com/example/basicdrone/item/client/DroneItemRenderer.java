package com.example.basicdrone.item.client;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.item.DroneAnimatableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DroneItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final Renderer renderer;

    public DroneItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.renderer = new Renderer();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (stack.getItem() instanceof DroneAnimatableItem item) {
            renderer.render(item, poseStack, buffer, packedLight, stack);
        }
    }

    private static class Renderer extends GeoItemRenderer<DroneAnimatableItem> {
        public Renderer() {
            super(new Model());
        }
    }

    private static class Model extends AnimatedGeoModel<DroneAnimatableItem> {
        @Override
        public ResourceLocation getModelLocation(DroneAnimatableItem animatable) {
            return ResourceLocation.parse(BasicDroneMod.MODID + ":geo/drone.geo.json");
        }
        @Override
        public ResourceLocation getTextureLocation(DroneAnimatableItem animatable) {
            return ResourceLocation.parse(BasicDroneMod.MODID + ":textures/entity/drone.png");
        }
        @Override
        public ResourceLocation getAnimationFileLocation(DroneAnimatableItem animatable) {
            return ResourceLocation.parse(BasicDroneMod.MODID + ":animations/drone.animation.json");
        }
    }
}
