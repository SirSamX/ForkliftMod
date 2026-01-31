package me.sirsam;

import com.mojang.blaze3d.platform.InputConstants;
import me.sirsam.entity.ForkliftEntity;
import me.sirsam.entity.ModEntities;
import me.sirsam.networking.LiftC2SPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.loading.math.MolangQueries;

public class ForkliftClient implements ClientModInitializer {
	KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(
			Identifier.fromNamespaceAndPath(Forklift.MOD_ID, "custom_category")
	);

	KeyMapping forkDownKey = KeyBindingHelper.registerKeyBinding(
			new KeyMapping(
					"key.forklift.fork_down",
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_J,
					KEY_CATEGORY
			));

	KeyMapping forkUpKey = KeyBindingHelper.registerKeyBinding(
			new KeyMapping(
					"key.forklift.fork_up",
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_U,
					KEY_CATEGORY
			));

	@Override
	public void onInitializeClient() {
		EntityRenderers.register(ModEntities.FORKLIFT, ForkliftRenderer::new);

		MolangQueries.<ForkliftEntity>setActorVariable("query.forklift_forklift_steering_angle", actor -> {
			ForkliftEntity forklift = actor.animatable();
			float partialTick = actor.partialTick();

			return Mth.lerp(partialTick, forklift.prevSteeringAngle, forklift.steeringAngle);
		});

		MolangQueries.<ForkliftEntity>setActorVariable("query.forklift_forklift_drive_speed", actor -> (-2000 * actor.animatable().getEntityData().get(ForkliftEntity.DRIVE_SPEED)));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null && client.player.getVehicle() instanceof ForkliftEntity) {
				if (forkUpKey.isDown()) {
					ClientPlayNetworking.send(new LiftC2SPayload(LiftC2SPayload.LiftAction.UP));
				} else if (forkDownKey.isDown()) {
					ClientPlayNetworking.send(new LiftC2SPayload(LiftC2SPayload.LiftAction.DOWN));
				}
			}
		});
	}
}