package me.sirsam.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ForkliftEntity extends LivingEntity implements GeoEntity {
    protected static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("up");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public ForkliftEntity(EntityType<? extends @NotNull ForkliftEntity> type, Level level) {
        super(type, level);
    }

    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("Flying", 5, this::flyAnimController));
    }

    public static AttributeSupplier.Builder createCubeAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.TEMPT_RANGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    protected PlayState flyAnimController(final AnimationTest<@NotNull GeoAnimatable> animTest) {
        if (animTest.isMoving())
            return animTest.setAndContinue(FLY_ANIM);

        return PlayState.STOP;
    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput input) {
        super.addAdditionalSaveData(input);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(@NotNull ServerLevel level, DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypeTags.IS_FALL)
                || super.isInvulnerableTo(level, source);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void push(@NotNull Entity entity) {
        super.push(entity);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void positionRider(@NotNull Entity entity, MoveFunction moveFunction) {
        double xOffset = 0.0;
        double yOffset = 0.0;
        double zOffset = 0.0;

        Vec3 offset = new Vec3(xOffset, yOffset, zOffset).yRot(-this.getYRot() * ((float)Math.PI / 180F));

        moveFunction.accept(entity, this.getX() + offset.x, this.getY() + offset.y, this.getZ() + offset.z);
    }

    @Override
    public void rideTick() {
        super.rideTick();
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @SuppressWarnings("resource")
    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!this.isVehicle() && !player.isSecondaryUseActive()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return super.interact(player, hand);
    }
}