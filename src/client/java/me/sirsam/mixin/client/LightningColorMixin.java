package me.sirsam.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningBoltRenderer.class)
public class LightningColorMixin {

    @Redirect(
            method = "quad",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setColor(FFFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private static VertexConsumer changeLightningColor(VertexConsumer instance, float r, float g, float b, float a) {
        float newRed = 1.0F;
        float newGreen = 0.0F;
        float newBlue = 0.0F;
        float newAlpha = 0.3F;

        return instance.setColor(newRed, newGreen, newBlue, newAlpha);
    }
}