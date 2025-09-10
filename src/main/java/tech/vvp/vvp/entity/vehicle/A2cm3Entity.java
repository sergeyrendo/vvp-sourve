package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.ArtilleryIndicator;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.Math;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;
import static com.atsuishio.superbwarfare.tools.RangeTool.calculateLaunchVector;

public class A2cm3Entity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> CANNON_FIRE_TIME = SynchedEntityData.defineId(A2cm3Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(A2cm3Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(A2cm3Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> LOADED_SHELL = SynchedEntityData.defineId(A2cm3Entity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> SELECTED_AMMO_TYPE = SynchedEntityData.defineId(A2cm3Entity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int reloadCoolDown;

    public OBB obb;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obbTurret;

    public A2cm3Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.A_2CM3.get(), world);
    }

    public A2cm3Entity(EntityType<A2cm3Entity> type, Level world) {
        super(type, world);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(1.312500f, 0.531250f, 3.468750f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(1.875000f, 0.281250f, 3.468750f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.375000f, 0.750000f, 4.125000f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.375000f, 0.750000f, 4.125000f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(1.312500f, 0.781250f, 0.531250f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(1.375000f, 0.656250f, 2.000000f), new Quaternionf(), OBB.Part.TURRET);
    }

    public static A2cm3Entity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<A2cm3Entity> castedEntityType = (EntityType<A2cm3Entity>) entityTypeFromPacket;
        A2cm3Entity entity = new A2cm3Entity(castedEntityType, world);
        return entity;
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfig.YX_100_AP_CANNON_DAMAGE.get())
                                .explosionRadius(VehicleConfig.YX_100_AP_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(VehicleConfig.YX_100_AP_CANNON_EXPLOSION_DAMAGE.get())
                                .fireProbability(0)
                                .fireTime(0)
                                .durability(100)
                                .velocity(40)
                                .gravity(0.1f)
                                .sound(ModSounds.INTO_MISSILE.get())
                                .ammo(ModItems.AP_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/ap_shell.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.ABRAMS_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.ABRAMS_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.ABRAMS_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.ABRAMS_VERYFAR.get())
                                .mainGun(true),
                        // HE
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfig.YX_100_HE_CANNON_DAMAGE.get())
                                .explosionRadius(VehicleConfig.YX_100_HE_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(VehicleConfig.YX_100_HE_CANNON_EXPLOSION_DAMAGE.get())
                                .fireProbability(0.18F)
                                .fireTime(2)
                                .durability(1)
                                .velocity(25)
                                .gravity(0.1f)
                                .sound(ModSounds.INTO_CANNON.get())
                                .ammo(ModItems.HE_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/he_shell.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.ABRAMS_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.ABRAMS_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.ABRAMS_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.ABRAMS_VERYFAR.get())
                                .mainGun(true),
                }
        };
    }


    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(3 + ClientMouseHandler.custom3pDistanceLerp, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CANNON_FIRE_TIME, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(MISSILE_COUNT, 0);
        this.entityData.define(LOADED_SHELL, "null");
        this.entityData.define(SELECTED_AMMO_TYPE, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
        compound.putString("LoadedShell", this.entityData.get(LOADED_SHELL));
        compound.putInt("SelectedAmmoType", this.entityData.get(SELECTED_AMMO_TYPE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
        this.entityData.set(LOADED_SHELL, compound.getString("LoadedShell"));
        this.entityData.set(SELECTED_AMMO_TYPE, compound.getInt("SelectedAmmoType"));
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.15), random.nextFloat() * 0.15f + 1.05f);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        updateOBB();
        if (getLeftTrack() < 0) {
            setLeftTrack(100);
        }

        if (getLeftTrack() > 100) {
            setLeftTrack(0);
        }

        if (getRightTrack() < 0) {
            setRightTrack(100);
        }

        if (getRightTrack() > 100) {
            setRightTrack(0);
        }

        if (reloadCoolDown == 80 && this.getFirstPassenger() instanceof Player player) {
            SoundTool.playLocalSound(player, tech.vvp.vvp.init.ModSounds.T90_AUTORELOAD.get());
        }

        if (this.level() instanceof ServerLevel) {
            boolean hasCreativeAmmo = false;
            for (int i = 0; i < getMaxPassengers(); i++) {
                if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }

            if (reloadCoolDown > 0 && getWeapon(0).mainGun && (hasCreativeAmmo || countItem(getWeapon(0).ammo) > 0)) {
                reloadCoolDown--;
            }


            this.handleAmmo();
        }

        turretAngle(10, 12.5f);
        this.terrainCompact(4f, 5f);
        inertiaRotate(1);

        releaseSmokeDecoy(getTurretVector(1));

        lowHealthWarning();
        this.refreshDimensions();
    }

    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.07 || Mth.abs(this.entityData.get(POWER)) > 0.12;
    }

    private void handleAmmo() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers(); i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        if (getWeapon(0).mainGun) {
            entityData.set(SELECTED_AMMO_TYPE, getWeaponIndex(0));
        }

        if (this.getEntityData().get(LOADED_SHELL).equals("null") && reloadCoolDown <= 0 && (hasCreativeAmmo || hasItem(getWeapon(0).ammo))) {
            this.entityData.set(LOADED_SHELL, String.valueOf(ForgeRegistries.ITEMS.getKey(getWeapon(0).ammo)));
            if (!hasCreativeAmmo) {
                consumeItem(getWeapon(0).ammo, 1);
            }
        }

    }

    @Override
    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        Matrix4f transform = getBarrelTransform(1);
        if (type == 0) {
            if (reloadCoolDown == 0 && getWeapon(0).mainGun) {

                Vector4f worldPosition = transformPosition(transform, 0, 0, 4.79455000f);

                var cannonShell = (CannonShellWeapon) getWeapon(0);
                var entityToSpawn = cannonShell.create(player);

                entityToSpawn.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
                entityToSpawn.shoot(getBarrelVector(1).x, getBarrelVector(1).y + 0.005f, getBarrelVector(1).z, cannonShell.velocity, 0.02f);
                level().addFreshEntity(entityToSpawn);

                if (!player.level().isClientSide) {
                    playShootSound3p(player, 0, 8, 16, 32);
                }

                this.entityData.set(CANNON_RECOIL_TIME, 40);
                this.entityData.set(LOADED_SHELL, "null");

                this.consumeEnergy(10000);
                this.entityData.set(YAW, getTurretYRot());

                reloadCoolDown = 80;

                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX() + 5 * getBarrelVector(1).x,
                            this.getY() + 0.1,
                            this.getZ() + 5 * getBarrelVector(1).z,
                            300, 6, 0.02, 6, 0.005);

                    double x = worldPosition.x + 9 * getBarrelVector(1).x;
                    double y = worldPosition.y + 9 * getBarrelVector(1).y;
                    double z = worldPosition.z + 9 * getBarrelVector(1).z;

                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
                    server.sendParticles(ParticleTypes.CLOUD, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);

                    int count = 6;

                    for (float i = 9.5f; i < 23; i += .5f) {
                        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                worldPosition.x + i * getBarrelVector(1).x,
                                worldPosition.y + i * getBarrelVector(1).y,
                                worldPosition.z + i * getBarrelVector(1).z,
                                Mth.clamp(count--, 1, 5), 0.15, 0.15, 0.15, 0.0025);
                    }

                    Vector4f worldPositionL = transformPosition(transform, -0.35f, 0, 0);
                    Vector4f worldPositionR = transformPosition(transform, 0.35f, 0, 0);

                    for (float i = 3f; i < 6; i += .5f) {
                        server.sendParticles(ParticleTypes.CLOUD,
                                worldPositionL.x + i * getBarrelVector(1).x,
                                worldPositionL.y + i * getBarrelVector(1).y,
                                worldPositionL.z + i * getBarrelVector(1).z,
                                1, 0.025, 0.025, 0.025, 0.0015);

                        server.sendParticles(ParticleTypes.CLOUD,
                                worldPositionR.x + i * getBarrelVector(1).x,
                                worldPositionR.y + i * getBarrelVector(1).y,
                                worldPositionR.z + i * getBarrelVector(1).z,
                                1, 0.025, 0.025, 0.025, 0.0015);
                    }
                }

                ShakeClientMessage.sendToNearbyPlayers(this, 8, 10, 8, 60);

            }
        }
    }

    @Override
    public void travel() {
        trackEngine(true, 0.052, VehicleConfig.BMP_2_ENERGY_COST.get(), 1.25, 0.5, 1.9, 0.8, 0.21f, -0.16f, 0.0024f, 0.0024f, 0.1f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.BMP_ENGINE.get();
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
        Matrix4f transformV = getVehicleTransform(1);

        int i = this.getSeatIndex(passenger);

        Vector4f worldPosition;
        if (i == 0) {
            worldPosition = transformPosition(transform, 0.36f, -0.25f, 0.56f);
        } else {
            worldPosition = transformPosition(transformV, 0, 1, 0);
        }
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYBodyRot(getBarrelYRot(1));
        }
    }

    public int getMaxPassengers() {
        return 7;
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getTurretTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 0, 0.75f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 getNewEyePos(float pPartialTicks) {
        Matrix4f transform = getTurretTransform(pPartialTicks);
        Vector4f worldPosition = transformPosition(transform, 0, 1.65f, 0.75f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, -0.00464375f, -0.02486875f, 0.89383125f);

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

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = getTurretTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    @Override
    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0.00630625f, 2.6064375f, -1.4252625f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }
    @Override
    public float rotateYOffset() {
        return 2.7f;
    }

    protected void clampRotation(Entity entity) {
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

        entity.setYBodyRot(getBarrelYRot(1));
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState firePredicate(AnimationState<A2cm3Entity> event) {
        if (this.entityData.get(FIRE_ANIM) > 1 && getWeaponIndex(0) == 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav.fire"));
        }

        if (this.entityData.get(FIRE_ANIM) > 0 && getWeaponIndex(0) == 1) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav.fire2"));
        }

        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::firePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int mainGunRpm(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                return 15;
            }
        }
        return 15;
    }

    @Override
    public boolean canShoot(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                return !this.entityData.get(LOADED_SHELL).equals("null") && !this.cannotFire;
            }
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        if (player == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                return this.entityData.get(LOADED_SHELL).equals("null") ? 0 : 1;
            }
        }
        return 0;
    }

    @Override
    public boolean banHand(Player player) {
        return true;
    }

    @Override
    public boolean hidePassenger(int index) {
        return true;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(Player player) {
        if (player == getNthEntity(0)) {
            return entityData.get(COAX_HEAT);
        }
        return 0;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/bmp2_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderFirstPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);

        if (this.getWeaponIndex(0) == 0) {
            double heat = 1 - this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal(" 30MM 2A42 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat = 1 - this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal(" 7.62MM ПКТ " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        } else {
            guiGraphics.drawString(font, Component.literal("    9M113  " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), screenWidth / 2 - 33, screenHeight - 65, 0x66FF00, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            double heat = this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("30MM 2A42 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM ПКТ " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        } else {
            guiGraphics.drawString(font, Component.literal("9M113 " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), 30, -9, -1, false);
        }
    }

    @Override
    public boolean hasTracks() {
        return true;
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.22 : Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.27 : 0.36;
    }

    @Override
    public boolean isEnclosed(int index) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                return new Vec2((float) -getYRotFromVector(this.getBarrelVec(partialTicks)), (float) -getXRotFromVector(this.getBarrelVec(partialTicks)));
            } else {
                return new Vec2(Mth.lerp(partialTicks, player.yHeadRotO, player.getYHeadRot()), Mth.lerp(partialTicks, player.xRotO, player.getXRot()));
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
                    return new Vec3(this.driverZoomPos(partialTicks).x, Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), this.driverZoomPos(partialTicks).z);
                } else {
                    return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
                }
            } else {
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()) - 6 * player.getViewVector(partialTicks).x,
                        Mth.lerp(partialTicks, player.yo + player.getEyeHeight() + 1, player.getEyeY() + 1) - 6 * player.getViewVector(partialTicks).y,
                        Mth.lerp(partialTicks, player.zo, player.getZ()) - 6 * player.getViewVector(partialTicks).z);
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obbTurret);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0.000000f, 0.968750f, 0.156250f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.000000f, 1.718750f, -0.156250f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.687500f, 0.750000f, -0.500000f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.687500f, 0.750000f, -0.500000f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, 0.000000f, 1.218750f, -3.843750f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT = getTurretTransform(1);

        Vector4f worldPositionT = transformPosition(transformT, 0.000000f, 0.0498125f, -0.5f);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));
    }
}
