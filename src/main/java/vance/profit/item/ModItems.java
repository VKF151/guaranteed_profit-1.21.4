package vance.profit.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import vance.profit.Guaranteed_profit;
import vance.profit.block.ModBlocks;
import vance.profit.item.custom.CrazyMaceItem;
import vance.profit.item.custom.CrazyScytheItem;
import vance.profit.item.custom.CrazySlotsItem;

public class ModItems {
    public static final Item SUPER_DIAMOND = registerItem("super_diamond", new Item(
            new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of(Guaranteed_profit.MOD_ID, "super_diamond")))
    ));

    public static final Item CRAZY_SLOTS = registerItem("crazy_slots", new CrazySlotsItem(
            new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Guaranteed_profit.MOD_ID, "crazy_slots")))
                    .maxCount(1)
                    .fireproof()
    ));

    public static final Item CRAZY_SCYTHE = registerItem("crazy_scythe", new CrazyScytheItem(
            new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Guaranteed_profit.MOD_ID, "crazy_scythe")))
                    .maxCount(1)
                    .fireproof()
                    .attributeModifiers(CrazyScytheItem.createAttributeModifiers())
    ));

    public static final Item CRAZY_MACE = registerItem("crazy_mace", new CrazyMaceItem(
            new Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Guaranteed_profit.MOD_ID, "crazy_mace")))
                    .maxCount(1)
                    .fireproof()
                    .attributeModifiers(CrazyMaceItem.createAttributeModifiers())
    ));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Guaranteed_profit.MOD_ID, name), item);
    }

    public static final RegistryKey<ItemGroup> GUARANTEED_PROFIT_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Guaranteed_profit.MOD_ID, "guaranteed_profit_group"));
    public static final ItemGroup GUARANTEED_PROFIT_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModItems.SUPER_DIAMOND))
            .displayName(Text.translatable("itemGroup.guaranteed_profit"))
            .build();

    public static void registerModItems() {
        Guaranteed_profit.LOGGER.info("Registering Mod Items for " + Guaranteed_profit.MOD_ID);

        Registry.register(Registries.ITEM_GROUP, GUARANTEED_PROFIT_GROUP_KEY, GUARANTEED_PROFIT_GROUP);

        ItemGroupEvents.modifyEntriesEvent(GUARANTEED_PROFIT_GROUP_KEY).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.add(ModBlocks.SLOT_MACHINE.asItem());
            fabricItemGroupEntries.add(ModItems.CRAZY_SLOTS);
        });
    }
}
