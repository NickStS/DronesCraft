package com.example.basicdrone.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemRenderProperties;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import com.example.basicdrone.item.client.DroneItemClient;

import java.util.function.Consumer;

public class DroneAnimatableItem extends Item implements IAnimatable {
    @SuppressWarnings("removal")
    private final AnimationFactory factory = new AnimationFactory(this);


    public DroneAnimatableItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(DroneItemClient.initProps());
    }
}
