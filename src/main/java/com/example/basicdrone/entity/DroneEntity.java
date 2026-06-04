package com.example.basicdrone.entity;

import com.example.basicdrone.Registries;
import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.example.basicdrone.item.RemoteControllerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.TextComponent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DroneEntity extends PathfinderMob implements IAnimatable {
    private static final Map<UUID, DroneEntity> PLAYER_DRONES = new ConcurrentHashMap<>();
    public static final int MAX_BATTERY_TICKS = 20 * 60 * 20 ;
    private static final int WARNING_THRESHOLD = MAX_BATTERY_TICKS * 15 / 100;
    private int batteryTicks = MAX_BATTERY_TICKS;
    private boolean warned;

    private boolean flying;
    private boolean forcedLand;
    private boolean charging;
    private UUID ownerUUID;
    private BlockPos originPos;
    private int attackCooldown;

    private static final double TELEPORT_DIST = 64.0;

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    private int idleLookTimer = 0;
    private double idleLookX = 0;
    private double idleLookY = 0;
    private double idleLookZ = 0;
    private boolean isIdleLooking = false;

    private LivingEntity currentAttackTarget = null;

    public static DroneEntity getForPlayer(ServerPlayer p) {
        return PLAYER_DRONES.get(p.getUUID());
    }

    private static void registerForPlayer(ServerPlayer p, DroneEntity d) {
        PLAYER_DRONES.put(p.getUUID(), d);
    }

    private static void unregisterForPlayer(ServerPlayer p) {
        PLAYER_DRONES.remove(p.getUUID());
    }

    public DroneEntity(EntityType<? extends DroneEntity> type, Level world) {
        super(type, world);
        noPhysics = false;
        setNoGravity(true);
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "flight_ctrl", 0, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> e) {
        var c = e.getController();
        if (isOnGround() || isCharging()) {
            c.setAnimation(new AnimationBuilder().addAnimation("idle", EDefaultLoopTypes.LOOP));
        } else {
            c.setAnimation(new AnimationBuilder().addAnimation("spin", EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
    }

    public int getBatteryTicks() {
        return batteryTicks;
    }

    public void setBatteryTicks(int t) {
        batteryTicks = Mth.clamp(t, 0, MAX_BATTERY_TICKS);
    }


    public boolean isFlying() {
        return flying;
    }

    public void forceLand() {
        forcedLand = true;
    }

    public void startFlying(ServerPlayer owner) {
        ownerUUID = owner.getUUID();
        registerForPlayer(owner, this);
        flying = true;
        forcedLand = false;
        charging = false;
        warned = batteryTicks <= WARNING_THRESHOLD;
        attackCooldown = 0;
        noPhysics = false;
        setNoGravity(true);
    }

    public void toggleFlying(Player sender) {
        if (!(sender instanceof ServerPlayer sp)) return;
        if (isFlying()) forceLand();
        else startFlying(sp);
    }

    private ServerPlayer getOwner() {
        return ownerUUID == null
                ? null
                : level.getServer().getPlayerList().getPlayer(ownerUUID);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        if (forcedLand) {
            land();
            forcedLand = false;
            return;
        }

        if (flying && horizontalCollision) {
            for (int dy = 1; dy <= 4; dy++) {
                BlockPos head = blockPosition().above(dy);
                BlockPos feet = head.below();
                BlockPos front1 = head.relative(getMotionDirection());
                BlockPos front2 = feet.relative(getMotionDirection());
                if (level.isEmptyBlock(front1)
                        && level.isEmptyBlock(front2)
                        && level.isEmptyBlock(head)) {
                    getNavigation().moveTo(getX(), getY() + dy + .5, getZ(), 1.6);
                    break;
                }
            }
        }

        if (level.isClientSide) return;

        if (flying && batteryTicks > 0) {
            batteryTicks--;
            if (!warned && batteryTicks <= WARNING_THRESHOLD) {
                playSound(Registries.DRONE_LOW_BATTERY.get(), 1f, 1f);
                warned = true;
                System.out.println("LOW BATTERY WARNING, flying=" + flying + ", nav.isDone=" + getNavigation().isDone());
            }
            if (batteryTicks == 0) {
                land();
            }
        }

        if (!flying && batteryTicks == 0) {
            goalSelector.disableControlFlag(Goal.Flag.MOVE);
            goalSelector.disableControlFlag(Goal.Flag.LOOK);
        }


        ServerPlayer owner = getOwner();
        if (flying && owner != null) {
            double dist = this.distanceTo(owner);
            if (dist > TELEPORT_DIST) {
                this.teleportTo(owner.getX(), owner.getY() + 2, owner.getZ());
                this.getNavigation().stop();
            }
        }

        if (currentAttackTarget != null && currentAttackTarget.isAlive()) {
            this.getLookControl().setLookAt(
                    currentAttackTarget.getX(),
                    currentAttackTarget.getY() + currentAttackTarget.getBbHeight() * 0.5,
                    currentAttackTarget.getZ(),
                    30.0F, 30.0F
            );
        } else if (isIdleLooking) {
            this.getLookControl().setLookAt(idleLookX, idleLookY, idleLookZ, 30.0F, 30.0F);
        } else {
            Vec3 motion = this.getDeltaMovement();
            if (motion.lengthSqr() > 0.01) {
                double px = this.getX() + motion.x * 4.0;
                double py = this.getY() + motion.y * 4.0;
                double pz = this.getZ() + motion.z * 4.0;
                this.getLookControl().setLookAt(px, py, pz, 30.0F, 30.0F);
            }
        }

        if (!flying || currentAttackTarget != null) {
            isIdleLooking = false;
            idleLookTimer = 0;
        } else if (getDeltaMovement().lengthSqr() < 0.02) {
            if (--idleLookTimer <= 0) {
                if (random.nextDouble() < 0.6) {
                    idleLookX = getX() + (random.nextDouble() - 0.5) * 8;
                    idleLookY = getY() + (random.nextDouble() - 0.5) * 3;
                    idleLookZ = getZ() + (random.nextDouble() - 0.5) * 8;
                    isIdleLooking = true;
                    idleLookTimer = 25 + random.nextInt(60);
                } else {
                    isIdleLooking = false;
                    idleLookTimer = 30 + random.nextInt(50);
                }
            }
        } else {
            isIdleLooking = false;
            idleLookTimer = 0;
        }
        lookInMovementDirection();
    }

    public void setOriginPos(BlockPos pos) {
        this.originPos = pos;
    }

    public BlockPos getOriginPos() {
        return originPos;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean c) {
        charging = c;
    }

    public void land() {
        flying = false;
        setNoGravity(false);
        getNavigation().stop();
        if (!level.isClientSide) {
            int runAt = level.getServer().getTickCount() + 2;
            level.getServer().tell(new TickTask(runAt, this::materializeAsBlock));
        }
    }

    private void materializeAsBlock() {
        BlockPos pos = new BlockPos(getX(), getY(), getZ());
        while (level.isEmptyBlock(pos) && pos.getY() > level.getMinBuildHeight()) {
            pos = pos.below();
        }
        BlockPos placePos = pos.above();
        level.setBlockAndUpdate(placePos, Registries.DRONE_BLOCK.get().defaultBlockState());
        if (level.getBlockEntity(placePos) instanceof DroneBlockEntity be) {
            be.setBatteryTicks(Mth.clamp(getBatteryTicks(), 0, DroneBlockEntity.MAX_BATTERY_TICKS));
        }
        ServerPlayer owner = getOwner();
        if (owner != null) {
            for (ItemStack stack : owner.getInventory().items) {
                if (stack.getItem() instanceof RemoteControllerItem) {
                    stack.getOrCreateTag().putLong(RemoteControllerItem.TAG_LINKED_DRONE_POS, placePos.asLong());
                    stack.getOrCreateTag().remove(RemoteControllerItem.TAG_LINKED_DRONE_UUID);
                }
            }
            unregisterForPlayer(owner);
        }
        discard();
    }

    @Override
    protected PathNavigation createNavigation(Level lvl) {
        var nav = new FlyingPathNavigation(this, lvl);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(2, new AttackGoal());
        goalSelector.addGoal(3, new FollowOwnerGoal());
    }

    @Override
    public boolean hurt(DamageSource src, float f) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource src) {
        return src.getEntity() instanceof Mob;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof RemoteControllerItem) {
            stack.getOrCreateTag().putUUID(RemoteControllerItem.TAG_LINKED_DRONE_UUID, this.getUUID());
            player.sendMessage(
                    new TextComponent("Контроллер успешно привязан к этому дрону!"),
                    player.getUUID()
            );
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Battery", this.batteryTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.batteryTicks = tag.getInt("Battery");
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level.isClientSide
                && (reason == RemovalReason.KILLED || reason == RemovalReason.UNLOADED_TO_CHUNK)) {
            ItemStack stack = new ItemStack(Registries.DRONE_ITEM.get());
            stack.getOrCreateTag().putInt("Battery", this.batteryTicks);
            level.addFreshEntity(new ItemEntity(level, getX(), getY(), getZ(), stack));
        }
        super.remove(reason);
    }


    private class AttackGoal extends Goal {
        private LivingEntity currentTarget = null;

        @Override
        public boolean canUse() {
            currentTarget = findTarget();
            DroneEntity.this.currentAttackTarget = currentTarget;
            return flying && batteryTicks > 0 && currentTarget != null;
        }

        @Override
        public boolean canContinueToUse() {
            DroneEntity.this.currentAttackTarget = currentTarget;
            return flying && batteryTicks > 0 && currentTarget != null && currentTarget.isAlive();
        }

        @Override
        public void start() {
            DroneEntity.this.level.playSound(
                    null,
                    DroneEntity.this.blockPosition(),
                    Registries.DRONE_SPOT.get(),
                    SoundSource.PLAYERS,
                    0.7f, 1f
            );
        }


        @Override
        public void stop() {
            currentTarget = null;
            DroneEntity.this.currentAttackTarget = null;
        }

        @Override
        public void tick() {
            if (currentTarget == null || !currentTarget.isAlive() || DroneEntity.this.distanceTo(currentTarget) > 24.0D) {
                currentTarget = null;
                DroneEntity.this.getNavigation().stop();
                DroneEntity.this.setDeltaMovement(DroneEntity.this.getDeltaMovement().scale(0.6));
                this.stop();
                return;
            }

            double dx = currentTarget.getX() - DroneEntity.this.getX();
            double dy = (currentTarget.getY() + currentTarget.getBbHeight() * 0.5) - DroneEntity.this.getY();
            double dz = currentTarget.getZ() - DroneEntity.this.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            double speed = 1.5D;
            double alpha = 0.12;

            Vec3 curVel = DroneEntity.this.getDeltaMovement();
            Vec3 targetVel = dist > 2.2D
                    ? new Vec3(dx / dist * speed, dy / dist * speed, dz / dist * speed)
                    : curVel.scale(0.65);
            Vec3 newVel = curVel.scale(1.0 - alpha).add(targetVel.scale(alpha));
            DroneEntity.this.setDeltaMovement(newVel);

            if (attackCooldown-- <= 0) {
                Vec3 dir = new Vec3(
                        currentTarget.getX() - DroneEntity.this.getX(),
                        currentTarget.getY() - DroneEntity.this.getY(),
                        currentTarget.getZ() - DroneEntity.this.getZ()
                ).normalize();
                BulletEntity b = new BulletEntity(DroneEntity.this.level, DroneEntity.this);
                b.shoot(dir.x, dir.y, dir.z, 3.0F, 0.0F);
                DroneEntity.this.level.addFreshEntity(b);
                DroneEntity.this.level.playSound(
                        null,
                        DroneEntity.this.blockPosition(),
                        Registries.DRONE_SHOOT.get(),
                        SoundSource.PLAYERS,
                        0.3F,
                        1.0F
                );
                attackCooldown = 3;
            }
        }

        private LivingEntity findTarget() {
            return DroneEntity.this.level.getEntitiesOfClass(
                    LivingEntity.class,
                    DroneEntity.this.getBoundingBox().inflate(16.0D),
                    e -> e.getType().getCategory() == MobCategory.MONSTER
                            && !(e instanceof net.minecraft.world.entity.monster.EnderMan)
                            && DroneEntity.this.hasLineOfSight(e)
                            && !e.isInvisible()
                            && e.isAlive()
            ).stream().findFirst().orElse(null);
        }
    }

    private class FollowOwnerGoal extends Goal {
        private static final double FOLLOW_DIST_MIN = 4.0D;
        private static final double FOLLOW_DIST_MAX = 6.0D;
        private static final double TELEPORT_DIST = 48.0D;
        private static final double FLIGHT_SPEED = 1.5D;
        private static final double HEIGHT_ABOVE_GROUND = 3.0D;

        private double offsetAngle = 0;
        private int offsetTimer = 0;

        @Override
        public boolean canUse() {
            return DroneEntity.this.flying && DroneEntity.this.batteryTicks > 0 && getOwner() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            ServerPlayer owner = getOwner();
            if (owner == null) return;

            if (offsetTimer-- <= 0) {
                offsetAngle = DroneEntity.this.getRandom().nextDouble() * 2 * Math.PI;
                offsetTimer = 40 + DroneEntity.this.getRandom().nextInt(60);
            }

            double offsetRadius = FOLLOW_DIST_MIN + DroneEntity.this.getRandom().nextDouble() * (FOLLOW_DIST_MAX - FOLLOW_DIST_MIN);
            double targetX = owner.getX() + Math.cos(offsetAngle) * offsetRadius;
            double targetZ = owner.getZ() + Math.sin(offsetAngle) * offsetRadius;
            int groundY = owner.level.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(targetX), Mth.floor(targetZ));
            double targetY = groundY + HEIGHT_ABOVE_GROUND;

            double dx = targetX - DroneEntity.this.getX();
            double dy = targetY - DroneEntity.this.getY();
            double dz = targetZ - DroneEntity.this.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > TELEPORT_DIST) {
                DroneEntity.this.teleportTo(targetX, targetY, targetZ);
                DroneEntity.this.setDeltaMovement(0, 0, 0);
                return;
            }

            double speed = FLIGHT_SPEED;
            double alpha = 0.10;
            Vec3 curVel = DroneEntity.this.getDeltaMovement();
            Vec3 targetVel = dist > 1.5D
                    ? new Vec3(dx / dist * speed, dy / dist * speed, dz / dist * speed)
                    : curVel.scale(0.6);
            Vec3 newVel = curVel.scale(1.0 - alpha).add(targetVel.scale(alpha));
            DroneEntity.this.setDeltaMovement(newVel);
            lookInMovementDirection();
        }

        @Override
        public void stop() {
            DroneEntity.this.setDeltaMovement(0, 0, 0);
        }
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.60D)
                .add(Attributes.FLYING_SPEED, 0.60D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    private void lookInMovementDirection() {
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 0.02) {
            double yaw = (float) (Mth.atan2(-motion.x, motion.z) * (180F / Math.PI));
            this.setYRot((float) yaw);
            this.yRotO = this.getYRot();
        }
    }
}
