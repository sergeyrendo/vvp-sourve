package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.WgMissileWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
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
import tech.vvp.vvp.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class Btr4Entity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> CANNON_FIRE_TIME = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CURRENT_MISSILE = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_FIRE_COOLDOWN = SynchedEntityData.defineId(Btr4Entity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int reloadCoolDown;

    public OBB obb;
    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;
    public OBB obb7;
    public OBB obb8;
    public OBB obb9;
    public OBB obbTurret;

    public Btr4Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.BTR_4.get(), world);
    }

    public Btr4Entity(EntityType<Btr4Entity> type, Level world) {
        super(type, world);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(0.25781f, 0.60156f, 0.59375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        // кабина
        this.obb8 = new OBB(this.position().toVector3f(), new Vector3f(1.60938f, 0.65625f, 1.35156f), new Quaternionf(), OBB.Part.BODY);
        // корпус
        this.obb9 = new OBB(this.position().toVector3f(), new Vector3f(1.78125f, 0.60938f, 3.28125f), new Quaternionf(), OBB.Part.BODY);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(0.8984375f, 0.53125f, 0.90625f), new Quaternionf(), OBB.Part.TURRET);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .damage(VehicleConfig.BMP_2_CANNON_DAMAGE.get())
                                .explosionDamage(VehicleConfig.BMP_2_CANNON_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfig.BMP_2_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png"))
                                .sound1p(ModSounds.BMP_CANNON_FIRE_1P.get())
                                .sound3p(ModSounds.BMP_CANNON_FIRE_3P.get())
                                .sound3pFar(ModSounds.LAV_CANNON_FAR.get())
                                .sound3pVeryFar(ModSounds.LAV_CANNON_VERYFAR.get()),
                        new ProjectileWeapon()
                                .damage(9.5f)
                                .headShot(2)
                                .zoom(false)
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_7_62mm.png"))
                                .sound1p(ModSounds.COAX_FIRE_1P.get())
                                .sound3p(ModSounds.M_60_FIRE_3P.get())
                                .sound3pFar(ModSounds.M_60_FAR.get())
                                .sound3pVeryFar(ModSounds.M_60_VERYFAR.get()),
                        new WgMissileWeapon()
                                .damage(ExplosionConfig.WIRE_GUIDE_MISSILE_DAMAGE.get())
                                .explosionDamage(ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE.get())
                                .explosionRadius(ExplosionConfig.WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS.get())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(ModSounds.BMP_MISSILE_FIRE_1P.get())
                                .sound3p(ModSounds.BMP_MISSILE_FIRE_3P.get()),
                },
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
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(CURRENT_MISSILE, 0);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(MISSILE_FIRE_COOLDOWN, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putInt("MissileFireCooldown", this.entityData.get(MISSILE_FIRE_COOLDOWN)); // <-- ДОБАВЬ ЭТУ СТРОКУ
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(MISSILE_FIRE_COOLDOWN, compound.getInt("MissileFireCooldown")); // <-- ДОБАВЬ ЭТУ СТРОКУ
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

        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            this.handleAmmo();
        }

        this.terrainCompact(4f, 5f);
        inertiaRotate(1);
        releaseSmokeDecoy(getTurretVector(1));
        lowHealthWarning();

        for (int i = 1; i < 7; i++) {
            if (getNthEntity(i) instanceof Mob mob && canShoot(mob) && mob.getTarget() != null) {
                int rpm = 20 / (mainGunRpm(mob) / 60);
                if (tickCount %rpm == 0) {
                    vehicleShoot(mob, i);
                }
            }
        }

        this.refreshDimensions();
    }

    // 炮塔最大水平旋转速度
    @Override
    public float turretYSpeed() {
        return 10;
    }
    // 炮塔最大俯仰旋转速度
    @Override
    public float turretXSpeed() {
        return 12.5F;
    }
    // 炮塔最小俯角
    @Override
    public float turretMinPitch() {
        return -7.5f;
    }
    // 炮塔最大仰角
    @Override
    public float turretMaxPitch() {
        return 74;
    }
    // 炮弹发射位置
    @Override
    public Vec3 getTurretShootPos(Entity entity, float ticks) {
        Matrix4f transform = getBarrelTransform(1);
        Vector4f worldPosition;
        if (getWeaponIndex(0) == 0) {
            worldPosition = transformPosition(transform, 0.26856250f, 0.4f, 2.11611250f);
        } else if (getWeaponIndex(0) == 1) {
            worldPosition = transformPosition(transform, -0.56543750f, 0.4f, 0.00888750f);
        } else  {
            worldPosition = transformPosition(transform, 0, 1, 0);
        }
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }
    // 炮弹发射速度
    @Override
    public float projectileVelocity(Entity entity) {
        if (getWeaponIndex(0) == 0) {
            return 20;
        } else if (getWeaponIndex(0) == 1) {
            return 25;
        } else  {
            return 2;
        }
    }
    // 炮弹重力
    @Override
    public float projectileGravity(Entity entity) {
        if (getWeaponIndex(0) == 0) {
            return 0.03f;
        } else if (getWeaponIndex(0) == 1) {
            return 0.05f;
        } else  {
            return 0;
        }
    }

    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.07 || Mth.abs(this.entityData.get(POWER)) > 0.12;
    }

    private void handleAmmo() {

        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers(); i++) {
            if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {
                hasCreativeAmmo = true;
            }
        }

        int mgAmmoCount = this.getItemStacks().stream().filter(stack -> {
            if (stack.is(ModItems.AMMO_BOX.get())) {
                return Ammo.RIFLE.get(stack) > 0;
            }
            return false;
        }).mapToInt(Ammo.RIFLE::get).sum() + countItem(ModItems.RIFLE_AMMO.get());

        if ((hasItem(ModItems.WIRE_GUIDE_MISSILE.get()) || hasCreativeAmmo)
                && this.reloadCoolDown <= 0 && this.getEntityData().get(LOADED_MISSILE) < 1) {
            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) + 1);
            this.reloadCoolDown = 160;
            if (!hasCreativeAmmo) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.WIRE_GUIDE_MISSILE.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BMP_MISSILE_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if (getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, countItem(ModItems.SMALL_SHELL.get()));
        } else if (getWeaponIndex(0) == 2) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_MISSILE));
        }

        this.entityData.set(MG_AMMO, mgAmmoCount);
        this.entityData.set(MISSILE_COUNT, countItem(ModItems.WIRE_GUIDE_MISSILE.get()));
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
            if (getWeaponIndex(0) == 0) {
                if (this.cannotFire) return;
                var smallCannonShell = ((SmallCannonShellWeapon) getWeapon(0)).create(living);

                smallCannonShell.setPos(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z);
                smallCannonShell.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, projectileVelocity(living),
                        0.25f);
                this.level().addFreshEntity(smallCannonShell);

                sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z, 1, 0.02, 0.02, 0.02, 0, false);
                playShootSound3p(living, 0, 4, 12, 24, getTurretShootPos(living, 1));
                ShakeClientMessage.sendToNearbyPlayers(this, 5, 6, 5, 9);

                this.entityData.set(CANNON_RECOIL_TIME, 40);
                this.entityData.set(YAW, getTurretYRot());

                this.entityData.set(HEAT, this.entityData.get(HEAT) + 7);
                this.entityData.set(FIRE_ANIM, 3);

                if (hasCreativeAmmo) return;

                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            } else if (getWeaponIndex(0) == 1) {
                if (this.cannotFireCoax) return;

                if (this.entityData.get(MG_AMMO) > 0 || hasCreativeAmmo) {
                    var projectileRight = ((ProjectileWeapon) getWeapon(0)).create(living).setGunItemId(this.getType().getDescriptionId());

                    projectileRight.bypassArmorRate(0.2f);
                    projectileRight.setPos(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z);
                    projectileRight.shoot(living, getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z,  projectileVelocity(living),
                            0.25f);
                    this.level().addFreshEntity(projectileRight);

                    if (!hasCreativeAmmo) {
                        ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                            if (stack.is(ModItems.AMMO_BOX.get())) {
                                return Ammo.RIFLE.get(stack) > 0;
                            }
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
                playShootSound3p(living, 0, 3, 6, 12, getTurretShootPos(living, 1));

            } else if (getWeaponIndex(0) == 2 && this.getEntityData().get(LOADED_MISSILE) > 0) {
                var wgMissileEntity = ((WgMissileWeapon) getWeapon(0)).create(living);

                wgMissileEntity.setPos(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z);
                wgMissileEntity.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z,  projectileVelocity(living), 0f);
                living.level().addFreshEntity(wgMissileEntity);
                playShootSound3p(living, 0, 6, 0, 0, getTurretShootPos(living, 1));

                this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);
                reloadCoolDown = 160;
            }
        }

        for (int i = 1; i < 7; i++) {
            if (type == i) {
                Vec3 shootPosition = passengerCameraPos(1, living);

                if (this.entityData.get(MG_AMMO) > 0 || hasCreativeAmmo) {
                    var projectile = ((ProjectileWeapon) getWeapon(i)).create(living).setGunItemId(this.getType().getDescriptionId());

                    Vec3 shootVec = new Vec3(living.getLookAngle().x, living.getLookAngle().y, living.getLookAngle().z);
                    float spread = 0.25f;

                    if (living instanceof Mob mob && mob.getTarget() != null) {
                        Entity target = mob.getTarget();
                        if (target.getVehicle() != null) {
                            target = target.getVehicle();
                        }

                        Vec3 targetVel = target.getDeltaMovement();

                        if (target instanceof LivingEntity pLiving) {
                            double gravity = pLiving.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
                            targetVel = targetVel.add(0, gravity, 0);
                        }

                        if (target instanceof Player) {
                            targetVel = targetVel.multiply(2, 1, 2);
                        }

                        shootVec = RangeTool.calculateFiringSolution(shootPosition, target.getBoundingBox().getCenter(), targetVel, 18, 0.05);
                        spread = 1.2f;

                        double angle = VectorTool.calculateAngle(shootVec, getPassengerVec(living, i));
                        if (angle > 50) return;
                    }

                    projectile.bypassArmorRate(0.2f);
                    projectile.setPos(shootPosition.x, shootPosition.y - 0.05, shootPosition.z);
                    projectile.shoot(living, shootVec.x, shootVec.y, shootVec.z, 18, spread);
                    this.level().addFreshEntity(projectile);

                    playShootSound3p(living, i, 3, 6, 12, shootPosition);

                    if (!hasCreativeAmmo) {
                        ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                            if (stack.is(ModItems.AMMO_BOX.get())) {
                                return Ammo.RIFLE.get(stack) > 0;
                            }
                            return false;
                        }).findFirst().orElse(ItemStack.EMPTY);

                        if (!ammoBox.isEmpty()) {
                            Ammo.RIFLE.add(ammoBox, -1);
                        } else {
                            this.getItemStacks().stream().filter(stack -> stack.is(ModItems.RIFLE_AMMO.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void travel() {
        wheelEngine(true, 0.052, VehicleConfigVVP.WHEEL_ENERGY_COST.get(), 1.25, 1.5, 0.18f, -0.13f, 0.0024f, 0.0024f, 0.1f);
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
        } else if (i == 1) {
            worldPosition = transformPosition(transformV, 0.5f, 0f, -0.8125f);
        } else if (i == 2) {
            worldPosition = transformPosition(transformV, -0.5f, 0f, -0.8125f);
        } else if (i == 3) {
            worldPosition = transformPosition(transformV, 0.5f, 0f, -2.1875f);
        } else if (i == 4) {
            worldPosition = transformPosition(transformV, -0.5f, 0f, -2.1875f);
        } else if (i == 5) {
            worldPosition = transformPosition(transformV, 0.5f, 0f, -3.0625f);
        } else if (i == 6) {
            worldPosition = transformPosition(transformV, -0.5f, 0f, -3.0625f);
        }else {
            worldPosition = transformPosition(transformV, 0, 1, 0);
        }
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYBodyRot(getBarrelYRot(1));
        } else if (entity == getNthEntity(1) || entity == getNthEntity(3)) {
            float f = Mth.wrapDegrees(entity.getYRot() - getYRot());
            float g = Mth.clamp(f, -140.0f, -40.0f);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(getYRot() - 90);
        } else if (entity == getNthEntity(2) || entity == getNthEntity(4) || entity == getNthEntity(6)) {
            float f = Mth.wrapDegrees(entity.getYRot() - getYRot());
            float g = Mth.clamp(f, 40.0f, 140.0f);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(getYRot() + 90);
        } else if (entity == getNthEntity(5)) {
            float f = Mth.wrapDegrees(entity.getYRot() - getYRot() - 180);
            float g = Mth.clamp(f, -50.0f, 50.0f);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(getYRot() - 180);
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
        Vector4f worldPosition = transformPosition(transform, -0.26856250f, -0.87243125f, -0.36611250f);

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
        Vector4f worldPosition = transformPosition(transform, 0.02354375f, 2.55829375f, -1.30387500f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }
    @Override
    public float rotateYOffset() {
        return 2.7f;
    }

    protected void clampRotation(Entity entity) {
        if (entity == getNthEntity(0)) {
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
            entity.setYBodyRot(getBarrelYRot(1));
        } else if (entity == getNthEntity(1) || entity == getNthEntity(3)) {
            float min = -40f - getRoll();
            float max = 40f - getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -140.0F, -40.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(getYRot() - 90);
        } else if (entity == getNthEntity(2) || entity == getNthEntity(4) || entity == getNthEntity(6)) {
            float min = -40f + getRoll();
            float max = 40f + getRoll();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, 40.0F, 140.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(getYRot() + 90);
        } else if (entity == getNthEntity(5)) {
            float min = -40f + getXRot();
            float max = 40f + getXRot();

            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, min, max);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot() - 180);
            float f3 = Mth.clamp(f2, -50, 50);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(getYRot() - 180);
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    public Vec3 passengerCameraPos(float ticks, Entity entity) {
        int i = this.getSeatIndex(entity);

        Matrix4f transformV = getVehicleTransform(ticks);

        Vector4f worldPosition;
        if (i == 1) {
            worldPosition = transformPosition(transformV, 1.803125f, 1.9765625f, -0.8125f);
        } else if (i == 2) {
            worldPosition = transformPosition(transformV, -1.803125f, 1.9765625f, -0.8125f);
        } else if (i == 3) {
            worldPosition = transformPosition(transformV, 1.615625f, 1.9765625f, -2.1875f);
        } else if (i == 4) {
            worldPosition = transformPosition(transformV, -1.615625f, 1.9765625f, -2.1875f);
        } else if (i == 5) {
            worldPosition = transformPosition(transformV, 0.6875f, 1.6015625f, -4.1f);
        } else if (i == 6) {
            worldPosition = transformPosition(transformV, -1.50625f, 1.9765625f, -3.0625f);
        } else {
            worldPosition = transformPosition(transformV, 0, 1, 0);
        }
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Vec3 getPassengerVec(Entity entity ,int i) {
        Matrix4f transformV = getVehicleTransform(1);
        Vector4f worldPosition = transformPosition(transformV, 0, 0, 0);
        Vec3 root = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        Vector4f vector4f = switch (i) {
            case 1, 3 -> transformPosition(transformV, 1, 0, 0);
            case 2, 4, 6 -> transformPosition(transformV, -1, 0, 0);
            case 5 -> transformPosition(transformV, 0, 0, -1);
            default -> transformPosition(transformV, 0, 0, 0);
        };
        Vec3 v0 = new Vec3(vector4f.x, vector4f.y, vector4f.z);
        return root.vectorTo(v0).normalize();
    }

    @Override
    public int passengerSeatLocation(Entity entity) {
        int i = this.getSeatIndex(entity);
        return i == 0 ? 1 : 0;
    }

    private PlayState firePredicate(AnimationState<Btr4Entity> event) {
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
    public int mainGunRpm(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return 325;
            } else if (getWeaponIndex(0) == 1) {
                return 700;
            }
        }

        return 700;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeaponIndex(0) == 0) {
                return (this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(living)) && !cannotFire;
            } else if (getWeaponIndex(0) == 1) {
                return (this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(living)) && !cannotFireCoax;
            } else if (getWeaponIndex(0) == 2) {
                return (this.entityData.get(LOADED_MISSILE) > 0);
            }
        } else {
            return this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(living);
        }

        return true;
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        if (living == getNthEntity(0)) {
            if (getWeaponIndex(0) == 1) {
                return this.entityData.get(MG_AMMO);
            } else {
                return this.entityData.get(AMMO);
            }
        } else {
            return this.entityData.get(MG_AMMO);
        }
    }

    @Override
    public boolean banHand(LivingEntity entity) {
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
    public int getWeaponHeat(LivingEntity living) {
        if (getWeaponIndex(0) == 0) {
            return entityData.get(HEAT);
        } else if (getWeaponIndex(0) == 1) {
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

        if (this.getWeaponIndex(0) == 0) {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/bmp_cannon_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            int heat = this.getEntityData().get(HEAT);
            guiGraphics.drawString(font, Component.literal(" 30MM 2A72 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        } else if (this.getWeaponIndex(0) == 1) {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            int heat = this.getEntityData().get(COAX_HEAT);
            guiGraphics.drawString(font, Component.literal(" 7.62MM PKT " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        } else {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_missile_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            guiGraphics.drawString(font, Component.literal("  PTRK  " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), screenWidth / 2 - 33, screenHeight - 65, color, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            double heat = this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("30MM 2A72 " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM PKT " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        } else {
            guiGraphics.drawString(font, Component.literal("PTRK " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), 30, -9, -1, false);
        }
    }

    @Override
    public int getHudColor() {
        return 0xFFC700;
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
    public boolean useFixedCameraPos(Entity entity) {
        return this.getSeatIndex(entity) != 0;
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
                return passengerCameraPos(partialTicks, player);
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return VVP.loc("textures/gui/vehicle/type/ukr.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb1, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7,
                this.obb8, this.obb9, this.obbTurret);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 1.53906f, 0.67969f, 2.78125f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition1 = transformPosition(transform, 1.53906f, 0.67969f, 1.15625f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 1.53906f, 0.67969f, -0.90625f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(2, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.53906f, 0.67969f, -2.53125f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(3, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.53906f, 0.67969f, 2.78125f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(4, this));

        Vector4f worldPosition5 = transformPosition(transform, -1.53906f, 0.67969f, 1.15625f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(5, this));

        Vector4f worldPosition6 = transformPosition(transform, -1.53906f, 0.67969f, -0.90625f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(6, this));

        Vector4f worldPosition7 = transformPosition(transform, -1.53906f, 0.67969f, -2.53125f);
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(7, this));

        // кабина
        Vector4f worldPosition8 = transformPosition(transform, 0.0f, 1.9296875f, 3.5390625f);
        this.obb8.center().set(new Vector3f(worldPosition8.x, worldPosition8.y, worldPosition8.z));
        this.obb8.setRotation(VectorTool.combineRotations(7, this));

        // корпус
        Vector4f worldPosition9 = transformPosition(transform, 0.0f, 1.9609375f, -1.03125f);
        this.obb9.center().set(new Vector3f(worldPosition9.x, worldPosition9.y, worldPosition9.z));
        this.obb9.setRotation(VectorTool.combineRotations(7, this));

        Matrix4f transformT = getTurretTransform(1);
        Vector4f worldPositionT = transformPosition(transformT, 0.0f, 0.0f, 0.0f);
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

        return super.interact(player, hand);
    }

    @Override
    public boolean hasPassengerTurretWeapon() {
        return false;
    }
}
