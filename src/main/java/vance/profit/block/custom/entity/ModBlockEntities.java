package vance.profit.block.custom.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import vance.profit.Guaranteed_profit;
import vance.profit.block.ModBlocks;

public class ModBlockEntities {
    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Guaranteed_profit.MOD_ID, path), blockEntityType);
    }

    public static final BlockEntityType<SlotMachineBlockEntity> SLOT_MACHINE_ENTITY = register("slot_machine_entity",
            FabricBlockEntityTypeBuilder.create(SlotMachineBlockEntity::new, ModBlocks.SLOT_MACHINE).build(null));

    public static void initialize() {};

}
