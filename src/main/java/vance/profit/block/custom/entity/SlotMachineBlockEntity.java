package vance.profit.block.custom.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import vance.profit.block.ModBlocks;
import vance.profit.block.custom.SlotMachineBlock;
import vance.profit.inventory.SlotMachineInventory;

import java.util.Set;
import java.util.stream.IntStream;

public class SlotMachineBlockEntity extends BlockEntity implements SlotMachineInventory, SidedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean WON = false;
    private long lastActivatedTime = -1;

    public SlotMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLOT_MACHINE_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public ItemStack removeStack(int slot) {
        items.set(slot, ItemStack.EMPTY);
        markDirty();
        return null;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        writeNBT(nbt, registryLookup); // Save inventory using SlotMachineInventory's method
        nbt.putLong("LastActivatedTime", this.lastActivatedTime);
        nbt.putBoolean("WON", this.WON);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        readNBT(nbt, registryLookup); // Load inventory using SlotMachineInventory's method
        this.lastActivatedTime = nbt.getLong("LastActivatedTime");
        this.WON = nbt.getBoolean("WON");
    }

    public void playGame(PlayerEntity player, BlockPos pos, World world) {


        if (!world.isClient) {
            boolean win = world.random.nextFloat() <0.2;

            BlockState currentState = world.getBlockState(pos);
            world.setBlockState(pos, currentState.with(SlotMachineBlock.WIN, win), Block.NOTIFY_ALL);
            world.playSound(
                    null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1.0f, 1.0f
            );
            if (win) {
                if (world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity) {
                    setWON(true);
                    int diamondsWon = 1 + world.random.nextInt(3);
                    ItemStack reward = new ItemStack(Items.DIAMOND, diamondsWon);
                    Block.dropStack(world, pos, reward);
                    world.playSound(
                            null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1.0f, 1.0f
                    );
                }
            } else if (world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity) {
                setWON(false);
                world.playSound(
                        null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.BLOCKS, 1.0f, 1.0f
                );
            }
        }
    }

    public  boolean getWon() {
        return WON;
    }
    public void setWON(boolean WON) {
        this.WON = WON;
    }

    public long getLastActivatedTime() {
        return lastActivatedTime;
    }
    public void setLastActivatedTime(long lastActivatedTime) {
        this.lastActivatedTime = lastActivatedTime;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction direction) {
        return direction != Direction.DOWN;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return true;
    }


}
