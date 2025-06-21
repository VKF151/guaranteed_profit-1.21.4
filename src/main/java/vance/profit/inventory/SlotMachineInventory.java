package vance.profit.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface SlotMachineInventory extends Inventory {

    DefaultedList<ItemStack> getItems();

    static SlotMachineInventory of(DefaultedList<ItemStack> items) {
        return new SlotMachineInventory() {
            @Override
            public ItemStack removeStack(int slot) {
                return null;
            }

            private final DefaultedList<ItemStack> inventoryItems = items;

            @Override
            public DefaultedList<ItemStack> getItems() {
                return inventoryItems;
            }
        };
    }

    static SlotMachineInventory ofSize(int size) {
        return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
    }

    @Override
    default int size() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > stack.getMaxCount()) {
            stack.setCount(stack.getMaxCount());
        }
        markDirty();
    }

    @Override
    default void clear() {
        getItems().clear();
        markDirty();
    }

    @Override
    default void markDirty() {

    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

}
