package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
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
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
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

public class StrykerEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<String> LOADED_SHELL = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> SELECTED_AMMO_TYPE = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUN_FIRE_TIME = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_DRONE = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> HAS_MANGAL = SynchedEntityData.defineId(StrykerEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int droneReloadCoolDown;

    public OBB obb, obb1, obb2, obb3, obb4, obb5, obb6, obb7, obb8, obb9, obb10, obb11, obb12, obbTurret;

    public StrykerEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.STRYKER.get(), world);
    }

    public StrykerEntity(EntityType<StrykerEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb9 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb10 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb11 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb12 = new OBB(this.position().toVector3f(), new Vector3f(0.2f, 0.45f, 0.45f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(1.625f, 0.90625f, 2.4375f), new Quaternionf(), OBB.Part.BODY);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(1.625f, 0.53125f, 0.34375f), new Quaternionf(), OBB.Part.BODY);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(1.625f, 0.625f, 1.5f), new Quaternionf(), OBB.Part.BODY);
        this.obb8 = new OBB(this.position().toVector3f(), new Vector3f(0.71875f, 0.46875f, 0.875f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(0.711f, 0.453f, 1.789f), new Quaternionf(), OBB.Part.TURRET);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static StrykerEntity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        EntityType<StrykerEntity> castedEntityType = (EntityType<StrykerEntity>) entityTypeFromPacket;
        return new StrykerEntity(castedEntityType, world);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        // AP
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfigVVP.STRYKER_M1128_AP_CANNON_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.STRYKER_M1128_AP_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(VehicleConfigVVP.STRYKER_M1128_AP_CANNON_EXPLOSION_DAMAGE.get())
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
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.M1128_VERYFAR.get())
                                .mainGun(true),
                        // HE
                        new CannonShellWeapon()
                                .hitDamage(VehicleConfigVVP.STRYKER_M1128_HE_CANNON_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.STRYKER_M1128_HE_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .explosionDamage(VehicleConfigVVP.STRYKER_M1128_HE_CANNON_EXPLOSION_DAMAGE.get())
                                .fireProbability(0.18F)
                                .fireTime(2)
                                .durability(1)
                                .velocity(25)
                                .gravity(0.1f)
                                .sound(ModSounds.INTO_CANNON.get())
                                .ammo(ModItems.HE_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/he_shell.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.M1128_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.M1128_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.M1128_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.M1128_VERYFAR.get())
                                .mainGun(true),
                        // 同轴重机枪
                        new ProjectileWeapon()
                                .damage(VehicleConfig.HEAVY_MACHINE_GUN_DAMAGE.get())
                                .headShot(2)
                                .zoom(false)
                                .bypassArmorRate(0.4f)
                                .ammo(ModItems.HEAVY_AMMO.get())
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_12_7mm.png"))
                                .sound1p(ModSounds.M_2_HB_FIRE_1P.get())
                                .sound3p(ModSounds.M_2_HB_FIRE_3P.get())
                                .sound3pFar(ModSounds.M_2_HB_FAR.get())
                                .sound3pVeryFar(ModSounds.M_2_HB_VERYFAR.get()),
                },
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
        this.entityData.define(LOADED_SHELL, "null");
        this.entityData.define(LOADED_DRONE, 0);
        this.entityData.define(SELECTED_AMMO_TYPE, 0);
        this.entityData.define(GUN_FIRE_TIME, 0);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(HAS_MANGAL, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("LoadedShell", this.entityData.get(LOADED_SHELL));
        compound.putInt("LoadedDrone", this.entityData.get(LOADED_DRONE));
        compound.putInt("SelectedAmmoType", this.entityData.get(SELECTED_AMMO_TYPE));
        compound.putInt("WeaponType", getWeaponIndex(0));
        compound.putInt("PassengerWeaponType", getWeaponIndex(1));
        compound.putInt("ThirdPassengerWeaponType", getWeaponIndex(2));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putBoolean("has_mangal", this.entityData.get(HAS_MANGAL));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_SHELL, compound.getString("LoadedShell"));
        this.entityData.set(LOADED_DRONE, compound.getInt("LoadedDrone"));
        this.entityData.set(SELECTED_AMMO_TYPE, compound.getInt("SelectedAmmoType"));
        setWeaponIndex(0, compound.getInt("WeaponType"));
        setWeaponIndex(1, compound.getInt("PassengerWeaponType"));
        setWeaponIndex(2, compound.getInt("ThirdPassengerWeaponType"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(HAS_MANGAL, compound.getBoolean("has_mangal"));
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.3f) * damage);
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.15), random.nextFloat() * 0.15f + 1.05f);
    }


    @Override
    public void baseTick() {
        super.baseTick();
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

        if (reloadCoolDown == 70 && this.getFirstPassenger() instanceof Player player) {
            SoundTool.playLocalSound(player, tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get());
        }

        if (this.level() instanceof ServerLevel) {
            boolean hasCreativeAmmo = false;
            for (int i = 0; i < getMaxPassengers(); i++) {
                if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {
                    hasCreativeAmmo = true;
                }
            }

            if (reloadCoolDown > 0 && getWeapon(0).mainGun && (hasCreativeAmmo || countItem(getWeapon(0).ammo) > 0)) {
                reloadCoolDown--;
            }

            this.handleAmmo();
        }

        if (getNthEntity(2) instanceof Mob mob && canShoot(mob) && mob.getTarget() != null) {
            int rpm = 20 / (mainGunRpm(mob) / 60);
            if (tickCount %rpm == 0) {
                vehicleShoot(mob, 2);
            }
        }

        lowHealthWarning();
        terrainCompact(4.375f, 6.3125f);
        inertiaRotate(1.2f);
        releaseSmokeDecoy(getTurretVector(1));

        this.refreshDimensions();
    }


    @Override
    public float turretYSpeed() {
        return 5;
    }

    @Override
    public float turretXSpeed() {
        return 5F;
    }

    @Override
    public float turretMinPitch() {
        return -10f;
    }
    // 炮塔最大仰角
    @Override
    public float turretMaxPitch() {
        return 30f;
    }

    @Override
    public Vec3 getTurretShootPos(Entity entity, float ticks) {
        Matrix4f transform = getBarrelTransform(1);
        Vector4f worldPosition;
        if (getWeapon(0).mainGun) {
            worldPosition = transformPosition(transform, 0.08279375f, 0.55f, 4.0710375f);
        } else {
            worldPosition = transformPosition(transform, -0.75f, -1.05f, 1.4f);
        }
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }


    @Override
    public float projectileVelocity(Entity entity) {
        if (entity == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                var cannonShell = (CannonShellWeapon) getWeapon(0);
                return cannonShell.velocity;
            } else {
                return 20f;
            }
        } else {
            return 20f;
        }
    }

    @Override
    public float projectileGravity(Entity entity) {
        if (getWeapon(0).mainGun && entity == getNthEntity(0)) {
            var cannonShell = (CannonShellWeapon) getWeapon(0);
            return cannonShell.gravity;
        } else {
            return 0.05f;
        }
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

        if (this.getEntityData().get(LOADED_SHELL).equals("null") && reloadCoolDown <= 0 && (hasCreativeAmmo || hasItem(getWeapon(0).ammo))) {
            this.entityData.set(LOADED_SHELL, String.valueOf(ForgeRegistries.ITEMS.getKey(getWeapon(0).ammo)));
            if (!hasCreativeAmmo) {
                consumeItem(getWeapon(0).ammo, 1);
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
    public void vehicleShoot(LivingEntity living, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {
                hasCreativeAmmo = true;
            }
        }

        if (type == 0) {
            if (reloadCoolDown == 0 && getWeapon(0).mainGun) {

                var cannonShell = (CannonShellWeapon) getWeapon(0);
                var entityToSpawn = cannonShell.create(living);

                entityToSpawn.setPos(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z);
                entityToSpawn.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, cannonShell.velocity, 0.02f);
                level().addFreshEntity(entityToSpawn);

                playShootSound3p(living, 0, 8, 16, 32, new Vec3(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z));

                this.entityData.set(CANNON_RECOIL_TIME, 40);
                this.entityData.set(LOADED_SHELL, "null");

                this.entityData.set(YAW, getTurretYRot());

                reloadCoolDown = 80;

                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            this.getX() + 5 * getBarrelVector(1).x,
                            this.getY() + 0.1,
                            this.getZ() + 5 * getBarrelVector(1).z,
                            300, 6, 0.02, 6, 0.005);

                    double x = getTurretShootPos(living, 1).x + 9 * getBarrelVector(1).x;
                    double y = getTurretShootPos(living, 1).y + 9 * getBarrelVector(1).y;
                    double z = getTurretShootPos(living, 1).z + 9 * getBarrelVector(1).z;

                    server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
                    server.sendParticles(ParticleTypes.CLOUD, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);

                    int count = 6;

                    for (float i = 9.5f; i < 23; i += .5f) {
                        server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                getTurretShootPos(living, 1).x + i * getBarrelVector(1).x,
                                getTurretShootPos(living, 1).y + i * getBarrelVector(1).y,
                                getTurretShootPos(living, 1).z + i * getBarrelVector(1).z,
                                Mth.clamp(count--, 1, 5), 0.15, 0.15, 0.15, 0.0025);
                    }

                    Matrix4f transform = getBarrelTransform(1);
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
            } else if (getWeaponIndex(0) == 2) {
                if (this.cannotFireCoax) return;

                if (this.entityData.get(MG_AMMO) > 0 || hasCreativeAmmo) {
                    var projectileRight = ((ProjectileWeapon) getWeapon(0)).create(living).setGunItemId(this.getType().getDescriptionId() + ".1");

                    projectileRight.setPos(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z);
                    projectileRight.shoot(living, getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, 20,
                            0.25f);
                    this.level().addFreshEntity(projectileRight);

                    this.entityData.set(COAX_HEAT, this.entityData.get(COAX_HEAT) + 4);
                    this.entityData.set(FIRE_ANIM, 2);

                    playShootSound3p(living, 0, 4, 12, 24, new Vec3(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z));

                    if (!hasCreativeAmmo) {
                        ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                            if (stack.is(ModItems.AMMO_BOX.get())) {
                                return Ammo.HEAVY.get(stack) > 0;
                            }
                            return false;
                        }).findFirst().orElse(ItemStack.EMPTY);

                        if (!ammoBox.isEmpty()) {
                            Ammo.HEAVY.add(ammoBox, -1);
                        } else {
                            this.getItemStacks().stream().filter(stack -> stack.is(ModItems.HEAVY_AMMO.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void travel() {
        wheelEngine(false, 0.052, VehicleConfigVVP.STRYKER_M1296_ENERGY_COST.get(), 1.25, 1.5, 0.18f, -0.13f, 0.0020f, 0.0019f, 0.1f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return tech.vvp.vvp.init.ModSounds.STRYKER_ENGINE.get();
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
            worldPosition = transformPosition(transform, 0.0f, -0.1f, 0.0f);
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

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getTurretTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 1f, 0.6076875f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public int getMaxPassengers() {
        return 5;
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


    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, -0.07961875f, -0.38081875f, 0.970775f);

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
        Vector4f worldPosition = transformPosition(transform, -0.1238125f, 2.51569375f, -0.82226875f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }


    @Override
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

            float min = -turretMaxPitch() - r * getXRot() - r2 * getRoll();
            float max = -turretMinPitch() - r * getXRot() - r2 * getRoll();

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

            float min = -passengerWeaponMaxPitch() - r * getXRot() - r2 * getRoll();
            float max = -passengerWeaponMinPitch() - r * getXRot() - r2 * getRoll();

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
        } else if (entity == getNthEntity(2)) {
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

            float min = -90f - r * getXRot() - r2 * getRoll();
            float max = 22.5f - r * getXRot() - r2 * getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    public int passengerSeatLocation(Entity entity) {
        return 1;
    }

    private PlayState firePredicate(AnimationState<StrykerEntity> event) {
        return PlayState.STOP;
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
    public int mainGunRpm(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                return 15;
            } else if (getWeaponIndex(0) == 2) {
                return 500;
            }
        }


        return 15;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                return !this.entityData.get(LOADED_SHELL).equals("null") && getEnergy() > VehicleConfig.YX_100_SHOOT_COST.get();
            } else if (getWeaponIndex(0) == 2) {
                return (this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(living)) && !cannotFireCoax;
            }
        }
        return false;
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeapon(0).mainGun) {
                return this.entityData.get(LOADED_SHELL).equals("null") ? 0 : 1;
            } else if (getWeaponIndex(0) == 2) {
                return this.entityData.get(MG_AMMO);
            }
        }
        return 0;
    }

    @Override
    public boolean banHand(LivingEntity entity) {
        if (entity == getNthEntity(0) || entity == getNthEntity(1)) {
            return true;
        }
        return entity == getNthEntity(2) && !entity.isShiftKeyDown();
    }

    @Override
    public boolean hidePassenger(int index) {
        return index == 0 || index == 1;
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
    public int getWeaponHeat(LivingEntity living) {
        if (living == getNthEntity(0)) {
            return entityData.get(COAX_HEAT);
        }

        if (living == getNthEntity(1)) {
            return entityData.get(HEAT);
        }

        return 0;
    }

    @Override
    public void changeWeapon(int index, int value, boolean isScroll) {
        if (index != 0) return;

        var weapons = getAvailableWeapons(index);
        if (weapons.isEmpty()) return;
        var count = weapons.size();

        var typeIndex = isScroll ? (value + getWeaponIndex(index) + count) % count : value;

        if (typeIndex == 0 || typeIndex == 1 || typeIndex == 2 || typeIndex == 3) {
            boolean hasCreativeAmmo = false;
            for (int i = 0; i < getMaxPassengers(); i++) {
                if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                }
            }


            if (typeIndex != entityData.get(SELECTED_AMMO_TYPE)) {
                this.reloadCoolDown = 80;
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entityData.get(LOADED_SHELL)));
                if (item == null) {
                    return;
                }
                if (!this.entityData.get(LOADED_SHELL).equals("null") && !hasCreativeAmmo) {
                    this.insertItem(new ItemStack(item).getItem(), 1);
                }
                entityData.set(LOADED_SHELL, "null");
            }

            if (this.getFirstPassenger() instanceof ServerPlayer player) {
                var clientboundstopsoundpacket = new ClientboundStopSoundPacket(tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get().getLocation(), SoundSource.PLAYERS);
                player.connection.send(clientboundstopsoundpacket);
            }
        }

        WeaponVehicleEntity.super.changeWeapon(index, value, isScroll);
    }

    public Vec3 getGunVec(float ticks) {
        return getGunnerVector(ticks);
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/stryker_icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, PoseStack poseStack, Font font, Player player, int screenWidth, int screenHeight, float scale, int color) {
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
        if (this.getWeapon(0).mainGun) {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/tank_cannon_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
        } else  {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
        }

        // 武器名称
        if (this.getWeaponIndex(0) == 0) {
            guiGraphics.drawString(font, Component.literal("M68A1E4 AP  " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), screenWidth / 2 - 33, screenHeight - 65, color, false);
        } else if (this.getWeaponIndex(0) == 1) {
            guiGraphics.drawString(font, Component.literal("M68A1E4 HE  " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), screenWidth / 2 - 33, screenHeight - 65, color, false);
        } else if (this.getWeaponIndex(0) == 2) {
            int heat = this.getEntityData().get(COAX_HEAT);
            guiGraphics.drawString(font, Component.literal(" 7.62MM M240 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            guiGraphics.drawString(font, Component.literal("M68A1E4 AP " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), 30, -9, -1, false);
        } else if (this.getWeaponIndex(0) == 1) {
            guiGraphics.drawString(font, Component.literal("M68A1E4 HE " + this.getAmmoCount(player) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(AMMO))), 30, -9, -1, false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM M240 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        }
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        if (seatIndex == 0) {
            return zoom ? 0.17 : Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.22 : 0.35;
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
        return VVP.loc("textures/gui/vehicle/type/land.png");
    }

    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7, this.obb8, this.obb9, this.obb10, this.obb11, this.obb12, this.obbTurret);
    }

    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);
        Vector4f worldPosition = transformPosition(transform, -1.37f, 0.50f, 2.58f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition2 = transformPosition(transform, 1.37f, 0.50f, 2.58f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition9 = transformPosition(transform, 1.37f, 0.50f, 1.20f);
        this.obb9.center().set(new Vector3f(worldPosition9.x, worldPosition9.y, worldPosition9.z));
        this.obb9.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition10 = transformPosition(transform, 1.37f, 0.50f, -0.45f);
        this.obb10.center().set(new Vector3f(worldPosition10.x, worldPosition10.y, worldPosition10.z));
        this.obb10.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition11 = transformPosition(transform, -1.37f, 0.50f, 1.20f);
        this.obb11.center().set(new Vector3f(worldPosition11.x, worldPosition11.y, worldPosition11.z));
        this.obb11.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition12 = transformPosition(transform, -1.37f, 0.50f, -0.45f);
        this.obb12.center().set(new Vector3f(worldPosition12.x, worldPosition12.y, worldPosition12.z));
        this.obb12.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition3 = transformPosition(transform, 1.37f, 0.50f, -2.05f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition4 = transformPosition(transform, -1.37f, 0.50f, -2.05f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition5 = transformPosition(transform, 0, 1.53125f, -0.4375f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition6 = transformPosition(transform, 0, 1.90625f, -3.21875f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition7 = transformPosition(transform, 0, 1.8f, 2.53125f);
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(1, this));
        Vector4f worldPosition8 = transformPosition(transform, 0.65625f, 2.03125f, -2.0625f);
        this.obb8.center().set(new Vector3f(worldPosition8.x, worldPosition8.y, worldPosition8.z));
        this.obb8.setRotation(VectorTool.combineRotations(1, this));
        Matrix4f transformT = getTurretTransform(1);
        Vector4f worldPositionT = transformPosition(transformT, 0, 0.5f, 0f);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        // Загрузка модулей (отдельные if для каждого, как раньше)
        if (stack.is(tech.vvp.vvp.init.ModItems.MANGAL_BODY.get()) && !this.entityData.get(HAS_MANGAL)) {
            return loadModule(player, stack, HAS_MANGAL, tech.vvp.vvp.init.ModItems.MANGAL_BODY.get());  // Универсальная функция (см. ниже)
        }

        // Универсальное удаление с ключом (один if для всех флагов)
        if (stack.is(tech.vvp.vvp.init.ModItems.WRENCH.get())) {
            // Проверяем флаги по порядку (можно сделать цикл для всех)
            if (this.entityData.get(HAS_MANGAL)) {
                return removeModule(player, HAS_MANGAL, tech.vvp.vvp.init.ModItems.MANGAL_BODY.get());
            }
        }

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

        return super.interact(player, hand);
    }

    // Новая функция для загрузки (чтобы избежать дубликатов)
    private InteractionResult loadModule(Player player, ItemStack stack, EntityDataAccessor<Boolean> flag, Item returnItem) {
        if (!this.level().isClientSide) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.entityData.set(flag, true);
            this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.REMONT.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    // Новая функция для удаления (чтобы избежать дубликатов)
    private InteractionResult removeModule(Player player, EntityDataAccessor<Boolean> flag, Item returnItem) {
        if (!this.level().isClientSide) {
            this.entityData.set(flag, false);
            ItemStack returnedItem = new ItemStack(returnItem, 1);
            boolean addedToInventory = player.getInventory().add(returnedItem);
            if (!addedToInventory) {
                ItemEntity droppedItem = new ItemEntity(this.level(), this.getX(), this.getY() + 1, this.getZ(), returnedItem);
                this.level().addFreshEntity(droppedItem);
            }
            this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.REMONT.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public int getHudColor() {
        return 0xFFFFFF;
    }

    @Override
    public boolean hasPassengerTurretWeapon() {
        return true;
    }

    @Override
    public float getTurretMaxHealth() {
        return 135;
    }

    @Override
    public float getWheelMaxHealth() {
        return 50;
    }

    @Override
    public float getEngineMaxHealth() {
        return 140;
    }
}