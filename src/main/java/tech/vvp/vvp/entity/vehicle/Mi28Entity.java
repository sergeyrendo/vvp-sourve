package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.entity.vehicle.weapon.HFireWeapon;
import tech.vvp.vvp.entity.vehicle.weapon.LmurWeapon;
import tech.vvp.vvp.entity.vehicle.weapon.S130Weapon;

import java.util.List;

import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraPitch;
import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraYaw;
import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class Mi28Entity extends ContainerMobileVehicleEntity implements GeoEntity, HelicopterEntity, WeaponVehicleEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Float> PROPELLER_ROT = SynchedEntityData.defineId(Mi28Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> LOADED_ROCKET = SynchedEntityData.defineId(Mi28Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(Mi28Entity.class, EntityDataSerializers.INT);
    public boolean engineStart;
    public boolean engineStartOver;

    public double velocity;
    public int fireIndex;
    public int holdTick;
    public int holdPowerTick;
    public float destroyRot;
    public int reloadCoolDownMissile;
    public String lockingTargetO = "none";
    public String lockingTarget = "none";
    public int lockTime;
    public boolean locked;

    public float delta_x;
    public float delta_y;

    public OBB obb;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb6;
    public OBB obb7;


    public Mi28Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.MI_28.get(), world);
    }

    public Mi28Entity(EntityType<Mi28Entity> type, Level world) {
        super(type, world);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(0.5781f, 1.2813f, 2.4375f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(0.9938f, 1.0625f, 1.3125f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.5469f, 0.6250f, 2.8438f), new Quaternionf(), OBB.Part.BODY);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.5469f, 1.2188f, 0.8750f), new Quaternionf(), OBB.Part.BODY);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(0.5469f, 0.7188f, 2.1250f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(0.5469f, 0.7188f, 2.1250f), new Quaternionf(), OBB.Part.ENGINE2);
    }

    public static Mi28Entity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<Mi28Entity> castedEntityType = (EntityType<Mi28Entity>) entityTypeFromPacket;
        Mi28Entity entity = new Mi28Entity(castedEntityType, world);
        return entity;
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new LmurWeapon()
                                .sound(ModSounds.INTO_MISSILE.get()),
                        new S130Weapon()
                                .damage(VehicleConfigVVP.MI_28_MEDIUM_ROCKET_DAMAGE.get().floatValue())
                                .explosionDamage(VehicleConfigVVP.MI_28_MEDIUM_ROCKET_EXPLOSION_DAMAGE.get().floatValue())
                                .explosionRadius(VehicleConfigVVP.MI_28_MEDIUM_ROCKET_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(ModSounds.SMALL_ROCKET_FIRE_1P.get())
                                .sound3p(ModSounds.SMALL_ROCKET_FIRE_3P.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(7, 1, -2.7);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(PROPELLER_ROT, 0f);
        this.entityData.define(LOADED_MISSILE, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedRocket", this.entityData.get(LOADED_ROCKET));
        compound.putFloat("PropellerRot", this.entityData.get(PROPELLER_ROT));
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(PROPELLER_ROT, compound.getFloat("PropellerRot"));
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> {
                    var entity = source.getDirectEntity();
                    if (entity != null && entity.getType().is(ModTags.EntityTypes.AERIAL_BOMB)) {
                        damage *= 2;
                    }
                    damage *= getHealth() > 0.1f ? 0.7f : 0.05f;
                    return damage;
                });
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.SMALL_ROCKET.get() && this.entityData.get(LOADED_ROCKET) < 10) {
            // 装载火箭
            this.entityData.set(LOADED_ROCKET, this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (stack.getItem() == tech.vvp.vvp.init.ModItems.LMUR_ITEM.get() && this.entityData.get(LOADED_MISSILE) < 4) {
            // 装载火箭
            this.entityData.set(LOADED_MISSILE, this.entityData.get(LOADED_MISSILE) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.interact(player, hand);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.lockingTargetO = getTargetUuid();
        updateOBB();

        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            if (reloadCoolDownMissile > 0) {
                reloadCoolDownMissile--;
            }
            handleAmmo();
        }

        if (this.getWeaponIndex(0) == 0) {
            seekTarget();
        }

        releaseDecoy();
        lowHealthWarning();
        this.terrainCompact(2.7f, 2.7f);

        this.refreshDimensions();
    }

    private void handleAmmo() {

        boolean hasCreativeAmmoBox = this.getFirstPassenger() instanceof Player player && InventoryTool.hasCreativeAmmoBox(player);

        int ammoCount = countItem(ModItems.SMALL_SHELL.get());

        if ((hasItem(ModItems.SMALL_ROCKET.get()) || hasCreativeAmmoBox) && reloadCoolDown == 0 && this.getEntityData().get(LOADED_ROCKET) < 10) {
            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) + 1);
            reloadCoolDown = 260;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_ROCKET.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if ((hasItem(tech.vvp.vvp.init.ModItems.LMUR_ITEM.get()) || hasCreativeAmmoBox) && reloadCoolDownMissile == 0 && this.getEntityData().get(LOADED_MISSILE) < 4) {
            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) + 1);
            reloadCoolDownMissile = 160;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter(stack -> stack.is(tech.vvp.vvp.init.ModItems.LMUR_ITEM.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_MISSILE));
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_ROCKET));
        }

    }

    public void seekTarget() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        if (getTargetUuid().equals(lockingTargetO) && !getTargetUuid().equals("none")) {
            lockTime++;
        } else {
            resetSeek(player);
        }

        Entity entity = SeekTool.seekCustomSizeEntity(this, this.level(), 384, 18, 0.9, true);
        if (entity != null) {
            if (lockTime == 0) {
                setTargetUuid(String.valueOf(entity.getUUID()));
            }
            if (!String.valueOf(entity.getUUID()).equals(getTargetUuid())) {
                resetSeek(player);
                setTargetUuid(String.valueOf(entity.getUUID()));
            }
        } else {
            setTargetUuid("none");
        }

        if (lockTime == 1) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCK.get(), 2, 1);
            }
        }

        if (lockTime > 10) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCKON.get(), 2, 1);
            }
            locked = true;
        }
    }

    public void resetSeek(Player player) {
        lockTime = 0;
        locked = false;
        if (player instanceof ServerPlayer serverPlayer) {
            var clientboundstopsoundpacket = new ClientboundStopSoundPacket(new ResourceLocation(Mod.MODID, "jet_lock"), SoundSource.PLAYERS);
            serverPlayer.connection.send(clientboundstopsoundpacket);
        }
    }

    public void setTargetUuid(String uuid) {
        this.lockingTarget = uuid;
    }

    public String getTargetUuid() {
        return this.lockingTarget;
    }

    @Override
    public void travel() {
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1, 0.8));
        } else {
            setZRot(getRoll() * (backInputDown ? 0.9f : 0.99f));
            float f = (float) Mth.clamp(0.95f - 0.015 * getDeltaMovement().length() + 0.02f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).scale((this.getXRot() < 0 ? -0.035 : (this.getXRot() > 0 ? 0.035 : 0)) * this.getDeltaMovement().length())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.95, f));
        }

        if (this.isInWater() && this.tickCount % 4 == 0 && getSubmergedHeight(this) > 0.5 * getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, this.getFirstPassenger() == null ? this : this.getFirstPassenger()), 6 + (float) (20 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
        }

        Entity passenger = getFirstPassenger();
        Entity passenger2 = getNthEntity(1);
        Entity passenger3 = getNthEntity(2);
        Entity passenger4 = getNthEntity(3);
        float diffX;
        float diffZ;

        if (getHealth() > 0.1f * getMaxHealth()) {
            if (passenger == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.upInputDown = false;
                this.downInputDown = false;
                this.setZRot(this.roll * 0.98f);
                this.setXRot(this.getXRot() * 0.98f);
                if (passenger2 == null && passenger3 == null && passenger4 == null) {
                    this.entityData.set(POWER, this.entityData.get(POWER) * 0.99f);
                }
            } else if (passenger instanceof Player player) {
                delta_x = ((this.onGround()) ? 0 : 0.9f) * entityData.get(MOUSE_SPEED_Y) * this.entityData.get(PROPELLER_ROT);
                delta_y = Mth.clamp((this.onGround() ? 0.06f : 1.2f) * entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT) + (this.entityData.get(ENGINE2_DAMAGED) ? 25 : 0) * this.entityData.get(PROPELLER_ROT), -6f, 6f);

                if (!entityData.get(LANDING_INPUT_DOWN) || findNearestLandingPos(30) == null) {
                    if (rightInputDown) {
                        holdTick++;
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 1.2f * Math.min(holdTick, 5) * this.entityData.get(POWER));
                    } else if (this.leftInputDown) {
                        holdTick++;
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 1.2f * Math.min(holdTick, 5) * this.entityData.get(POWER));
                    } else {
                        holdTick = 0;
                    }
                    this.setXRot(this.getXRot() + delta_x);
                    this.setZRot(this.getRoll() - this.entityData.get(DELTA_ROT) + (this.onGround() ? 0 : 0.15f) * entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT));
                }

                this.setYRot(this.getYRot() + delta_y);
                if (findNearestLandingPos(30) != null && !onGround() && entityData.get(LANDING_INPUT_DOWN)) {
                    this.updateAutoLanding(findNearestLandingPos(30));
                }

                if (level().isClientSide && findNearestLandingPos(30) != null && !onGround()) {
                    player.displayClientMessage(Component.translatable("tips.superbwarfare.press_s_to_landing"), true);
                }
            }

            if (this.level() instanceof ServerLevel) {
                if (this.getEnergy() > 0) {
                    boolean up = upInputDown || forwardInputDown;
                    boolean down = this.downInputDown;

                    if (!engineStart && up) {
                        engineStart = true;
                        this.level().playSound(null, this, ModSounds.HELICOPTER_ENGINE_START.get(), this.getSoundSource(), 3, 1);
                    }

                    if (up && engineStartOver) {
                        holdPowerTick++;
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0007f * Math.min(holdPowerTick, 10), 0.12f));
                    }

                    if (engineStartOver) {
                        if (down) {
                            holdPowerTick++;
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.001f * Math.min(holdPowerTick, 5), this.onGround() ? 0 : 0.025f));
                        } else if (backInputDown) {
                            holdPowerTick++;
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.001f * Math.min(holdPowerTick, 5), this.onGround() ? 0 : 0.059f));
                        }

                    }

                    if (engineStart && !engineStartOver) {
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0012f, 0.045f));
                    }

                    if (!(up || down || backInputDown) && engineStartOver) {
                        if (this.getDeltaMovement().y() < 0) {
                            this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0002f, 0.12f));
                        } else {
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.onGround() ? 0.00005f : 0.0002f), 0));
                        }
                        holdPowerTick = 0;
                    }
                } else {
                    this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.0001f, 0));
                    this.forwardInputDown = false;
                    this.backInputDown = false;
                    engineStart = false;
                    engineStartOver = false;
                }
            }
        } else if (!onGround() && engineStartOver) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.0003f, 0.01f));
            destroyRot += 0.08f;

            diffX = 45 - this.getXRot();
            diffZ = -20 - this.getRoll();

            this.setXRot(this.getXRot() + diffX * 0.05f * this.entityData.get(PROPELLER_ROT));
            this.setYRot(this.getYRot() + destroyRot);
            this.setZRot(this.getRoll() + diffZ * 0.1f * this.entityData.get(PROPELLER_ROT));
            setDeltaMovement(getDeltaMovement().add(0, -destroyRot * 0.004, 0));
        }

        if (entityData.get(ENGINE1_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.98f);
        }

        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.9f);
        this.entityData.set(PROPELLER_ROT, Mth.lerp(0.18f, this.entityData.get(PROPELLER_ROT), this.entityData.get(POWER)));
        this.setPropellerRot(this.getPropellerRot() + 30 * this.entityData.get(PROPELLER_ROT));
        this.entityData.set(PROPELLER_ROT, this.entityData.get(PROPELLER_ROT) * 0.9995f);

        if (engineStart) {
            this.consumeEnergy((int) (VehicleConfig.AH_6_MIN_ENERGY_COST.get() + this.entityData.get(POWER) * ((VehicleConfig.AH_6_MAX_ENERGY_COST.get() - VehicleConfig.AH_6_MIN_ENERGY_COST.get()) / 0.12)));
        }

        Matrix4f transform = getVehicleTransform(1);

        Vector4f force0 = transformPosition(transform, 0, 0, 0);
        Vector4f force1 = transformPosition(transform, 0, 1, 0);

        Vec3 force = new Vec3(force0.x, force0.y, force0.z).vectorTo(new Vec3(force1.x, force1.y, force1.z));

        setDeltaMovement(getDeltaMovement().add(force.scale(this.entityData.get(PROPELLER_ROT))));

        if (this.entityData.get(POWER) > 0.04f) {
            engineStartOver = true;
        }

        if (this.entityData.get(POWER) < 0.0004f) {
            engineStart = false;
            engineStartOver = false;
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.HELICOPTER_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return entityData.get(PROPELLER_ROT) * 2f;
    }

    protected void clampRotation(Entity entity) {
        if (entity == getNthEntity(0)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, -80.0F, 80F);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == getNthEntity(2)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, 10.0F, 170.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(getYRot() + 90);
        } else if (entity == getNthEntity(3)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -170.0F, -10.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(getYRot() - 90);
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        // From Immersive_Aircraft
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1);

        float x = 0.0000f;
        float y = 1.83f - 1.45f;
        float z = 0.11f;

        float x_1 = 0.0000f;
        float y_2 = 1.2f - 1.45f;
        float z_3 = 1.4f;

        y += (float) passenger.getMyRidingOffset();

        int i = this.getOrderedPassengers().indexOf(passenger);

        if (i == 0) {
            Vector4f worldPosition = transformPosition(transform, x, y, z);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        } else if (i == 1) {
            Vector4f worldPosition = transformPosition(transform, x_1, y_2, z_3);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        }

        if (passenger != this.getFirstPassenger()) {
            passenger.setXRot(passenger.getXRot() + (getXRot() - xRotO));
        }

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYHeadRot(entity.getYHeadRot() + delta_y);
            entity.setYRot(entity.getYRot() + delta_y);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getYRot() - getYRot());
            float g = Mth.clamp(f, -105.0f, 105.0f);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f + 0.9f * destroyRot);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(getYRot());
        }
    }

    @Override
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 1.45f, getY() + 1.45f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, yRotO, getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

