package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.ExplosionConfigVVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.entity.vehicle.weapon.SpikeATGMWeapon;
import tech.vvp.vvp.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class PumaEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {

    public static final EntityDataAccessor<Integer> CANNON_FIRE_TIME = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CURRENT_MISSILE = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_FIRE_COOLDOWN = SynchedEntityData.defineId(PumaEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public int reloadCoolDown;
    private static final int MISSILE_CD_TICKS = 10;

    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obbTurret;
    public OBB obbBarrel;

    public PumaEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.PUMA.get(), world);
    }

    public PumaEntity(EntityType<PumaEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(73f/32f, 13f/32f, 96f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(36f/32f, 13f/32f, 132f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(55f/32f, 13f/32f, 36f/32f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(12f/32f, 24f/32f, 125f/32f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(12f/32f, 24f/32f, 125f/32f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(29f/32f, 17f/32f, 57f/32f), new Quaternionf(), OBB.Part.TURRET);
        this.obbBarrel = new OBB(this.position().toVector3f(), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternionf(), OBB.Part.BODY);

    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .damage(VehicleConfigVVP.PUMA_CANNON_DAMAGE.get())
                                .explosionDamage(VehicleConfigVVP.PUMA_CANNON_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.PUMA_CANNON_EXPLOSION_RADIUS.get().floatValue())
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
                        new SpikeATGMWeapon()
                                .damage(ExplosionConfigVVP.SPIKE_MISSILE_DAMAGE.get())
                                .explosionDamage(ExplosionConfigVVP.SPIKE_MISSILE_EXPLOSION_DAMAGE.get())
                                .explosionRadius(ExplosionConfigVVP.SPIKE_MISSILE_EXPLOSION_RADIUS.get())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(tech.vvp.vvp.init.ModSounds.TOW_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.TOW_3P.get()),
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


        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }

            int mcd = this.entityData.get(MISSILE_FIRE_COOLDOWN);
            if (mcd > 0) {
                this.entityData.set(MISSILE_FIRE_COOLDOWN, mcd - 1);
            }

            this.handleAmmo();
        }


        this.terrainCompact(4f, 5f);
        inertiaRotate(1);
        releaseSmokeDecoy(getTurretVector(3));
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
        return 7.5f;
    }
    // 炮塔最大俯仰旋转速度
    @Override
    public float turretXSpeed() {
        return 8f;
    }
    // 炮塔最小俯角
    @Override
    public float turretMinPitch() {
        return -7.5f;
    }
    // 炮塔最大仰角
    @Override
    public float turretMaxPitch() {
        return 40;
    }
    // 炮弹发射位置
    @Override
    public Vec3 getTurretShootPos(Entity entity, float ticks) {
        Matrix4f transform = getBarrelTransform(1);
        Vector4f worldPosition;
        if (getWeaponIndex(0) == 0) {
            worldPosition = transformPosition(transform, 0f, -0.25f, 3.2f);
        } else if (getWeaponIndex(0) == 1) {
            worldPosition = transformPosition(transform, -0.6f, -0.1f, 0.75f);
        } else  {
            worldPosition = transformPosition(transform, 27f/16f, -0.2593750f, -3f/16f);
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
                && this.reloadCoolDown <= 0 && this.getEntityData().get(LOADED_MISSILE) < 2) {
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
                // Проверка КД между пусками
                if (this.entityData.get(MISSILE_FIRE_COOLDOWN) > 0) return;

                var SpikeATGMEntity = ((SpikeATGMWeapon) getWeapon(0)).create(living);

                SpikeATGMEntity.setPos(getTurretShootPos(living, 1).x, getTurretShootPos(living, 1).y, getTurretShootPos(living, 1).z);
                SpikeATGMEntity.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, projectileVelocity(living), 0f);
                living.level().addFreshEntity(SpikeATGMEntity);
                playShootSound3p(living, 0, 6, 0, 0, getTurretShootPos(living, 1));

                this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);

                // КД 0.5 секунды между пусками
                this.entityData.set(MISSILE_FIRE_COOLDOWN, MISSILE_CD_TICKS);

                reloadCoolDown = 160; // как и было — перезарядка из инвентаря
            }

        }
    }

    @Override
    public void travel() {
        trackEngine(false, 0.052, VehicleConfigVVP.PUMA_ENERGY_COST.get(), 0.55, 0.5, 1.9, 0.8, 0.21f, -0.16f, 0.0020f, 0.0019f, 0.1f);
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
            worldPosition = transformPosition(transform, 17.1f/16f, -20f/16f, 17f/16f);
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
        Vector4f worldPosition = transformPosition(transform, -8.883f/16f, 4.6865f/16f, 15.2036f/16f);

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
        Vector4f worldPosition = transformPosition(transform, 9.3372f/16f, 47.6336f/16f, -24.121f/16f);

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
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }


    private PlayState firePredicate(AnimationState<PumaEntity> event) {
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
                return 200;
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
                return this.entityData.get(LOADED_MISSILE) > 0
                        && this.entityData.get(MISSILE_FIRE_COOLDOWN) <= 0;
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
            guiGraphics.drawString(font, Component.literal(" M242 25MM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        } else if (this.getWeaponIndex(0) == 1) {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_gun_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            int heat = this.getEntityData().get(COAX_HEAT);
            guiGraphics.drawString(font, Component.literal(" 7.62MM M240C " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
        } else {
            RenderHelper.blit(poseStack, Mod.loc("textures/screens/land/lav_missile_cross.png"), centerW, centerH, 0, 0.0F, scaledMinWH, scaledMinWH, scaledMinWH, scaledMinWH, color);
            guiGraphics.drawString(font, Component.literal("  BGM-71 TOW  " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), screenWidth / 2 - 33, screenHeight - 65, color, false);
        }
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        if (this.getWeaponIndex(0) == 0) {
            double heat = this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("M242 25MM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM M240C " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        } else {
            guiGraphics.drawString(font, Component.literal("BGM-71 TOW " + this.getEntityData().get(LOADED_MISSILE) + " " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getEntityData().get(MISSILE_COUNT))), 30, -9, -1, false);
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
        return VVP.loc("textures/gui/vehicle/type/land.png");
    }

    public List<OBB> getOBBs() {
        return List.of(this.obb1, this.obb2, this.obb3, this.obb4, this.obb5, this.obbTurret, this.obbBarrel);
    }

    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition1 = transformPosition(transform, -0.5f/16f, 31.5f/16f, -20f/16f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.0f/16f, 18.5f/16f, -2f/16f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, -0.5f/16f, 31.5f/16f, 46f/16f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 24f/16f, 13f/16f, -5.5f/16f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, -24f/16f, 13f/16f, -5.5f/16f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT = getTurretTransform(1);
        Vector4f worldPositionT = transformPosition(transformT, 0.0f, 0.0f, 0.0f);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));

        Matrix4f transformB = getBarrelTransform(1);
        Vector4f worldPositionB = transformPosition(transformB, 0.0f, 0.0f, 0.0f);
        this.obbBarrel.center().set(new Vector3f(worldPositionB.x, worldPositionB.y, worldPositionB.z));
        this.obbBarrel.setRotation(VectorTool.combineRotationsTurret(1, this));

    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);


        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {  // Только на сервере
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 4;  // Количество типов (default=0, desert=1, forest=2)
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
    public float getTurretMaxHealth() {
        return 140;
    }

    @Override
    public float getWheelMaxHealth() {
        return 56;
    }

    @Override
    public float getEngineMaxHealth() {
        return 143;
    }

    @Override
    public boolean hasPassengerTurretWeapon() {
        return false;
    }
}
