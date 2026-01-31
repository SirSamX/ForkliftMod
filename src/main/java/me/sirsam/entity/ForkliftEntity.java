package me.sirsam.entity;

import me.sirsam.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
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
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ForkliftEntity extends LivingEntity implements GeoEntity {
    private float currentDriveSpeed = 0.0F;
    public float prevSteeringAngle = 0.0F;
    public float steeringAngle = 0.0F;
    private int backupBeeperTimer = 0;
    public float lastLiftHeight = 0.0F;

    public static final RawAnimation STEER_ANIM = RawAnimation.begin().thenLoop("move.steer");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public static final EntityDataAccessor<@NotNull Float> LIFT_HEIGHT =
            SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.FLOAT);

    public static final EntityDataAccessor<@NotNull Float> DRIVE_SPEED =
            SynchedEntityData.defineId(ForkliftEntity.class, EntityDataSerializers.FLOAT);

    protected final SimpleContainer inventory = new SimpleContainer(9) {
        @Override
        public boolean canPlaceItem(int i, @NotNull ItemStack itemStack) {
            return itemStack.is(ModItems.FUEL_CANISTER.asItem());
        }
    };

    public ForkliftEntity(EntityType<? extends @NotNull ForkliftEntity> type, Level level) {
        super(type, level);
    }

    public float getLiftHeight() {
        return entityData.get(LIFT_HEIGHT);
    }

    public void setLiftHeight(float h) {
        if (Math.abs(getLiftHeight() - h) > 0.01f) {
            entityData.set(LIFT_HEIGHT, Mth.clamp(h, 0.0F, 25.0F));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LIFT_HEIGHT, 0.0F);
        builder.define(DRIVE_SPEED, 0.0F);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("drive_controller", state -> {
            float speed = ForkliftEntity.this.entityData.get(DRIVE_SPEED);

            if (Math.abs(speed) > 0.01F) {
                return state.setAndContinue(DefaultAnimations.DRIVE);
            }
            return PlayState.STOP;
        }));
        controllers.add(new AnimationController<>("steer_controller", state -> state.setAndContinue(STEER_ANIM)).additiveAnimations());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.TEMPT_RANGE, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.SCALE, 1.5)
                .add(Attributes.STEP_HEIGHT, 1)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1);
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
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return (this.getFirstPassenger() instanceof LivingEntity entity) ? entity : null;
    }

    @Override
    public void tick() {
        super.tick();

        lastLiftHeight = getLiftHeight();
        this.prevSteeringAngle = this.steeringAngle;

        // --- STEERING ---
        if (this.level().isClientSide()) {
            LivingEntity passenger = this.getControllingPassenger();
            if (passenger instanceof Player player && player.isLocalPlayer()) {
                float steerInput = player.xxa;
                if (steerInput != 0) {
                    this.steeringAngle = Mth.clamp(this.steeringAngle + (steerInput * 4.5F), -45.0F, 45.0F);
                } else {
                    this.steeringAngle = Mth.lerp(0.2F, this.steeringAngle, 0.0F);
                }
            }
        }
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (!this.isAlive()) {
            super.travel(travelVector);
            return;
        }

        LivingEntity passenger = this.getControllingPassenger();
        if (passenger != null) {
            // Correct logic to check for the local player's input on client vs server processing
            boolean isLocalControl = this.level().isClientSide() && passenger instanceof Player player && player.isLocalPlayer();
            boolean isServerLogic = !this.level().isClientSide();

            if (isLocalControl || isServerLogic) {
                float forwardInput = passenger.zza;
                float maxSpeed = (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

                // --- DRIVE LOGIC ---
                if (Math.abs(forwardInput) > 0) {
                    this.currentDriveSpeed = Mth.lerp(0.08F, this.currentDriveSpeed, forwardInput * maxSpeed);
                } else {
                    this.currentDriveSpeed = Mth.lerp(0.02F, this.currentDriveSpeed, 0);
                    if (Math.abs(this.currentDriveSpeed) < 0.005F) {
                        this.currentDriveSpeed = 0.0F;
                    }
                }

                this.entityData.set(DRIVE_SPEED, this.currentDriveSpeed);

                if (Math.abs(this.currentDriveSpeed) > 0.01F) {
                    // Adjust turn rate based on speed
                    float turnIntensity = (this.steeringAngle / 45.0F) * (this.currentDriveSpeed > 0 ? 5.5F : -5.5F);
                    this.setYRot(this.getYRot() - turnIntensity);
                    this.yRotO = this.getYRot();
                }

                // --- MOVEMENT ---
                float rotationRadians = this.getYRot() * ((float) Math.PI / 180F);
                this.setDeltaMovement(-Mth.sin(rotationRadians) * this.currentDriveSpeed, travelVector.y, Mth.cos(rotationRadians) * this.currentDriveSpeed);
                this.move(MoverType.SELF, this.getDeltaMovement());

                // --- SERVER-ONLY EFFECTS (Sound) ---
                if (isServerLogic) {
                    if (this.currentDriveSpeed < -0.05F) {
                        if (this.backupBeeperTimer <= 0) {
                            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                                    SoundEvents.NOTE_BLOCK_PLING, this.getSoundSource(), 1.0F, 2.0F);
                            this.backupBeeperTimer = 15;
                        }
                        this.backupBeeperTimer--;
                    } else {
                        this.backupBeeperTimer = 0;
                    }
                }
            }
            return;
        }

        // Reset when empty
        this.currentDriveSpeed = 0;
        if (!this.level().isClientSide()) {
            this.entityData.set(DRIVE_SPEED, 0.0F);
        }
        super.travel(travelVector);
    }

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

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return SoundEvents.NETHERITE_BLOCK_BREAK;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.NETHERITE_BLOCK_BREAK;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput valueOutput) {
        super.addAdditionalSaveData(valueOutput);
        valueOutput.putFloat("lift_height", getLiftHeight());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput valueInput) {
        super.readAdditionalSaveData(valueInput);
        setLiftHeight(valueInput.getInt("lift_height").orElse(0));
    }
}