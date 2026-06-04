package com.example.basicdrone.block;

import com.example.basicdrone.block.entity.CableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;




import java.util.*;

public class CableBlock extends BaseEntityBlock {
    public enum CableShape implements StringRepresentable {
        STRAIGHT("straight"),
        CORNER("corner"),
        T_SHAPE("t_shape"),
        TRIPLE("triple"),
        CROSS("cross"),
        QUAD("quad"),
        QUINTUPLE("quintuple"),
        FULL("full");

        private final String name;
        CableShape(String name) { this.name = name; }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public enum VariantKey implements StringRepresentable {
        STRAIGHT_NORTH("straight_north"),
        STRAIGHT_EAST("straight_east"),
        STRAIGHT_UP("straight_up"),

        CORNER_UP_WEST("corner_up_west"),
        CORNER_UP_EAST("corner_up_east"),
        CORNER_UP_NORTH("corner_up_north"),
        CORNER_UP_SOUTH("corner_up_south"),
        CORNER_DOWN_WEST("corner_down_west"),
        CORNER_DOWN_EAST("corner_down_east"),
        CORNER_DOWN_NORTH("corner_down_north"),
        CORNER_DOWN_SOUTH("corner_down_south"),
        CORNER_NORTH_EAST("corner_north_east"),
        CORNER_NORTH_WEST("corner_north_west"),
        CORNER_SOUTH_EAST("corner_south_east"),
        CORNER_SOUTH_WEST("corner_south_west"),

        T_FORWARD_LEFT_RIGHT("t_forward_left_right"),
        T_FORWARD_RIGHT_BACK("t_forward_right_back"),
        T_BACK_RIGHT_LEFT("t_back_right_left"),
        T_LEFT_FORWARD_BACK("t_left_forward_back"),

        T_UP_FORWARD_BACK("t_up_forward_back"),
        T_UP_LEFT_RIGHT("t_up_left_right"),
        T_DOWN_LEFT_RIGHT("t_down_left_right"),
        T_DOWN_FORWARD_BACK("t_down_forward_back"),


        T_UP_DOWN_FORWARD("t_up_down_forward"),
        T_UP_DOWN_BACK("t_up_down_back"),
        T_UP_DOWN_LEFT("t_up_down_left"),
        T_UP_DOWN_RIGHT("t_up_down_right"),


        TRIPLE_UP_NORTH_WEST("triple_up_north_west"),
        TRIPLE_UP_NORTH_EAST("triple_up_north_east"),
        TRIPLE_UP_SOUTH_WEST("triple_up_south_west"),
        TRIPLE_UP_SOUTH_EAST("triple_up_south_east"),
        TRIPLE_DOWN_NORTH_WEST("triple_down_north_west"),
        TRIPLE_DOWN_NORTH_EAST("triple_down_north_east"),
        TRIPLE_DOWN_SOUTH_WEST("triple_down_south_west"),
        TRIPLE_DOWN_SOUTH_EAST("triple_down_south_east"),

        CROSS_HORIZONTAL("cross_horizontal"),
        CROSS_VERTICAL_X("cross_vertical_x"),
        CROSS_VERTICAL_Z("cross_vertical_z"),

        QUAD_UP_FORWARD_LEFT_RIGHT("quad_up_forward_left_right"),
        QUAD_UP_BACK_LEFT_RIGHT("quad_up_back_left_right"),
        QUAD_UP_LEFT_FORWARD_BACK("quad_up_left_forward_back"),
        QUAD_UP_RIGHT_FORWARD_BACK("quad_up_right_forward_back"),
        QUAD_DOWN_FORWARD_LEFT_RIGHT("quad_down_forward_left_right"),
        QUAD_DOWN_BACK_LEFT_RIGHT("quad_down_back_left_right"),
        QUAD_DOWN_LEFT_FORWARD_BACK("quad_down_left_forward_back"),
        QUAD_DOWN_RIGHT_FORWARD_BACK("quad_down_right_forward_back"),
        QUAD_UP_DOWN_FORWARD_LEFT("quad_up_down_forward_left"),
        QUAD_UP_DOWN_FORWARD_RIGHT("quad_up_down_forward_right"),
        QUAD_UP_DOWN_BACK_LEFT("quad_up_down_back_left"),
        QUAD_UP_DOWN_BACK_RIGHT("quad_up_down_back_right"),

        QUINT_FORWARD_BACK_LEFT_RIGHT_UP("quint_forward_back_left_right_up"),
        QUINT_FORWARD_BACK_LEFT_RIGHT_DOWN("quint_forward_back_left_right_down"),
        QUINT_FORWARD_LEFT_RIGHT_UP_DOWN("quint_forward_left_right_up_down"),
        QUINT_BACK_LEFT_RIGHT_UP_DOWN("quint_back_left_right_up_down"),
        QUINT_LEFT_FORWARD_BACK_UP_DOWN("quint_left_forward_back_up_down"),
        QUINT_RIGHT_FORWARD_BACK_UP_DOWN("quint_right_forward_back_up_down"),


        FULL_ALL("full_all");

        private final String name;
        VariantKey(String name) { this.name = name; }
        @Override
        public String getSerializedName() { return this.name; }
    }


