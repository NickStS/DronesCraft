package com.example.basicdrone.block.entity.client;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.block.entity.DroneBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class DroneBlockModel extends AnimatedGeoModel<DroneBlockEntity> {
    @Override
    public ResourceLocation getModelLocation(DroneBlockEntity object) {
        return ResourceLocation.tryParse(BasicDroneMod.MODID + ":geo/drone.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(DroneBlockEntity object) {
        return ResourceLocation.tryParse(BasicDroneMod.MODID + ":textures/block/drone.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(DroneBlockEntity animatable) {
        return ResourceLocation.tryParse(BasicDroneMod.MODID + ":animations/drone.animation.json");
    }
}
