package me.sirsam;

import me.sirsam.entity.ModEntities;
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
		ModEntities.init();
	}
}