package vance.profit.block.custom.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.brain.task.TargetUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vance.profit.Guaranteed_profit;
import vance.profit.inventory.SlotMachineInventory;

import java.util.List;

import static net.minecraft.block.Block.*;
import static vance.profit.block.custom.SlotMachineBlock.WIN;

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
        writeNBT(nbt, registryLookup);
        nbt.putLong("LastActivatedTime", this.lastActivatedTime);
        nbt.putBoolean("WON", this.WON);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        readNBT(nbt, registryLookup);
        this.lastActivatedTime = nbt.getLong("LastActivatedTime", 1);
        this.WON = nbt.getBoolean("WON", false);
    }

    public void playGame(PlayerEntity player, BlockPos pos, World world) {


        if (!world.isClient) {
            boolean win = world.random.nextFloat() <0.25;

            BlockState currentState = world.getBlockState(pos);
            world.setBlockState(pos, currentState.with(WIN, win), Block.NOTIFY_ALL);
            world.playSound(
                    null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_BASS, SoundCategory.BLOCKS, 1.0f, 1.0f
            );
            if (win) {
                setWON(true);
                world.playSound(
                        null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1.0f, 1.0f
                );
                for (ItemStack itemStack : getWonItem(SlotMachineBlockEntity.this)) {
                    dropStack(world, pos.up(), itemStack);
                }



            } else {
                setWON(false);
                world.setBlockState(pos, currentState.with(WIN, false));
                world.playSound(
                        null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.BLOCKS, 1.0f, 1.0f
                );
            } markDirty();
        }
    }

    private static List<ItemStack> getWonItem(BlockEntity entity) {
        LootTable lootTable = entity.getWorld().getServer().getReloadableRegistries().getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE,Identifier.of(Guaranteed_profit.MOD_ID, "rewards/slot_machine")));
        return lootTable.generateLoot(
                new LootWorldContext.Builder((ServerWorld) entity.getWorld())
                        .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(entity.getPos()))
                        .build(LootContextTypes.CHEST)
        );
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
