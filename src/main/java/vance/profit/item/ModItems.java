package vance.profit.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import vance.profit.Guaranteed_profit;

public class ModItems {
    public static final Item SUPER_DIAMOND = registerItem("super_diamond", new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of(Guaranteed_profit.MOD_ID, "super_diamond")))));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Guaranteed_profit.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Guaranteed_profit.LOGGER.info("Registering Mod Items for " + Guaranteed_profit.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(SUPER_DIAMOND);
        });
    }
}
