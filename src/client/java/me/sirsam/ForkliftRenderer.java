package me.sirsam;

import me.sirsam.entity.ForkliftEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class ForkliftRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<@NotNull ForkliftEntity, @NotNull R> {
    public ForkliftRenderer(EntityRendererProvider.Context context) {
        super(context, new ForkliftModel());

        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public boolean shouldShowName(@NotNull ForkliftEntity animatable, double distToCameraSq) {
        return false;
    }
}