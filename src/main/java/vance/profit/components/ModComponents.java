package vance.profit.components;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import vance.profit.Guaranteed_profit;

public class ModComponents {
    protected static void initialize() {
        Guaranteed_profit.LOGGER.info("Registering {} components", Guaranteed_profit.MOD_ID);
    }

    public static final ComponentType<Integer> TRANSFORMABLE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Guaranteed_profit.MOD_ID, "transformable"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<String> ORIGINAL_ITEM = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Guaranteed_profit.MOD_ID, "original_Item"),
            ComponentType.<String>builder().codec(Codec.STRING).build()
    );

}
