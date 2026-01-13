package me.sirsam;

import me.sirsam.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class ForkliftClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRenderers.register(ModEntities.FORKLIFT, ForkliftRenderer::new);
	}
}