package vance.profit.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import vance.profit.Guaranteed_profit;
import vance.profit.block.custom.SlotMachineBlock;

public class ModBlocks {
    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(Guaranteed_profit.MOD_ID,  name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM,Identifier.of(Guaranteed_profit.MOD_ID, name))));
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static final Block SLOT_MACHINE = register(
            new SlotMachineBlock(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK,Identifier.of(Guaranteed_profit.MOD_ID, "slot_machine"))).sounds(BlockSoundGroup.HEAVY_CORE).requiresTool().strength(2.0F, 6.0F)),
            "slot_machine",
            true
    );

    public  static void initialize() {}
}
