package me.sirsam;

import me.sirsam.block.ModBlocks;
import me.sirsam.entity.ModEntities;
import me.sirsam.item.ModItemGroup;
import me.sirsam.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Forklift implements ModInitializer {
	public static final String MOD_ID = "forklift";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        ModItems.init();
		ModItemGroup.init();
		ModBlocks.init();
		ModEntities.init();
	}
}