    public static final EnumProperty<CableShape> SHAPE =
            EnumProperty.create("shape", CableShape.class);
    public static final EnumProperty<VariantKey> VARIANT =
            EnumProperty.create("facing", VariantKey.class);

    public CableBlock() {
        super(Properties.of(Material.METAL)
                .strength(0.5f)
                .noOcclusion()
        );

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(SHAPE, CableShape.STRAIGHT)
                        .setValue(VARIANT, VariantKey.STRAIGHT_NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, VARIANT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return getUpdatedState(ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockState newState = getUpdatedState(level, pos);
            if (newState != state) {
                level.setBlock(pos, newState, 3);
            }

            for (Direction dir : Direction.values()) {
                BlockPos npos = pos.relative(dir);
                BlockState neighbor = level.getBlockState(npos);
                if (neighbor.getBlock() instanceof CableBlock) {
                    BlockState updated = ((CableBlock) neighbor.getBlock()).getUpdatedState(level, npos);
                    if (updated != neighbor) {
                        level.setBlock(npos, updated, 3);
                    }
                }
            }
        }
    }

    private static List<Direction> getConnections(Level level, BlockPos pos) {
        List<Direction> result = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor instanceof CableBlock
                    || neighbor instanceof GeneratorBlock
                    || neighbor instanceof ChargingStationBlock
                    || neighbor instanceof EnergyStorageBlock) {
                result.add(dir);
            }
        }
        return result;
    }

