package com.example.basicdrone.block;

import com.example.basicdrone.block.entity.DroneBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class DroneBlock extends BaseEntityBlock {

    public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;

    public DroneBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .strength(0.5f)
                .noOcclusion());
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Nullable
    @Override
    public DroneBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DroneBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level lvl, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!lvl.isClientSide) {
            BlockEntity be = lvl.getBlockEntity(pos);
            if (be instanceof DroneBlockEntity droneBe) {
                int battery = droneBe.getBatteryTicks();
                if (stack.hasTag() && stack.getTag().contains("Battery")) {
                    battery = stack.getTag().getInt("Battery");
                }
                droneBe.setBatteryTicks(battery);
            }
        }
        super.setPlacedBy(lvl, pos, state, placer, stack);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(this);

            if (level.getBlockEntity(pos) instanceof DroneBlockEntity be) {
                stack.getOrCreateTag().putInt("Battery", be.getBatteryTicks());
            }

            if (!player.getInventory().add(stack))
                player.drop(stack, false);

            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 1f);
            level.removeBlock(pos, false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
