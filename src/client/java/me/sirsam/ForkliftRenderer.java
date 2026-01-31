package me.sirsam;

import me.sirsam.entity.ForkliftEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.BoneSnapshots;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.RenderPassInfo;
import software.bernie.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

public class ForkliftRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<@NotNull ForkliftEntity, @NotNull R> {
    public static final DataTicket<@NotNull Float> FRONT_BONE_HEIGHT = DataTicket.create("forklift_lift_height", Float.class);

    public ForkliftRenderer(EntityRendererProvider.Context context) {
        super(context, new ForkliftModel());

        this.withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public int getPackedOverlay(@NotNull ForkliftEntity animatable, @Nullable Void relatedObject, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    @Override
    public void addRenderData(@NotNull ForkliftEntity animatable, @Nullable Void relatedObject, @NotNull R renderState, float partialTick) {
        float lerpedHeight = Mth.lerp(partialTick, animatable.lastLiftHeight, animatable.getLiftHeight());
        renderState.addGeckolibData(FRONT_BONE_HEIGHT, lerpedHeight);
    }

    @Override
    public boolean shouldShowName(@NotNull ForkliftEntity animatable, double distToCameraSq) {
        return false;
    }

    @Override
    public void adjustModelBonesForRender(@NotNull RenderPassInfo<@NotNull R> renderPassInfo, @NotNull BoneSnapshots snapshots) {
        snapshots.ifPresent("front", boneSnapshot -> {
            boneSnapshot.setTranslateY(renderPassInfo.renderState().getGeckolibData(FRONT_BONE_HEIGHT));
        });
    }
}