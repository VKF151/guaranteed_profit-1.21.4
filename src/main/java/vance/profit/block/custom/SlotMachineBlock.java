package vance.profit.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
        builder.add(FACING, WIN); // Register the "win" and "facing" property
    }

    @Override
    public MapCodec<? extends SlotMachineBlock> getCodec() {return createCodec(SlotMachineBlock::new);}

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlotMachineBlockEntity(pos, state);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {Direction direction = state.get(FACING);}

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

        if (!handStack.isEmpty() && handStack.getItem() == Items.DIAMOND) {
            // Deposit a diamond into the slot machine
            ItemStack storedStack = blockEntity.getStack(0);

            if (storedStack.isEmpty()) {
                blockEntity.setStack(0, handStack.split(1));
            } else if (storedStack.getCount() < storedStack.getMaxCount()) {
                storedStack.increment(1);
                handStack.decrement(1);
            }

            blockEntity.markDirty();
            return ActionResult.SUCCESS;
        } else {
            // Withdraw diamonds from the slot machine
            ItemStack storedStack = blockEntity.getStack(0);

            if (!storedStack.isEmpty()) {
                player.getInventory().offerOrDrop(storedStack.copy()); // Give the full stack to the player
                blockEntity.setStack(0, ItemStack.EMPTY); // Clear the slot
                blockEntity.markDirty();
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;

    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient && world.isReceivingRedstonePower(pos)) {
            if (world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity) {
                ItemStack storedStack = blockEntity.getStack(0);

                long currentTime = world.getTime();
                if (blockEntity.getLastActivatedTime() + 10 <= currentTime) {
                    blockEntity.setLastActivatedTime(currentTime);

                    if (!storedStack.isEmpty() && storedStack.getCount() > 0) {
                        storedStack.decrement(1);// Consume one diamond
                        blockEntity.playGame(null, pos, world);// Play the game without a player
                        if (blockEntity.getWon()) {
                            boolean won = state.get(WIN);
                            world.setBlockState(pos, state.with(WIN, true));
                        } else {
                            world.setBlockState(pos, state.with(WIN, false));
                        }


                    }
                }


            }
        }
    }



}
