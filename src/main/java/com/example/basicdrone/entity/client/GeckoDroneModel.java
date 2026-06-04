package com.example.basicdrone.entity.client;

import com.example.basicdrone.BasicDroneMod;
import com.example.basicdrone.entity.DroneEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class GeckoDroneModel extends AnimatedGeoModel<DroneEntity> {

	@Override
	public ResourceLocation getModelLocation(DroneEntity animatable) {
		return ResourceLocation.parse(
				BasicDroneMod.MODID + ":geo/drone.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(DroneEntity animatable) {
		return ResourceLocation.parse(
				BasicDroneMod.MODID + ":textures/entity/drone.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(DroneEntity animatable) {
		return ResourceLocation.parse(
				BasicDroneMod.MODID + ":animations/drone.animation.json");
	}
}
