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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import tech.vvp.vvp.config.server.ExplosionConfigVVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class TerminatorEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> CANNON_FIRE_TIME = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CURRENT_MISSILE = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_FIRE_COOLDOWN = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> HAS_MANGAL = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_FOLIAGE = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_FOLIAGE_BODY = SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> LAST_BARREL_LEFT =
            SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> LAST_MISSILE_LEFT =
            SynchedEntityData.defineId(TerminatorEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public int reloadCoolDown;



    // Смещение дуль относительно центра стволбокса
    private static final float CANNON_MUZZLE_X_OFFSET = 0.2084062f; // влево/вправо (подстрой под модель)
    private static final float CANNON_MUZZLE_Y       = -0.0003375f;
    private static final float CANNON_MUZZLE_Z       = 3.3871062f;

    private static final float MISSILE_MUZZLE_X_OFFSET = 1.2000000f;
    private static final float MISSILE_MUZZLE_Y       = -0.2593750f;
    private static final float MISSILE_MUZZLE_Z       = 1.0675125f;

    private Vec3 getCannonMuzzlePos(float ticks, boolean left) {
        Matrix4f transform = getBarrelTransform(ticks);
        float x = left ? -CANNON_MUZZLE_X_OFFSET : CANNON_MUZZLE_X_OFFSET;
        Vector4f wp = transformPosition(transform, x, CANNON_MUZZLE_Y, CANNON_MUZZLE_Z);
        return new Vec3(wp.x, wp.y, wp.z);
    }

    private Vec3 getMissileMuzzlePos(float ticks, boolean left) {
        Matrix4f transform = getBarrelTransform(ticks);
        float x = left ? -MISSILE_MUZZLE_X_OFFSET : MISSILE_MUZZLE_X_OFFSET;
        Vector4f wp = transformPosition(transform, x, MISSILE_MUZZLE_Y, MISSILE_MUZZLE_Z);
        return new Vec3(wp.x, wp.y, wp.z);
    }

    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obbTurret;

    public TerminatorEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.TERMINATOR.get(), world);
    }

    public TerminatorEntity(EntityType<TerminatorEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(2.25f, 0.625f, 2.75f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(2.25f, 0.625f, 1.28125f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.34375f, 0.3984375f, 3.6171875f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.34375f, 0.3984375f, 3.6171875f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(1.2890625f, 0.5859375f, 1.3828125f), new Quaternionf(), OBB.Part.TURRET);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .damage(VehicleConfigVVP.TERMINATOR_CANNON_DAMAGE.get())
                                .explosionDamage(VehicleConfigVVP.TERMINATOR_CANNON_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.TERMINATOR_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.BUSHMASTER_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.BUSHMASTER_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.BUSHMASTER_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.BUSHMASTER_VERYFAR.get()),
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
                                .damage(ExplosionConfigVVP.TERMINATOR_MISSILE_DAMAGE.get())
                                .explosionDamage(ExplosionConfigVVP.TERMINATOR_MISSILE_EXPLOSION_DAMAGE.get())
                                .explosionRadius(ExplosionConfigVVP.TERMINATOR_MISSILE_EXPLOSION_RADIUS.get())
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
        this.entityData.define(HAS_MANGAL, false);
        this.entityData.define(HAS_FOLIAGE, false);
        this.entityData.define(HAS_FOLIAGE_BODY, false);
        this.entityData.define(LAST_BARREL_LEFT, false);
        this.entityData.define(LAST_MISSILE_LEFT, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putInt("MissileFireCooldown", this.entityData.get(MISSILE_FIRE_COOLDOWN));
        compound.putBoolean("HasMangal", this.entityData.get(HAS_MANGAL));
        compound.putBoolean("HasFoliage", this.entityData.get(HAS_FOLIAGE));
        compound.putBoolean("HasFoliageBody", this.entityData.get(HAS_FOLIAGE_BODY));
        compound.putBoolean("LastBarrelLeft", this.entityData.get(LAST_BARREL_LEFT));
        compound.putBoolean("LastMissileLeft", this.entityData.get(LAST_MISSILE_LEFT));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(MISSILE_FIRE_COOLDOWN, compound.getInt("MissileFireCooldown"));
        this.entityData.set(HAS_MANGAL, compound.getBoolean("HasMangal"));
        this.entityData.set(HAS_FOLIAGE, compound.getBoolean("HasFoliage"));
        this.entityData.set(HAS_FOLIAGE_BODY, compound.getBoolean("HasFoliageBody"));
        this.entityData.set(LAST_BARREL_LEFT, compound.getBoolean("LastBarrelLeft"));
        this.entityData.set(LAST_MISSILE_LEFT, compound.getBoolean("LastMissileLeft"));
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

            int mcd = this.entityData.get(MISSILE_FIRE_COOLDOWN);
            if (mcd > 0) this.entityData.set(MISSILE_FIRE_COOLDOWN, mcd - 1);

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
        return 6.5f;
    }
    // 炮塔最大俯仰旋转速度
    @Override
    public float turretXSpeed() {
        return 7f;
    }
    // 炮塔最小俯角
    @Override
    public float turretMinPitch() {
        return -7f;
    }
    // 炮塔最大仰角
    @Override
    public float turretMaxPitch() {
        return 30;
    }
    // 炮弹发射位置
    @Override
    public Vec3 getTurretShootPos(Entity entity, float ticks) {
        Matrix4f transform = getBarrelTransform(1);
        Vector4f worldPosition;
        if (getWeaponIndex(0) == 0) {
            worldPosition = transformPosition(transform, -0.2084062f, -0.0003375f, 3.3871062f);
        } else if (getWeaponIndex(0) == 1) {
            worldPosition = transformPosition(transform, -0.0030187f, -0.1851875f, 0.6547187f);
        } else  {
            worldPosition = transformPosition(transform, 1.2f, -0.2593750f, 1.0675125f);
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
                && this.reloadCoolDown <= 0 && this.getEntityData().get(LOADED_MISSILE) < 4) {
            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) + 1);
            this.reloadCoolDown = 180;
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

            // Чередуем стволы: текущий выстрел – противоположный к последнему
            boolean fireLeft = !this.entityData.get(LAST_BARREL_LEFT);
            Vec3 muzzle = getCannonMuzzlePos(1, fireLeft);

            var smallCannonShell = ((SmallCannonShellWeapon) getWeapon(0)).create(living);
            smallCannonShell.setPos(muzzle.x, muzzle.y, muzzle.z);
            smallCannonShell.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, projectileVelocity(living), 0.25f);
            this.level().addFreshEntity(smallCannonShell);

            sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, muzzle.x, muzzle.y, muzzle.z, 1, 0.02, 0.02, 0.02, 0, false);
            playShootSound3p(living, 0, 4, 12, 24, muzzle);
            ShakeClientMessage.sendToNearbyPlayers(this, 5, 6, 5, 9);

            // Сообщаем клиенту, какой ствол выстрелил – для отката кости
            this.entityData.set(LAST_BARREL_LEFT, fireLeft);

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
             // Проверка КД между пусками
             if (this.entityData.get(MISSILE_FIRE_COOLDOWN) > 0) return;

             // Лево ↔ Право
             boolean fireLeftMissile = !this.entityData.get(LAST_MISSILE_LEFT);
             Vec3 mPos = getMissileMuzzlePos(1, fireLeftMissile);

             var wgMissileEntity = ((WgMissileWeapon) getWeapon(0)).create(living);
             wgMissileEntity.setPos(mPos.x, mPos.y, mPos.z);
             wgMissileEntity.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, projectileVelocity(living), 0f);
             living.level().addFreshEntity(wgMissileEntity);

             playShootSound3p(living, 0, 6, 0, 0, mPos);

             // Запоминаем сторону и вешаем КД 1.5 сек (30 тиков)
             this.entityData.set(LAST_MISSILE_LEFT, fireLeftMissile);
             this.entityData.set(MISSILE_FIRE_COOLDOWN, 30);

             this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);

             // этот КД отвечает за перезарядку из инвентаря (оставляем как было)
             reloadCoolDown = 160;
         }

        }
    }

    @Override
    public void travel() {
        trackEngine(false, 0.052, VehicleConfigVVP.BRADLEY_ENERGY_COST.get(), 0.55, 0.5, 1.9, 0.8, 0.21f, -0.16f, 0.0015f, 0.0004f, 0.1f);
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
            worldPosition = transformPosition(transform, -0.75f, -0.2f, -1.6f);
        } else if (i == 1) {
            worldPosition = transformPosition(transformV, 0f, 1.2f, 0f);
        } else if (i == 2) {
            worldPosition = transformPosition(transformV, 0f, 1.2f, 0f);
        } else if (i == 3) {
            worldPosition = transformPosition(transformV, 0f, 1.2f, 0f);
        } else if (i == 4) {
            worldPosition = transformPosition(transformV, 0f, 1.2f, 0f);
        } else if (i == 5) {
            worldPosition = transformPosition(transformV, 0f, 1.2f, 0f);
        } else if (i == 6) {
            worldPosition = transformPosition(transformV, 0f, 1.2f, 0f);
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
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0.0160500f, 0.2873687f, -1.4599187f);

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
        Vector4f worldPosition = transformPosition(transform, -0.0093750f, 2.7001938f, -0.6247500f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }
    @Override
    public float rotateYOffset() {
        return 0f;
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
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }


    private PlayState firePredicate(AnimationState<TerminatorEntity> event) {
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
                return (this.entityData.get(LOADED_MISSILE) > 0) && this.entityData.get(MISSILE_FIRE_COOLDOWN) <= 0;
            }
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
        }

        return this.entityData.get(AMMO);
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
            guiGraphics.drawString(font, Component.literal(" 2А42 30MM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        } else if (this.getWeaponIndex(0) == 1) {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            int heat = this.getEntityData().get(COAX_HEAT);
            guiGraphics.drawString(font, Component.literal(" 7.62MM PKTM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        } else {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_missile_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            guiGraphics.drawString(font, Component.literal("  9М120-1 MISSLE  " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), screenWidth / 2 - 33, screenHeight - 65, color, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            double heat = this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("2А42 30MM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM PKTM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        } else {
            guiGraphics.drawString(font, Component.literal("9М120-1 MISSLE " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), 30, -9, -1, false);
        }
    }

    @Override
    public int getHudColor() {
        return 0xFFFFFF;
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
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()) - 6 * player.getViewVector(partialTicks).x,
                        Mth.lerp(partialTicks, player.yo + player.getEyeHeight() + 1, player.getEyeY() + 1) - 6 * player.getViewVector(partialTicks).y,
                        Mth.lerp(partialTicks, player.zo, player.getZ()) - 6 * player.getViewVector(partialTicks).z);
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return VVP.loc("textures/gui/vehicle/type/land_ru.png");
    }
    public List<OBB> getOBBs() {
        return List.of(this.obb1, this.obb2, this.obb3, this.obb4, this.obbTurret);
    }

    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition1 = transformPosition(transform, 0.0f, 1.5f, -1.75f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.0f, 1.5f, 2.28125f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.71875f, 0.476562f, -0.382812f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.71875f, 0.476562f, -0.382812f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT = getTurretTransform(1);
        Vector4f worldPositionT = transformPosition(transformT, 0.0f, 0.0f, 0.0f);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));

    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Загрузка модулей (отдельные if для каждого, как раньше)
        if (stack.is(tech.vvp.vvp.init.ModItems.MANGAL_TURRET.get()) && !this.entityData.get(HAS_MANGAL)) {
            return loadModule(player, stack, HAS_MANGAL, tech.vvp.vvp.init.ModItems.MANGAL_TURRET.get());  // Универсальная функция (см. ниже)
        }
        if (stack.is(tech.vvp.vvp.init.ModItems.SETKA_TURRET.get()) && !this.entityData.get(HAS_FOLIAGE)) {
            return loadModule(player, stack, HAS_FOLIAGE, tech.vvp.vvp.init.ModItems.SETKA_TURRET.get());
        }

        // Универсальное удаление с ключом (один if для всех флагов)
        if (stack.is(tech.vvp.vvp.init.ModItems.WRENCH.get())) {
            // Проверяем флаги по порядку (можно сделать цикл для всех)
            if (this.entityData.get(HAS_MANGAL)) {
                return removeModule(player, HAS_MANGAL, tech.vvp.vvp.init.ModItems.MANGAL_TURRET.get());
            } else if (this.entityData.get(HAS_FOLIAGE)) {
                return removeModule(player, HAS_FOLIAGE, tech.vvp.vvp.init.ModItems.SETKA_TURRET.get());
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

        // Если ничего не подошло — базовая логика (вход/инвентарь)
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

    @Override
    public boolean hasPassengerTurretWeapon() {
        return false;
    }
}
