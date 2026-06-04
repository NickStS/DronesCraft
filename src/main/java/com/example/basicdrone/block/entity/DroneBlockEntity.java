package com.example.basicdrone.block.entity;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.DroneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.*;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

@SuppressWarnings({"deprecation", "removal"})
public class DroneBlockEntity extends BlockEntity implements IAnimatable {

    public  static final int MAX_BATTERY_TICKS = 20 * 60 * 20;
    private static final String TAG_BATTERY   = "Battery";

    private int batteryTicks = MAX_BATTERY_TICKS;

    public int  getBatteryTicks()            { return batteryTicks; }
    public void setBatteryTicks(int ticks) {
        batteryTicks = Mth.clamp(ticks, 0, MAX_BATTERY_TICKS);
        setChanged();
    }

    public DroneBlockEntity(BlockPos pos, BlockState state) {
        super(Registries.DRONE_BLOCK_ENTITY.get(), pos, state);
    }

    private final AnimationFactory factory = new AnimationFactory(this);

    @Override public AnimationFactory getFactory() { return factory; }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(
                this, "spin_controller", 2, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> c = event.getController();
        if (isSpinning()) {
            c.setAnimation(new AnimationBuilder().addAnimation("spin", true));
            return PlayState.CONTINUE;
        }
        c.clearAnimationCache();
        return PlayState.STOP;
    }

    private boolean isSpinning() {
        if (level == null) return false;
        BlockState st = level.getBlockState(worldPosition);
        return st.hasProperty(DroneBlock.ACTIVE) && st.getValue(DroneBlock.ACTIVE);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(TAG_BATTERY, batteryTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(TAG_BATTERY))
            batteryTicks = tag.getInt(TAG_BATTERY);
    }
}