    public BlockState getUpdatedState(Level level, BlockPos pos) {
        List<Direction> list = getConnections(level, pos);
        Set<Direction> conns = EnumSet.noneOf(Direction.class);
        conns.addAll(list);
        int count = conns.size();

        CableShape shape = CableShape.STRAIGHT;
        VariantKey variant = VariantKey.STRAIGHT_NORTH;

        if (count == 0) {
            shape = CableShape.STRAIGHT;
            variant = VariantKey.STRAIGHT_NORTH;
        }

        else if (count == 1) {
            Direction d = list.get(0);
            if (d == Direction.NORTH || d == Direction.SOUTH) {
                shape = CableShape.STRAIGHT;
                variant = VariantKey.STRAIGHT_NORTH;
            } else if (d == Direction.EAST || d == Direction.WEST) {
                shape = CableShape.STRAIGHT;
                variant = VariantKey.STRAIGHT_EAST;
            } else { // UP или DOWN
                shape = CableShape.STRAIGHT;
                variant = VariantKey.STRAIGHT_UP;
            }
        }

        else if (count == 2) {
            if (conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)) {
                shape = CableShape.STRAIGHT;
                variant = VariantKey.STRAIGHT_NORTH;
            }
            else if (conns.contains(Direction.EAST) && conns.contains(Direction.WEST)) {
                shape = CableShape.STRAIGHT;
                variant = VariantKey.STRAIGHT_EAST;
            }
            else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)) {
                shape = CableShape.STRAIGHT;
                variant = VariantKey.STRAIGHT_UP;
            }
            else {
                shape = CableShape.CORNER;
                if (conns.contains(Direction.UP) && conns.contains(Direction.WEST)) {
                    variant = VariantKey.CORNER_UP_WEST;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.CORNER_UP_EAST;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.NORTH)) {
                    variant = VariantKey.CORNER_UP_NORTH;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.SOUTH)) {
                    variant = VariantKey.CORNER_UP_SOUTH;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.WEST)) {
                    variant = VariantKey.CORNER_DOWN_WEST;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.CORNER_DOWN_EAST;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.NORTH)) {
                    variant = VariantKey.CORNER_DOWN_NORTH;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.SOUTH)) {
                    variant = VariantKey.CORNER_DOWN_SOUTH;
                }
                else if (conns.contains(Direction.NORTH) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.CORNER_NORTH_EAST;
                }
                else if (conns.contains(Direction.NORTH) && conns.contains(Direction.WEST)) {
                    variant = VariantKey.CORNER_NORTH_WEST;
                }
                else if (conns.contains(Direction.SOUTH) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.CORNER_SOUTH_EAST;
                }
                else if (conns.contains(Direction.SOUTH) && conns.contains(Direction.WEST)) {
                    variant = VariantKey.CORNER_SOUTH_WEST;
                }
            }
        }
        else if (count == 3) {
            if (conns.contains(Direction.UP) && conns.contains(Direction.NORTH) && conns.contains(Direction.WEST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_UP_NORTH_WEST;
            }
            else if (conns.contains(Direction.UP) && conns.contains(Direction.NORTH) && conns.contains(Direction.EAST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_UP_NORTH_EAST;
            }
            else if (conns.contains(Direction.UP) && conns.contains(Direction.SOUTH) && conns.contains(Direction.WEST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_UP_SOUTH_WEST;
            }
            else if (conns.contains(Direction.UP) && conns.contains(Direction.SOUTH) && conns.contains(Direction.EAST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_UP_SOUTH_EAST;
            }
            else if (conns.contains(Direction.DOWN) && conns.contains(Direction.NORTH) && conns.contains(Direction.WEST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_DOWN_NORTH_WEST;
            }
            else if (conns.contains(Direction.DOWN) && conns.contains(Direction.NORTH) && conns.contains(Direction.EAST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_DOWN_NORTH_EAST;
            }
            else if (conns.contains(Direction.DOWN) && conns.contains(Direction.SOUTH) && conns.contains(Direction.WEST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_DOWN_SOUTH_WEST;
            }
            else if (conns.contains(Direction.DOWN) && conns.contains(Direction.SOUTH) && conns.contains(Direction.EAST)) {
                shape = CableShape.TRIPLE;
                variant = VariantKey.TRIPLE_DOWN_SOUTH_EAST;
            }
            else {
                shape = CableShape.T_SHAPE;

                if (conns.contains(Direction.UP) && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)) {
                    variant = VariantKey.T_UP_FORWARD_BACK;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.T_UP_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.T_DOWN_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)) {
                    variant = VariantKey.T_DOWN_FORWARD_BACK;
                }


                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN) && conns.contains(Direction.NORTH)) {
                    variant = VariantKey.T_UP_DOWN_FORWARD;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN) && conns.contains(Direction.SOUTH)) {
                    variant = VariantKey.T_UP_DOWN_BACK;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN) && conns.contains(Direction.WEST)) {
                    variant = VariantKey.T_UP_DOWN_LEFT;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN) && conns.contains(Direction.EAST)) {
                    variant = VariantKey.T_UP_DOWN_RIGHT;
                }


                else if (conns.contains(Direction.NORTH) && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.SOUTH)) {
                    variant = VariantKey.T_FORWARD_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.NORTH) && conns.contains(Direction.EAST) && conns.contains(Direction.SOUTH)
                        && !conns.contains(Direction.WEST)) {
                    variant = VariantKey.T_FORWARD_RIGHT_BACK;
                }
                else if (conns.contains(Direction.SOUTH) && conns.contains(Direction.EAST) && conns.contains(Direction.WEST)
                        && !conns.contains(Direction.NORTH)) {
                    variant = VariantKey.T_BACK_RIGHT_LEFT;
                }
                else if (conns.contains(Direction.WEST) && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                        && !conns.contains(Direction.EAST)) {
                    variant = VariantKey.T_LEFT_FORWARD_BACK;
                }
                else {
                    variant = VariantKey.T_FORWARD_LEFT_RIGHT;
                }
            }
        }

        else if (count == 4) {
            if (conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                    && conns.contains(Direction.EAST) && conns.contains(Direction.WEST)
                    && !conns.contains(Direction.UP) && !conns.contains(Direction.DOWN)) {
                shape = CableShape.CROSS;
                variant = VariantKey.CROSS_HORIZONTAL;
            }
            else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)
                    && conns.contains(Direction.EAST) && conns.contains(Direction.WEST)
                    && !conns.contains(Direction.NORTH) && !conns.contains(Direction.SOUTH)) {
                shape = CableShape.CROSS;
                variant = VariantKey.CROSS_VERTICAL_X;
            }
            else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)
                    && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                    && !conns.contains(Direction.EAST) && !conns.contains(Direction.WEST)) {
                shape = CableShape.CROSS;
                variant = VariantKey.CROSS_VERTICAL_Z;
            }
            else {
                shape = CableShape.QUAD;
                if (conns.contains(Direction.UP) && conns.contains(Direction.NORTH)
                        && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.SOUTH) && !conns.contains(Direction.DOWN)) {
                    variant = VariantKey.QUAD_UP_FORWARD_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.SOUTH)
                        && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.NORTH) && !conns.contains(Direction.DOWN)) {
                    variant = VariantKey.QUAD_UP_BACK_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.WEST)
                        && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                        && !conns.contains(Direction.EAST) && !conns.contains(Direction.DOWN)) {
                    variant = VariantKey.QUAD_UP_LEFT_FORWARD_BACK;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.EAST)
                        && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                        && !conns.contains(Direction.WEST) && !conns.contains(Direction.DOWN)) {
                    variant = VariantKey.QUAD_UP_RIGHT_FORWARD_BACK;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.NORTH)
                        && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.SOUTH) && !conns.contains(Direction.UP)) {
                    variant = VariantKey.QUAD_DOWN_FORWARD_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.SOUTH)
                        && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.NORTH) && !conns.contains(Direction.UP)) {
                    variant = VariantKey.QUAD_DOWN_BACK_LEFT_RIGHT;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.WEST)
                        && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                        && !conns.contains(Direction.EAST) && !conns.contains(Direction.UP)) {
                    variant = VariantKey.QUAD_DOWN_LEFT_FORWARD_BACK;
                }
                else if (conns.contains(Direction.DOWN) && conns.contains(Direction.EAST)
                        && conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                        && !conns.contains(Direction.WEST) && !conns.contains(Direction.UP)) {
                    variant = VariantKey.QUAD_DOWN_RIGHT_FORWARD_BACK;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)
                        && conns.contains(Direction.NORTH) && conns.contains(Direction.WEST)
                        && !conns.contains(Direction.SOUTH) && !conns.contains(Direction.EAST)) {
                    variant = VariantKey.QUAD_UP_DOWN_FORWARD_LEFT;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)
                        && conns.contains(Direction.NORTH) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.SOUTH) && !conns.contains(Direction.WEST)) {
                    variant = VariantKey.QUAD_UP_DOWN_FORWARD_RIGHT;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)
                        && conns.contains(Direction.SOUTH) && conns.contains(Direction.WEST)
                        && !conns.contains(Direction.NORTH) && !conns.contains(Direction.EAST)) {
                    variant = VariantKey.QUAD_UP_DOWN_BACK_LEFT;
                }
                else if (conns.contains(Direction.UP) && conns.contains(Direction.DOWN)
                        && conns.contains(Direction.SOUTH) && conns.contains(Direction.EAST)
                        && !conns.contains(Direction.NORTH) && !conns.contains(Direction.WEST)) {
                    variant = VariantKey.QUAD_UP_DOWN_BACK_RIGHT;
                }
                else {
                    variant = VariantKey.QUAD_UP_FORWARD_LEFT_RIGHT;
                }
            }
        }
        else if (count == 5) {
            shape = CableShape.QUINTUPLE;
            if (conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                    && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                    && conns.contains(Direction.UP) && !conns.contains(Direction.DOWN)) {
                variant = VariantKey.QUINT_FORWARD_BACK_LEFT_RIGHT_UP;
            }
            else if (conns.contains(Direction.NORTH) && conns.contains(Direction.SOUTH)
                    && conns.contains(Direction.WEST) && conns.contains(Direction.EAST)
                    && conns.contains(Direction.DOWN) && !conns.contains(Direction.UP)) {
                variant = VariantKey.QUINT_FORWARD_BACK_LEFT_RIGHT_DOWN;
            }
            else if (conns.contains(Direction.NORTH) && conns.contains(Direction.WEST)
                    && conns.contains(Direction.EAST) && conns.contains(Direction.UP)
                    && conns.contains(Direction.DOWN) && !conns.contains(Direction.SOUTH)) {
                variant = VariantKey.QUINT_FORWARD_LEFT_RIGHT_UP_DOWN;
            }
            else if (conns.contains(Direction.SOUTH) && conns.contains(Direction.WEST)
                    && conns.contains(Direction.EAST) && conns.contains(Direction.UP)
                    && conns.contains(Direction.DOWN) && !conns.contains(Direction.NORTH)) {
                variant = VariantKey.QUINT_BACK_LEFT_RIGHT_UP_DOWN;
            }
            else if (conns.contains(Direction.WEST) && conns.contains(Direction.NORTH)
                    && conns.contains(Direction.SOUTH) && conns.contains(Direction.UP)
                    && conns.contains(Direction.DOWN) && !conns.contains(Direction.EAST)) {
                variant = VariantKey.QUINT_LEFT_FORWARD_BACK_UP_DOWN;
            }
            else if (conns.contains(Direction.EAST) && conns.contains(Direction.NORTH)
                    && conns.contains(Direction.SOUTH) && conns.contains(Direction.UP)
                    && conns.contains(Direction.DOWN) && !conns.contains(Direction.WEST)) {
                variant = VariantKey.QUINT_RIGHT_FORWARD_BACK_UP_DOWN;
            }
            else {
                variant = VariantKey.QUINT_FORWARD_BACK_LEFT_RIGHT_UP;
            }
        }
        else if (count == 6) {
            shape = CableShape.FULL;
            variant = VariantKey.FULL_ALL;
        }

        return this.defaultBlockState()
                .setValue(SHAPE, shape)
                .setValue(VARIANT, variant);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? null
                : (lvl, pos, st, be) -> {
            if (be instanceof CableBlockEntity tile) {
                CableBlockEntity.tick(lvl, pos, st, tile);
            }
        };
    }

}