//    public Matrix4f getGunTransform(float ticks) {
//        Matrix4f transformT = getTurretTransform(ticks);
//
//        Matrix4f transform = new Matrix4f();
//        Vector4f worldPosition = transformPosition(transform, -0.6168875f, 0.7952750f, -1.0803625f);
//
//        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
//        transformT.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, gunYRotO, getGunYRot()) - Mth.lerp(ticks, turretYRotO, getTurretYRot())));
//        return transformT;
//    }
//
//    public Matrix4f getGunnerBarrelTransform(float ticks) {
//        Matrix4f transformG = getGunTransform(ticks);
//
//        Matrix4f transform = new Matrix4f();
//        Vector4f worldPosition = transformPosition(transform, 0f, 0.35984375f, 0.0551625f);
//
//        transformG.translate(worldPosition.x, worldPosition.y, worldPosition.z);
//
//        float a = getTurretYaw(ticks);
//
//        float r = (Mth.abs(a) - 90f) / 90f;
//
//        float r2;
//
//        if (Mth.abs(a) <= 90f) {
//            r2 = a / 90f;
//        } else {
//            if (a < 0) {
//                r2 = -(180f + a) / 90f;
//            } else {
//                r2 = (180f - a) / 90f;
//            }
//        }
//
//        float x = Mth.lerp(ticks, gunXRotO, getGunXRot());
//        float xV = Mth.lerp(ticks, xRotO, getXRot());
//        float z = Mth.lerp(ticks, prevRoll, getRoll());
//
//        transformG.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
//        return transformG;
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition = transformPosition(transform, 0f, -0.83f, 0.8f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition = transformPosition(transform, 0, 0, 0);
        Vector4f worldPosition2 = transformPosition(transform, 0, 0.01f, 1);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();
    }

    @Override
    public void vehicleShoot(LivingEntity living, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {
                hasCreativeAmmo = true;
            }
        }

        Matrix4f transform = getVehicleTransform(1);

        if (getWeaponIndex(0) == 0 && this.getEntityData().get(LOADED_MISSILE) > 0) {
            var LmurEntity = ((LmurWeapon) getWeapon(0)).create(living);

            Vector4f worldPosition;

            if (this.getEntityData().get(LOADED_MISSILE) == 4) {
                worldPosition = transformPosition(transform, 5.28f, -1.76f, 1.87f);
            } else if (this.getEntityData().get(LOADED_MISSILE) == 3) {
                worldPosition = transformPosition(transform, -5.28f, -1.76f, 1.87f);
            } else if (this.getEntityData().get(LOADED_MISSILE) == 2) {
                worldPosition = transformPosition(transform, 6.63f, -1.55f, 1.83f);
            } else {
                worldPosition = transformPosition(transform, -6.63f, -1.55f, 1.83f);
            }

            if (locked) {
                LmurEntity.setTargetUuid(getTargetUuid());
            }
            LmurEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            LmurEntity.shoot(shootVec(1).x, shootVec(1).y, shootVec(1).z, (float) getDeltaMovement().length() + 1, 1);
            living.level().addFreshEntity(LmurEntity);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3, 1);

            if (this.getEntityData().get(LOADED_MISSILE) == 4) {
                reloadCoolDownMissile = 260;
            }

            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);
        } else if (getWeaponIndex(0) == 1 && this.getEntityData().get(LOADED_ROCKET) > 0) {

            var heliRocketEntity = ((S130Weapon) getWeapon(0)).create(living);

            Vector4f worldPosition;
            Vector4f worldPosition2;

            if (fireIndex == 0) {
                worldPosition = transformPosition(transform, 40f/16f, 23f/16f, -4f/16f);
                worldPosition2 = transformPosition(transform, 40f/16f + 0.009f - 0.0025f, 23f/16f + 0.012f, 1.8f);
                fireIndex = 1;
            } else {
                worldPosition = transformPosition(transform, -40f/16f, 23f/16f, -4f/16f);
                worldPosition2 = transformPosition(transform, -40f/16f + 0.009f - 0.0025f, 23f/16f + 0.012f, 1.8f);
                fireIndex = 0;
            }

            Vec3 shootVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();

            heliRocketEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 7, 0.25f);
            living.level().addFreshEntity(heliRocketEntity);

            playShootSound3p(living, 0, 6, 6, 6, new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) - 1);
            reloadCoolDown = 100;
        }
    }

    @Override
    public int mainGunRpm(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return 50;
            } else if (getWeaponIndex(0) == 1) {
                return 150;
            }
        }

        return 0;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        if (getWeaponIndex(0) == 0) {
            return this.entityData.get(AMMO) > 0;
        } else if (getWeaponIndex(0) == 1) {
            return this.entityData.get(AMMO) > 0;
        }
        return false;
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        return this.entityData.get(AMMO);
    }

    @Override
    public int zoomFov() {
        return 5;
    }

    @Override
    public int getWeaponHeat(LivingEntity living) {
        return entityData.get(HEAT);
    }

    @Override
    public float getRotX(float tickDelta) {
        return this.getPitch(tickDelta);
    }

    @Override
    public float getRotY(float tickDelta) {
        return this.getYaw(tickDelta);
    }

    @Override
    public float getRotZ(float tickDelta) {
        return this.getRoll(tickDelta);
    }

    @Override
    public float getPower() {
        return this.entityData.get(POWER);
    }

    @Override
    public int getDecoy() {
        return this.entityData.get(DECOY_COUNT);
    }

    @Override
    public int getMaxPassengers() {
        return 2;
    }

    @Override
    public int passengerSeatLocation(Entity entity) {
        return entity == getNthEntity(0) ? 2 : 0;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/ah_6_icon.png");
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return seatIndex == 0 ? 0 : original;
    }

    @Override
    public double getMouseSensitivity() {
        return 0.25;
    }

    @Override
    public double getMouseSpeedX() {
        return 0.4;
    }

    @Override
    public double getMouseSpeedY() {
        return 0.25;
    }

    @Override
    public @NotNull Vec3 getDismountLocationForIndex(LivingEntity passenger, int index) {
        Matrix4f transform = getVehicleTransform(1);
        Vector4f worldPosition;

        if (index == 0) {
            worldPosition = transformPosition(transform, 2f, -0.25f, 1f);
        } else if (index == 1) {
            worldPosition = transformPosition(transform, -2f, -0.25f, 1f);
        } else if (index == 2) {
            worldPosition = transformPosition(transform, -2f, -0.25f, 0);
        } else {
            worldPosition = transformPosition(transform, 2f, -0.25f, 0);
        }

        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        if (this.getSeatIndex(entity) == 2) {
            return Pair.of(Axis.XP.rotationDegrees(-this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(this.getViewXRot(tickDelta)));
        } else if (this.getSeatIndex(entity) == 3) {
            return Pair.of(Axis.XP.rotationDegrees(this.getRoll(tickDelta)), Axis.ZP.rotationDegrees(-this.getViewXRot(tickDelta)));
        }
        return Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 1.45f, getY() + 1.45f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees((float) (-Mth.lerp(ticks, yRotO, getYRot()) + freeCameraYaw)));
        transform.rotate(Axis.XP.rotationDegrees((float) (Mth.lerp(ticks, xRotO, getXRot()) + freeCameraPitch)));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 0) {
            return new Vec2((float) (getRotY(partialTicks) - freeCameraYaw), (float) (getRotX(partialTicks) + freeCameraPitch));
        }

        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 0) {
            Matrix4f transform = getClientVehicleTransform(partialTicks);
            Vector4f maxCameraPosition = transformPosition(transform, -2.1f, 1, -10 - (float) ClientMouseHandler.custom3pDistanceLerp);
            Vec3 finalPos = CameraTool.getMaxZoom(transform, maxCameraPosition);

            if (isFirstPerson) {
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
            } else {
                return finalPos;
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public int getHudColor() {
        return super.getHudColor();
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return VVP.loc("textures/gui/vehicle/type/aircraft.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb6, this.obb7);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0.0313f, 2.2031f - 1.45f, -2.9375f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, -0.0313f, 2.4219f - 1.45f, 0.8125f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 0.0000f, 1.5469f - 1.45f, -8.1875f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 0.0000f, 2.1406f - 1.45f, -11.9375f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition6 = transformPosition(transform, 1.1250f, 2.4688f - 1.45f, -3.0000f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition7 = transformPosition(transform, -1.1250f, 2.4688f - 1.45f, -3.0000f);
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(1, this));
    }
}
