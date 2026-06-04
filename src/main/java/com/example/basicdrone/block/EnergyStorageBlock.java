package com.example.basicdrone.block;

import com.example.basicdrone.block.entity.EnergyStorageBlockEntity;
import com.example.basicdrone.menu.EnergyStorageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.RenderShape;

public class EnergyStorageBlock extends BaseEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public EnergyStorageBlock() {
        super(BlockBehaviour.Properties
                .of(net.minecraft.world.level.material.Material.METAL)
                .strength(2.5f)
        );
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyStorageBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lvl, BlockState state, BlockEntityType<T> type) {
        return lvl.isClientSide
                ? null
                : (lvl2, pos, st, be) -> {
            if (be instanceof EnergyStorageBlockEntity tile)
                EnergyStorageBlockEntity.tick(lvl2, pos, st, tile);
        };
    }


    @Override
    public InteractionResult use(BlockState state,
                                 Level level,
                                 BlockPos pos,
                                 Player player,
                                 InteractionHand hand,
                                 BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof EnergyStorageBlockEntity storageTile) {
                MenuConstructor constructor = (windowId, inv, p) ->
                        new EnergyStorageMenu(windowId, inv, storageTile);
                MenuProvider provider = new SimpleMenuProvider(
                        constructor,
                        new TranslatableComponent("container.basicdrone.energy_storage")
                );
                NetworkHooks.openGui(serverPlayer, provider, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
