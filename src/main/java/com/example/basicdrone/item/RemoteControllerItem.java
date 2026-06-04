package com.example.basicdrone.item;

import com.example.basicdrone.block.entity.DroneBlockEntity;
import com.example.basicdrone.entity.DroneEntity;
import com.example.basicdrone.menu.RemoteControllerMenu;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.UUID;

public class RemoteControllerItem extends Item {
    public static final String TAG_LINKED_DRONE_POS = "LinkedDronePos";
    public static final String TAG_LINKED_DRONE_UUID = "LinkedDroneUUID";

    public RemoteControllerItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!ctx.getLevel().isClientSide && ctx.getPlayer().isShiftKeyDown()) {
            BlockEntity be = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
            ItemStack stack = ctx.getItemInHand();
            if (be instanceof DroneBlockEntity) {
                stack.getOrCreateTag().putLong(TAG_LINKED_DRONE_POS, ctx.getClickedPos().asLong());
                ctx.getPlayer().sendMessage(
                        new TextComponent("Drone linked!"),
                        Util.NIL_UUID
                );
                stack.getOrCreateTag().remove(TAG_LINKED_DRONE_UUID);
                return InteractionResult.SUCCESS;

            }
        }
        return super.useOn(ctx);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        if (!player.level.isClientSide && target instanceof DroneEntity drone) {
            stack.getOrCreateTag().putUUID(TAG_LINKED_DRONE_UUID, drone.getUUID());
            player.sendMessage(new TextComponent("Remote linked to drone entity!"), Util.NIL_UUID);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);
        if (!(player instanceof ServerPlayer srv)) return InteractionResultHolder.pass(stack);

        UUID droneUUID = null;
        BlockPos boundPos = null;
        if (stack.getOrCreateTag().contains(TAG_LINKED_DRONE_UUID)) {
            droneUUID = stack.getOrCreateTag().getUUID(TAG_LINKED_DRONE_UUID);
        }
        if (stack.getOrCreateTag().contains(TAG_LINKED_DRONE_POS)) {
            boundPos = BlockPos.of(stack.getOrCreateTag().getLong(TAG_LINKED_DRONE_POS));
        }

        if (droneUUID != null) {
            DroneEntity drone = findDroneNearby(level, droneUUID, player.getX(), player.getY(), player.getZ(), 100);
            if (drone != null) {
                openGui(srv, drone.getBatteryTicks(), drone.isFlying());
                return InteractionResultHolder.success(stack);
            }
        }

        if (boundPos != null) {
            final BlockPos finalBoundPos = boundPos;
            BlockEntity be = level.getBlockEntity(boundPos);
            if (be instanceof DroneBlockEntity dbb) {
                openGui(srv, dbb.getBatteryTicks(), false);
                return InteractionResultHolder.success(stack);
            }
            List<DroneEntity> list = level.getEntitiesOfClass(
                    DroneEntity.class, new AABB(boundPos).inflate(100),
                    e -> finalBoundPos.equals(e.getOriginPos())
            );
            if (!list.isEmpty()) {
                DroneEntity drone = list.get(0);
                stack.getOrCreateTag().putUUID(TAG_LINKED_DRONE_UUID, drone.getUUID());
                openGui(srv, drone.getBatteryTicks(), drone.isFlying());
                return InteractionResultHolder.success(stack);
            }
        }

        srv.sendMessage(new TextComponent("No drone is linked!"), Util.NIL_UUID);
        return InteractionResultHolder.fail(stack);
    }


    private DroneEntity findDroneNearby(Level level, UUID uuid, double x, double y, double z, double radius) {
        List<DroneEntity> drones = level.getEntitiesOfClass(DroneEntity.class,
                new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius),
                e -> e.getUUID().equals(uuid) && e.isAlive());
        return drones.isEmpty() ? null : drones.get(0);
    }

    private void openGui(ServerPlayer srv, int battery, boolean flying) {
        if (srv == null) {
            System.out.println("Attempted to open RemoteControllerMenu for null player!");
            return;
        }
        MenuConstructor ctor = (win, inv, p) -> {
            if (p == null) {
                System.out.println("Attempted to open RemoteControllerMenu with null player inventory!");
                return null;
            }
            return new RemoteControllerMenu(win, inv, battery, flying);
        };
        Component title = new TranslatableComponent("screen.basicdrone.remote_controller");
        MenuProvider prov = new SimpleMenuProvider(ctor, title);

        NetworkHooks.openGui(
                srv,
                prov,
                (FriendlyByteBuf buf) -> {
                    buf.writeInt(battery);
                    buf.writeBoolean(flying);
                }
        );
    }

}
