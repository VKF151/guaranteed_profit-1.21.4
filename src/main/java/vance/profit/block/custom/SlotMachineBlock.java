package vance.profit.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import vance.profit.block.custom.entity.SlotMachineBlockEntity;

public class SlotMachineBlock extends BlockWithEntity{
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty WIN = BooleanProperty.of("win");
    public SlotMachineBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WIN, false));
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WIN);
    }

    @Override
    public MapCodec<? extends SlotMachineBlock> getCodec() {return createCodec(SlotMachineBlock::new);}

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlotMachineBlockEntity(pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        state.get(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity)) {
            return ActionResult.PASS;
        }

        ItemStack handStack = player.getStackInHand(hand);

        ItemStack storedStack = blockEntity.getStack(0);
        if (!handStack.isEmpty() && handStack.getItem() == Items.DIAMOND) {

            if (storedStack.isEmpty()) {
                blockEntity.setStack(0, handStack.split(1));
            } else if (storedStack.getCount() < storedStack.getMaxCount()) {
                storedStack.increment(1);
                handStack.decrement(1);
            }

            blockEntity.markDirty();
            return ActionResult.SUCCESS;
        } else {

            if (!storedStack.isEmpty()) {
                player.getInventory().offerOrDrop(storedStack.copy());
                blockEntity.setStack(0, ItemStack.EMPTY);
                blockEntity.markDirty();
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;

    }

    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        ItemScatterer.onStateReplaced(state, world, pos);
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            if (world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity) {
                ItemStack storedStack = blockEntity.getStack(0);

                long currentTime = world.getTime();
                if (blockEntity.getLastActivatedTime() + 5 <= currentTime) {
                    blockEntity.setLastActivatedTime(currentTime);

                    if (!storedStack.isEmpty() && storedStack.getCount() > 0) {
                        storedStack.decrement(1);
                        blockEntity.playGame(null, pos, world);

                    }
                }


            }
        }
    }



}
