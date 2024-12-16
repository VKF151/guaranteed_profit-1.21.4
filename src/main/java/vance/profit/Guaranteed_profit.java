package vance.profit;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vance.profit.block.ModBlocks;
import vance.profit.item.ModItems;

public class Guaranteed_profit implements ModInitializer {
	public static final String MOD_ID = "guaranteed_profit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.initialize();
	}
}