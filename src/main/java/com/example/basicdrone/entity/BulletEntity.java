package com.example.basicdrone.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class BulletEntity extends AbstractArrow {

    public BulletEntity(Level lvl, LivingEntity shooter) {
        super(EntityType.ARROW, shooter, lvl);

        setInvisible(true);
        setSilent(true);
        setBaseDamage(4);
        setKnockback(0);
        setCritArrow(false);
        setPierceLevel((byte)3);
    }

    @Override public void tick() {
        super.tick();
        if (!level.isClientSide) {
            ((ServerLevel) level).sendParticles(
                    ParticleTypes.END_ROD,
                    getX(), getY(), getZ(),
                    1, 0, 0, 0, 0);
        }
        if (inGround) discard();
    }

    @Override protected ItemStack getPickupItem() { return ItemStack.EMPTY; }

    @Override protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        discard();
    }

    @Override protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        discard();
    }
}
