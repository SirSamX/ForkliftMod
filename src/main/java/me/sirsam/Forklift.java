package me.sirsam;

import me.sirsam.block.ModBlocks;
import me.sirsam.entity.ForkliftEntity;
import me.sirsam.entity.ModEntities;
import me.sirsam.item.ModItemGroup;
import me.sirsam.item.ModItems;
import me.sirsam.networking.LiftC2SPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

		PayloadTypeRegistry.playC2S().register(LiftC2SPayload.ID, LiftC2SPayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(LiftC2SPayload.ID, (payload, context) -> context.server().execute(() -> {
            if (context.player().getVehicle() instanceof ForkliftEntity forklift) {
				if (payload.action() == LiftC2SPayload.LiftAction.UP)
					forklift.setLiftHeight(forklift.getLiftHeight() + 1);
				else
					forklift.setLiftHeight(forklift.getLiftHeight() - 1);
			}
        }));
	}
}