package me.sirsam.entity;

import me.sirsam.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
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
    protected static final RawAnimation UP_ANIM = RawAnimation.begin().thenLoop("up");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    protected final SimpleContainer inventory = new SimpleContainer(9) {
        @Override
        public boolean canPlaceItem(int i, @NotNull ItemStack itemStack) {
            return itemStack.is(ModItems.FUEL_CANISTER.asItem());
        }
    };

    public ForkliftEntity(EntityType<? extends @NotNull ForkliftEntity> type, Level level) {
        super(type, level);

    }

    public void registerControllers(final AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("up", 0, this::forkAnimController));
    }

    public static AttributeSupplier.Builder createCubeAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.TEMPT_RANGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.SCALE, 1.5)
                .add(Attributes.STEP_HEIGHT, 1);
    }

    protected PlayState forkAnimController(final AnimationTest<@NotNull GeoAnimatable> animTest) {
        if (true) {
            System.out.println("move");
            return animTest.setAndContinue(UP_ANIM);
        }

        return PlayState.STOP;
    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
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
        double xOffset = -0.23;
        double yOffset = 0.7;
        double zOffset = -0.45;

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

    @Override
    public LivingEntity getControllingPassenger() {
        return (this.getFirstPassenger() instanceof LivingEntity entity) ? entity : null;
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.isAlive()) {
            LivingEntity passenger = this.getControllingPassenger();
            if (passenger != null) {
                // 1. Match rotation to the driver
                this.setYRot(passenger.getYRot());
                this.yRotO = this.getYRot();
                this.setXRot(passenger.getXRot() * 0.5F);
                this.setRot(this.getYRot(), this.getXRot());
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.yBodyRot;

                // 2. Get Input Directions from the passenger
                float forward = passenger.zza; // W/S keys
                float strafe = passenger.xxa;  // A/D keys

                if (forward <= 0.0F) {
                    forward *= 0.5F; // Realistic: Reverse is slower
                }

                // 3. Handle Sprinting
                float speedMultiplier = passenger.isSprinting() ? 1.5F : 1.0F;

                // 4. Movement Execution
                // In LivingEntity, we check if we are controlled by the client/local player
                if (this.shouldClientDrive()) {
                    this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMultiplier);
                    super.travel(new Vec3(strafe, travelVector.y, forward));
                } else {
                    // This helps prevent "ghost" movement on the server/other clients
                    this.setDeltaMovement(Vec3.ZERO);
                }

                return;
            }
        }

        // Default physics (gravity, etc.) when no one is riding
        super.travel(travelVector);
    }

    protected boolean shouldClientDrive() {
        return this.getControllingPassenger() instanceof Player player && player.isLocalPlayer();
    }

    @SuppressWarnings("resource")
    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!this.level().isClientSide()) {

                player.openMenu(new SimpleMenuProvider(
                        (id, playerInv, p) -> new ChestMenu(
                                MenuType.GENERIC_9x1,
                                id,
                                playerInv,
                                this.inventory,
                                1
                        ),
                        Component.literal("Forklift Fuel Tank")
                ));
            }
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        if (!this.isVehicle()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return this.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return super.interact(player, hand);
    }

    private int dancingTimeLeft;
    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putInt("dancing_time_left", dancingTimeLeft);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        dancingTimeLeft = valueInput.getInt("dancing_time_left").orElse(0);
        //setDancing(dancingTimeLeft > 0);
    }
}