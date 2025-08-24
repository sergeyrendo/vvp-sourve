
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.SwarmDroneEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SwarmDroneWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;

import java.util.List;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class M60Entity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_AP = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_HE = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_AMMO_TYPE = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUN_FIRE_TIME = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_DRONE = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(M60Entity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int droneReloadCoolDown;

    public OBB obb;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obbTurret;

    public M60Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.M60.get(), world);
    }

    public M60Entity(EntityType<M60Entity> type, Level world) {
        super(type, world);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(1.141f, 0.688f, 2.156f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(1.141f, 0.688f, 1.312f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.359f, 0.844f, 3.344f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.359f, 0.844f, 3.344f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(1.359f, 0.688f, 2.219f), new Quaternionf(), OBB.Part.TURRET);
    }

    @SuppressWarnings("unchecked")
    public static M60Entity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<M60Entity> castedEntityType = (EntityType<M60Entity>) entityTypeFromPacket;
        M60Entity entity = new M60Entity(castedEntityType, world);
        return entity;
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfigVVP.M60_CANNON_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.M60_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(VehicleConfigVVP.M60_CANNON_EXPLOSION_DAMAGE.get())
                                .fireProbability(0)
                                .fireTime(0)
                                .durability(100)
                                .velocity(40)
                                .gravity(0.1f)
                                .sound(ModSounds.INTO_MISSILE.get())
                                .ammo(ModItems.AP_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/ap_shell.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.M1128_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.M1128_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.M1128_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.M1128_VERYFAR.get()),
                        new ProjectileWeapon()
                                .damage(VehicleConfig.LAV_150_MACHINE_GUN_DAMAGE.get())
                                .headShot(2).zoom(false)
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_7_62mm.png"))
                                .sound1p(ModSounds.COAX_FIRE_1P.get())
                                .sound3p(ModSounds.RPK_FIRE_3P.get())
                                .sound3pFar(ModSounds.RPK_FAR.get())
                                .sound3pVeryFar(ModSounds.RPK_VERYFAR.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return switch (index) {
            case 0 -> new ThirdPersonCameraPosition(5 + ClientMouseHandler.custom3pDistanceLerp, 1.5, -0.8669625);
            case 1 -> new ThirdPersonCameraPosition(-1 + 0.5 * ClientMouseHandler.custom3pDistanceLerp, 0.5, 0);
            default -> null;
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(LOADED_AP, 0);
        this.entityData.define(LOADED_HE, 0);
        this.entityData.define(LOADED_DRONE, 0);
        this.entityData.define(LOADED_AMMO_TYPE, 0);
        this.entityData.define(GUN_FIRE_TIME, 0);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedAP", this.entityData.get(LOADED_AP));
        compound.putInt("LoadedHE", this.entityData.get(LOADED_HE));
        compound.putInt("LoadedDrone", this.entityData.get(LOADED_DRONE));
        compound.putInt("LoadedAmmoType", this.entityData.get(LOADED_AMMO_TYPE));
        compound.putInt("WeaponType", getWeaponIndex(0));
        compound.putInt("PassengerWeaponType", getWeaponIndex(1));
        compound.putInt("ThirdPassengerWeaponType", getWeaponIndex(2));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_AP, compound.getInt("LoadedAP"));
        this.entityData.set(LOADED_HE, compound.getInt("LoadedHE"));
        this.entityData.set(LOADED_DRONE, compound.getInt("LoadedDrone"));
        this.entityData.set(LOADED_AMMO_TYPE, compound.getInt("LoadedAmmoType"));
        setWeaponIndex(0, compound.getInt("WeaponType"));
        setWeaponIndex(1, compound.getInt("PassengerWeaponType"));
        setWeaponIndex(2, compound.getInt("ThirdPassengerWeaponType"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.5f) * damage);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.15), random.nextFloat() * 0.15f + 1.05f);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        // Оригинальная логика
        this.updateOBB();
        if (getLeftTrack() < 0) {
            setLeftTrack(80);
        }
        if (getLeftTrack() > 80) {
            setLeftTrack(0);
        }
        if (getRightTrack() < 0) {
            setRightTrack(80);
        }
        if (getRightTrack() > 80) {
            setRightTrack(0);
        }
        if (this.entityData.get(GUN_FIRE_TIME) > 0) {
            this.entityData.set(GUN_FIRE_TIME, this.entityData.get(GUN_FIRE_TIME) - 1);
        }
        if (reloadCoolDown == 60 && this.getFirstPassenger() instanceof Player player) {
            SoundTool.playLocalSound(player, tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get());
        }
        if (this.level() instanceof ServerLevel) {
            boolean hasCreativeAmmo = false;
            for (int i = 0; i < getMaxPassengers(); i++) {
                if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }
            if (reloadCoolDown > 0 && (
                    (entityData.get(LOADED_AMMO_TYPE) == 0 && (hasCreativeAmmo || countItem(ModItems.AP_5_INCHES.get()) > 0)) ||
                            (entityData.get(LOADED_AMMO_TYPE) == 1 && (hasCreativeAmmo || countItem(ModItems.HE_5_INCHES.get()) > 0))
            )) {
                reloadCoolDown--;
            }

            this.handleAmmo();
        }
        if (this.onGround()) {
            float f0 = 0.54f + 0.25f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.05 * getDeltaMovement().dot(getViewVector(1)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.99, f0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }
        if (this.isInWater()) {
            float f1 = (float) (0.7f - (0.04f * Math.min(getSubmergedHeight(this), this.getBbHeight())) + 0.08f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.04 * getDeltaMovement().dot(getViewVector(1)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.85, f1));
        }
        if (this.level() instanceof ServerLevel serverLevel && this.isInWater() && this.getDeltaMovement().length() > 0.1) {
            sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 4 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
            sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 10 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
        }
        turretAngle(3f, 3f);
        lowHealthWarning();
        terrainCompact(4.375f, 6.3125f);
        inertiaRotate(1.2f);
        releaseSmokeDecoy(getTurretVector(1));
        this.refreshDimensions();
    }

    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.05 || Mth.abs(this.entityData.get(POWER)) > 0.1;
    }

    @Override
    public boolean canCollideBlockBeastly() {
        return getDeltaMovement().horizontalDistance() > 0.3;
    }

    private void handleAmmo() {
        boolean hasCreativeAmmo = false;
        if (this.getFirstPassenger() instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
            hasCreativeAmmo = true;
        }

        if (hasCreativeAmmo) {
            this.entityData.set(AMMO, 9999);
        } else {
            int ammoCount = this.getItemStacks().stream().filter(stack -> {
                if (stack.is(ModItems.AMMO_BOX.get())) return Ammo.RIFLE.get(stack) > 0;
                return false;
            }).mapToInt(Ammo.RIFLE::get).sum() + countItem(ModItems.RIFLE_AMMO.get());
            this.entityData.set(AMMO, ammoCount);
        }

        if (entityData.get(LOADED_AMMO_TYPE) == 0 && this.entityData.get(LOADED_AP) == 0 && reloadCoolDown <= 0) {
            if (hasCreativeAmmo || hasItem(ModItems.AP_5_INCHES.get())) {
                this.entityData.set(LOADED_AP, 1);
                if (!hasCreativeAmmo) {
                    consumeItem(ModItems.AP_5_INCHES.get(), 1);
                }
            }
        }
    }

    @Override
    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        super.move(movementType, movement);
        if (this.isInWater() && horizontalCollision) {
            setDeltaMovement(this.getDeltaMovement().add(0, 0.07, 0));
        }
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = (this.getFirstPassenger() instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer));
        Matrix4f transform = getBarrelTransform(1);

        if (getWeaponIndex(0) == 0) {
            if (this.cannotFire || this.entityData.get(LOADED_AP) <= 0) return;

            float x = 0f;
            float y = -0.3f;
            float z = 4.0927625f;
            Vector4f worldPosition = transformPosition(transform, x, y, z);
            var cannonShell = ((CannonShellWeapon) getWeapon(0)).create(player);

            cannonShell.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
            cannonShell.shoot(getBarrelVector(1).x, getBarrelVector(1).y + 0.005f, getBarrelVector(1).z, 35, 0.25f);
            this.level().addFreshEntity(cannonShell);

            this.entityData.set(LOADED_AP, 0);
            this.reloadCoolDown = 80;

        } else if (getWeaponIndex(0) == 1) {
            if (this.cannotFireCoax) return;
            float x = -0.6f;
            float y = 0.65f;
            float z = 0.4f;
            Vector4f worldPosition = transformPosition(transform, x, y, z);

            if (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                var projectile = ((ProjectileWeapon) getWeapon(0)).create(player).setGunItemId(this.getType().getDescriptionId());
                projectile.bypassArmorRate(0.2f);
                projectile.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
                projectile.shoot(player, getBarrelVector(1).x, getBarrelVector(1).y + 0.002f, getBarrelVector(1).z, 36, 0.25f);
                this.level().addFreshEntity(projectile);

                if (!hasCreativeAmmo) {
                    ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                        if (stack.is(ModItems.AMMO_BOX.get())) return Ammo.RIFLE.get(stack) > 0;
                        return false;
                    }).findFirst().orElse(ItemStack.EMPTY);

                    if (!ammoBox.isEmpty()) {
                        Ammo.RIFLE.add(ammoBox, -1);
                    } else {
                        this.getItemStacks().stream().filter(stack -> stack.is(ModItems.RIFLE_AMMO.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                    }
                }
            }
            this.entityData.set(COAX_HEAT, this.entityData.get(COAX_HEAT) + 3);
            this.entityData.set(FIRE_ANIM, 2);
            if (!player.level().isClientSide) {
                playShootSound3p(player, 0, 3, 6, 12);
            }
        }
    }

    @Override
    public void travel() {
        trackEngine(false, 0, VehicleConfigVVP.M60_ENERGY_COST.get(), 1.25, 0.75, 1.3, 0.6, 0.25f, -0.2f, 0.0026f, 0.002f, 0.1f);
    }


    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.YX_100_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Math.max(Mth.abs(entityData.get(POWER)), Mth.abs(0.1f * this.entityData.get(DELTA_ROT))) * 2.5f;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        // From Immersive_Aircraft
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getTurretTransform(1);

        int i = this.getOrderedPassengers().indexOf(passenger);

        var worldPosition = switch (i) {
            case 0 -> transformPosition(transform, 0f, 0f, 0);
            case 1 -> transformPosition(transform, -0.50f, 0.3f, -1f);
            case 2 -> transformPosition(transform, 0.6669625f, 0.07f, -1f);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };

        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYBodyRot(getBarrelYRot(1));
        }
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getTurretTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 1f, 0.6076875f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public int getMaxPassengers() {
        return 3;
    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = getTurretTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    @Override
    public Vec3 getGunnerVector(float pPartialTicks) {
        Matrix4f transform = getGunnerBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0f, 0.56345f, 0.6477125f);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);

        float a = getTurretYaw(ticks);

        float r = (Mth.abs(a) - 90f) / 90f;

        float r2;

        if (Mth.abs(a) <= 90f) {
            r2 = a / 90f;
        } else {
            if (a < 0) {
                r2 = -(180f + a) / 90f;
            } else {
                r2 = (180f - a) / 90f;
            }
        }

        float x = Mth.lerp(ticks, turretXRotO, getTurretXRot());
        float xV = Mth.lerp(ticks, xRotO, getXRot());
        float z = Mth.lerp(ticks, prevRoll, getRoll());

        transformT.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformT;
    }

    @Override
    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0, 1.703125f, 0.4375f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }

    public Matrix4f getGunTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0.577f, 4.616f, 0.560f);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformT.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, gunYRotO, getGunYRot()) - Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformT;
    }

    public Matrix4f getGunnerBarrelTransform(float ticks) {
        Matrix4f transformG = getGunTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0f, 0f, 0f);

        transformG.translate(worldPosition.x, worldPosition.y, worldPosition.z);

        float a = getTurretYaw(ticks);

        float r = (Mth.abs(a) - 90f) / 90f;

        float r2;

        if (Mth.abs(a) <= 90f) {
            r2 = a / 90f;
        } else {
            if (a < 0) {
                r2 = -(180f + a) / 90f;
            } else {
                r2 = (180f - a) / 90f;
            }
        }

        float x = Mth.lerp(ticks, gunXRotO, getGunXRot());
        float xV = Mth.lerp(ticks, xRotO, getXRot());
        float z = Mth.lerp(ticks, prevRoll, getRoll());

        transformG.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformG;
    }

    // @Override
    public float rotateYOffset() {
        return 3.5f;
    }

    protected void clampRotation(Entity entity) {
        Minecraft mc = Minecraft.getInstance();
        if (entity.level().isClientSide && entity == getFirstPassenger()) {
            float a = getTurretYaw(1);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = -(180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            float min = -11f - r * getXRot() - r2 * getRoll();
            float max = 4.6f - r * getXRot() - r2 * getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                float f2 = Mth.wrapDegrees(entity.getYRot() - this.getBarrelYRot(1));
                float f3 = Mth.clamp(f2, -20.0F, 20.0F);
                entity.yRotO += f3 - f2;
                entity.setYRot(entity.getYRot() + f3 - f2);
                entity.setYBodyRot(getBarrelYRot(1));
            }
        } else if (entity == getNthEntity(1)) {
            float a = getTurretYaw(1);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = -(180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            float min = -60f - r * getXRot() - r2 * getRoll();
            float max = 10f - r * getXRot() - r2 * getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                float f2 = Mth.wrapDegrees(entity.getYRot() - this.getGunYRot(1));
                float f3 = Mth.clamp(f2, -150.0F, 150.0F);
                entity.yRotO += f3 - f2;
                entity.setYRot(entity.getYRot() + f3 - f2);
                entity.setYBodyRot(entity.getYRot());
            }
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState cannonShootPredicate(AnimationState<M60Entity> event) {
        if (this.entityData.get(CANNON_RECOIL_TIME) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.yx100.fire"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.yx100.idle"));
    }

    private PlayState coaxShootPredicate(AnimationState<M60Entity> event) {
        if (this.entityData.get(FIRE_ANIM) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.yx100.fire_coax"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.yx100.idle_coax"));
    }

    private PlayState gunShootPredicate(AnimationState<M60Entity> event) {
        if (this.entityData.get(GUN_FIRE_TIME) > 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.yx100.fire2"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.yx100.idle2"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "cannon", 0, this::cannonShootPredicate));
        data.add(new AnimationController<>(this, "coax", 0, this::coaxShootPredicate));
        data.add(new AnimationController<>(this, "gun", 0, this::gunShootPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int mainGunRpm(Player player) {
        if (getWeaponIndex(0) == 0) {
            return 15;
        } else if (getWeaponIndex(0) == 1) {
            return 600;
        }
        return 300;
    }

    @Override
    public boolean canShoot(Player player) {
        if (getWeaponIndex(0) == 0) {
            return this.entityData.get(LOADED_AP) > 0 && !this.cannotFire;
        } else if (getWeaponIndex(0) == 1) {
            boolean hasCreativeAmmo = (this.getFirstPassenger() instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer));
            return (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) && !this.cannotFireCoax;
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        if (getWeaponIndex(0) == 0) {
            return this.entityData.get(LOADED_AP);
        } else {
            return this.entityData.get(AMMO);
        }
    }

    @Override
    public boolean banHand(Player player) {
        if (player == getNthEntity(0) || player == getNthEntity(1)) {
            return true;
        }
        return player == getNthEntity(2) && !player.isShiftKeyDown();
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return entity == getNthEntity(0) || entity == getNthEntity(1);
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public boolean hasTracks() {
        return true;
    }

    @Override
    public int getWeaponHeat(Player player) {
        if (getWeaponIndex(0) == 0) {
            return entityData.get(HEAT);
        } else if (getWeaponIndex(0) == 1) {
            return entityData.get(COAX_HEAT);
        }
        return 0;
    }

    @Override
    public void changeWeapon(int index, int value, boolean isScroll) {
        if (index != 0) return;

        var weapons = getAvailableWeapons(index);
        if (weapons.isEmpty()) return;

        var typeIndex = isScroll ? (value + getWeaponIndex(index) + weapons.size()) % weapons.size() : value;

        if (getWeaponIndex(0) == 0 && entityData.get(LOADED_AP) > 0 && typeIndex != 0) {
            if (this.getFirstPassenger() instanceof Player player && !InventoryTool.hasCreativeAmmoBox(player)) {
                this.insertItem(ModItems.AP_5_INCHES.get(), 1);
            }
            entityData.set(LOADED_AP, 0);
        }

        if (typeIndex == 0 && entityData.get(LOADED_AMMO_TYPE) != 0) {
            if (entityData.get(LOADED_AP) == 0) {
                this.reloadCoolDown = 80;
            }
        }

        entityData.set(LOADED_AMMO_TYPE, typeIndex);

        if (this.getFirstPassenger() instanceof ServerPlayer player) {
            var stopSoundPacket = new ClientboundStopSoundPacket(tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get().getLocation(), SoundSource.PLAYERS);
            player.connection.send(stopSoundPacket);
        }

        WeaponVehicleEntity.super.changeWeapon(index, value, isScroll);
    }

    public Vec3 getGunVec(float ticks) {
        return getGunnerVector(ticks);
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/m60_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {

        float minWH = (float) Math.min(screenWidth, screenHeight);
        float scaledMinWH = Mth.floor(minWH * scale);
        float centerW = ((screenWidth - scaledMinWH) / 2);
        float centerH = ((screenHeight - scaledMinWH) / 2);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // 准心
        if (this.getWeaponIndex(0) == 0) {
            preciseBlit(guiGraphics, Mod.loc("textures/screens/land/tank_cannon_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        } else if (this.getWeaponIndex(0) == 1) {
            preciseBlit(guiGraphics, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH);
        }

        if (this.getWeaponIndex(0) == 0) {
            double heat = 1 - this.entityData.get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("      M68    " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        } else {
            double heat = 1 - this.entityData.get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM M73 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderThirdPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);
        if (this.getWeaponIndex(0) == 0) {
            double heat = this.entityData.get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("M68 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
        } else {
            double heat2 = this.entityData.get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM M73 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        }
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        if (seatIndex == 0) {
            return zoom ? 0.14 : Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.18 : 0.30; // Уменьшены значения чувствительности с 0.17/0.22/0.35 до 0.14/0.18/0.30
        } else if (seatIndex == 1) {
            return zoom ? 0.25 : Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.35 : 0.4;
        } else return original;
    }

    @Override
    public boolean isEnclosed(int index) {
        return index != 2;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                return new Vec2((float) -getYRotFromVector(this.getBarrelVec(partialTicks)), (float) -getXRotFromVector(this.getBarrelVec(partialTicks)));
            } else if (this.getSeatIndex(player) == 1) {
                return new Vec2((float) -getYRotFromVector(this.getGunnerVector(partialTicks)), (float) -getXRotFromVector(this.getGunnerVector(partialTicks)));
            }
        }
        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                if (zoom) {
                    return new Vec3(this.driverZoomPos(partialTicks).x, this.driverZoomPos(partialTicks).y, this.driverZoomPos(partialTicks).z);
                } else {
                    return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
                }
            } else if (this.getSeatIndex(player) == 1) {
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    @Override
    public float getTurretMaxHealth() {
        return 100;
    }

    @Override
    public float getWheelMaxHealth() {
        return 100;
    }

    @Override
    public float getEngineMaxHealth() {
        return 150;
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obbTurret);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0.016f, 1.125f, 1.344f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.016f, 1.125f, -2.125f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.531f, 0.875f, 0.031f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.531f, 0.875f, 0.031f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT = getTurretTransform(1);

        Vector4f worldPositionT = transformPosition(transformT, 0, 0.735f, 0);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {  // Только на сервере
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 2;  // Количество типов (default=0, desert=1, forest=2)
                int newType = (currentType + 1) % maxTypes;  // Цикл: 0→1→2→0
                this.entityData.set(CAMOUFLAGE_TYPE, newType);  // Сохраняем новый тип

                // Опционально: Звук и эффект (например, частицы)
                this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.SPRAY.get(), this.getSoundSource(), 1.0F, 1.0F);  // Пример звука (замени на свой)
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1, this.getZ(), 10, 1.0, 1.0, 1.0, 0.1);  // Частицы успеха
                }

                return InteractionResult.CONSUME;  // Consume — прерываем, не даём войти
            } else {
                return InteractionResult.SUCCESS;  // Success на клиенте для отклика
            }
        }

        // Если ничего не подошло — базовая логика (вход/инвентарь)
        return super.interact(player, hand);
    }
}