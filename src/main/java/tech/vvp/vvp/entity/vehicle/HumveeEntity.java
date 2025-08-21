package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.Yx100Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ArmedVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
// import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
// import com.atsuishio.superbwarfare.entity.vehicle.weapon.SwarmDroneWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import com.atsuishio.superbwarfare.entity.OBBEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.ParticleTypes;

import java.util.Comparator;
import java.util.List;

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
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import tech.vvp.vvp.VVP;

// Импортируем необходимые классы для атрибутов
// import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
// import net.minecraft.world.entity.ai.attributes.Attributes;
// import net.minecraft.world.entity.Mob;

public class HumveeEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, ArmedVehicleEntity, WeaponVehicleEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final EntityDataAccessor<Integer> HEAVY_AMMO = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CANNON_FIRE_TIME = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> SMOKE_DECOY = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUN_FIRE_TIME = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MG_AMMO = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_AMMO_TYPE = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> HAS_TENT_TURRET = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_TENT_BODY = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(HumveeEntity.class, EntityDataSerializers.INT);
    private VehicleWeapon[][] weapons = initWeapons();
    private int reloadCoolDown;
    private int selectedSeat;

    public OBB obb;
    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;
    public OBB obb7;

    public HumveeEntity(EntityType<? extends HumveeEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);

        this.obb = new OBB(this.position().toVector3f(), new Vector3f(1.438f, 0.875f, 1.219f), new Quaternionf(), OBB.Part.BODY);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(1.438f, 0.563f, 0.906f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(1.438f, 0.250f, 0.906f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.844f, 0.375f, 1.344f), new Quaternionf(), OBB.Part.TURRET);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.250f, 0.344f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(0.250f, 0.344f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(0.250f, 0.344f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(0.250f, 0.344f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);

    }


    @SuppressWarnings("unchecked")
    public static HumveeEntity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<HumveeEntity> castedEntityType = (EntityType<HumveeEntity>) entityTypeFromPacket;
        HumveeEntity entity = new HumveeEntity(castedEntityType, world);
        return entity;
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{

                },
                new VehicleWeapon[]{
                        // 机枪
                        new ProjectileWeapon()
                                .damage(VehicleConfig.HEAVY_MACHINE_GUN_DAMAGE.get())
                                .headShot(2)
                                .zoom(false)
                                .bypassArmorRate(0.4f)
                                .ammo(ModItems.HEAVY_AMMO.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_12_7mm.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.M2_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.M2_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.M2_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.M2_VERYFAR.get()),
                }
        };
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<HumveeEntity>(this, "movement", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/humvee_icon.png");
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(2.75, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        // Определяем HAS_TURRET ПЕРЕД вызовом super, чтобы избежать NPE в цепочке
        // Можно добавить другие, если они тоже используются в getMaxPassengers или аналогичных методах
        // (в вашем коде только HAS_TURRET критичен)

        super.defineSynchedData();

        // Остальные определения после super
        this.entityData.define(LOADED_AMMO_TYPE, 0);
        this.entityData.define(MG_AMMO, 0);
        this.entityData.define(HEAVY_AMMO, 0);
        this.entityData.define(CANNON_FIRE_TIME, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(MISSILE_COUNT, 0);
        this.entityData.define(SMOKE_DECOY, 2);
        this.entityData.define(GUN_FIRE_TIME, 0);
        this.entityData.define(HAS_TENT_TURRET, false);
        this.entityData.define(HAS_TENT_BODY, false);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
    }


    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        // Добавьте здесь любые дополнительные данные для сохранения, если они есть
        compound.putBoolean("HasTentBody", this.entityData.get(HAS_TENT_BODY));
        compound.putBoolean("HasTentTurret", this.entityData.get(HAS_TENT_TURRET));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(HAS_TENT_BODY, compound.getBoolean("HasTentBody"));
        this.entityData.set(HAS_TENT_TURRET, compound.getBoolean("HasTentTurret"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(com.atsuishio.superbwarfare.init.ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.15f + 1.05f);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    public void updateTurretRotation(Player player) {
        if (player == getNthEntity(1)) {
            float yaw = player.getYHeadRot();
            float pitch = player.getXRot();

            // Изменено: исправлена инверсия, сохранена нормализация
            this.setTurretYRot(Mth.wrapDegrees(this.getYRot() - yaw));
            this.setTurretXRot(Mth.clamp(-pitch, -45f, 45f));
        }
    }


    @Override
    public void baseTick() {
        turretYRotO = this.getTurretYRot();
        turretXRotO = this.getTurretXRot();
        rudderRotO = this.getRudderRot();
        leftWheelRotO = this.getLeftWheelRot();
        rightWheelRotO = this.getRightWheelRot();

        super.baseTick();
        updateOBB();

        if (this.entityData.get(GUN_FIRE_TIME) > 0) {
            this.entityData.set(GUN_FIRE_TIME, this.entityData.get(GUN_FIRE_TIME) - 1);
        }

        // Логика обновления поворота турели
        Entity gunner = getNthEntity(1); // Получаем сущность на втором месте
        if (gunner instanceof Player) {
            updateTurretRotation((Player) gunner); // Вызываем наш метод
        }

        if (this.onGround()) {
            float f0 = 0.54f + 0.25f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.05 * this.getDeltaMovement().horizontalDistance())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.85, f0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.95, 0.99));
        }

        if (this.level() instanceof ServerLevel) {
            boolean hasCreativeAmmo = false;

            // ИСПРАВЛЕНО: Цикл по реальным пассажирам (getPassengers()), чтобы избежать IndexOutOfBounds
            for (Entity passenger : this.getPassengers()) {
                if (passenger instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                    hasCreativeAmmo = true;
                    break;  // Можно убрать break, если нужно проверить всех
                }
            }

            // Вызов handleAmmo (предполагаю, что он использует hasCreativeAmmo внутри)
            this.handleAmmo();  // Если handleAmmo зависит от hasCreativeAmmo, передайте его как параметр, если нужно

        }

        releaseSmokeDecoy(getTurretVector(1));

        lowHealthWarning();
        this.terrainCompact(2.7f, 3.61f);
        inertiaRotate(1.25f);

        this.refreshDimensions();
    }


    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.09 || Mth.abs(this.entityData.get(POWER)) > 0.15;
    }

    @Override
    public void travel() {
        Entity passenger0 = this.getFirstPassenger();

        if (this.getEnergy() <= 0) return;

        if (passenger0 == null) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
            this.entityData.set(POWER, 0f);
        }

        if (forwardInputDown) {
            this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + (this.entityData.get(POWER) < 0 ? 0.014f : 0.0036f), 0.26f));
        }

        if (backInputDown) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.entityData.get(POWER) > 0 ? 0.014f : 0.0036f), -0.15f));
        }

        if (rightInputDown) {
            this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.11f);
        } else if (this.leftInputDown) {
            this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.11f);
        }

        if (this.forwardInputDown || this.backInputDown) {
            this.consumeEnergy(VehicleConfig.LAV_150_ENERGY_COST.get());
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * (upInputDown ? 0.5f : (rightInputDown || leftInputDown) ? 0.977f : 0.99f));
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * (float) Math.max(0.76f - 0.1f * this.getDeltaMovement().horizontalDistance(), 0.3));


        if (this.forwardInputDown || this.backInputDown) {
            this.consumeEnergy(VehicleConfig.LAV_150_ENERGY_COST.get());
        }


        float angle = (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1));
        double s0;

        if (Mth.abs(angle) < 90) {
            s0 = this.getDeltaMovement().horizontalDistance();
        } else {
            s0 = -this.getDeltaMovement().horizontalDistance();
        }

        this.setLeftWheelRot((float) ((this.getLeftWheelRot() - 1.25 * s0) - this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5f * this.entityData.get(DELTA_ROT), -5f, 5f)));
        this.setRightWheelRot((float) ((this.getRightWheelRot() - 1.25 * s0) + this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5f * this.entityData.get(DELTA_ROT), -5f, 5f)));

        this.setRudderRot(Mth.clamp(this.getRudderRot() - this.entityData.get(DELTA_ROT), -0.8f, 0.8f) * 0.75f);

        this.setYRot((float) (this.getYRot() - Math.max(10 * this.getDeltaMovement().horizontalDistance(), 0) * this.getRudderRot() * (this.entityData.get(POWER) > 0 ? 1 : -1)));
        if (onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale(this.entityData.get(POWER))));
        }
    }


    @Override
    public SoundEvent getEngineSound() {
        return tech.vvp.vvp.init.ModSounds.HUMVEE_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Math.max(Mth.abs(entityData.get(POWER)), Mth.abs(0.1f * this.entityData.get(DELTA_ROT))) * 2.5f;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1);

        int i = this.getSeatIndex(passenger);
        Vector4f worldPosition;

        switch(i) {
            case 0: // Водитель (слева спереди)
                worldPosition = transformPosition(transform, 0.8f, 0.28f, 0.2f);
                break;
            case 1: // Пассажир рядом с водителем
                worldPosition = transformPosition(transform, 0.0f, 1.5f, 0.0f);
                break;
            case 2: // Пассажир сзади слева
                worldPosition = transformPosition(transform, 0.8f, 0.28f, -0.8f);
                break;
            case 3: // Пассажир сзади справа
                worldPosition = transformPosition(transform, -0.8f, 0.28f, -0.8f);
                break;
            case 4: // Пассажир рядом с водителем
                worldPosition = transformPosition(transform, -0.8f, 0.28f, 0.2f);
                break;
            default:
                worldPosition = transformPosition(transform, 0, 1, 0);
                break;
        }

        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public int getMaxPassengers() {
        // ИЗМЕНЕНО: Если нет турели, уменьшаем на 1 (seat 1 недоступен)
        return 5;
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 80f,
                    this.getX(), this.getY(), this.getZ(), 5f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
        }

        explodePassengers();
        super.destroy();
    }


    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0.044f, 2.571f, -0.098f); // Примерные координаты турели

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = getTurretTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z)
                .vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }


    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0.3625f, 0.293125f, 1.18095f);

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

        // Применяем вращение по оси X без инверсии
        transformT.rotate(Axis.XP.rotationDegrees(-(x + r * xV + r2 * z)));

        // Применяем смещение ствола относительно турели после всех вращений
        transformT.translate(0.3625f, 0.293125f, 1.18095f);

        return transformT;
    }


    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }




    @Override
    public void onPassengerTurned(Entity entity) {
        super.onPassengerTurned(entity); // Вызываем реализацию родительского класса
    }

    private PlayState idlePredicate(AnimationState<HumveeEntity> event) {
        if (Mth.abs((float)this.getDeltaMovement().horizontalDistanceSqr()) > 0.001 || Mth.abs(this.entityData.get(POWER)) > 0.05) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
    }

    // Реализация методов ArmedVehicleEntity - заглушки, так как оружия у нас больше нет

    @Override
    public int mainGunRpm(Player player) {
        return 470; // Нет оружия
    }

    @Override
    public boolean canShoot(Player player) {
        if (player == getNthEntity(1)) {
            return (this.entityData.get(MG_AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFire;
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        if (player == getNthEntity(1)) {
            return this.entityData.get(MG_AMMO);
        }
        return 0;
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        if (type == 1) {
            if (this.cannotFire) return;
            Matrix4f transform = getBarrelTransform(1);
            Vector4f worldPosition = transformPosition(transform, -0.8f, -0.3f, 0.0f);

            var projectile = (ProjectileWeapon) getWeapon(1);
            var projectileEntity = projectile.create(player).setGunItemId(this.getType().getDescriptionId() + ".2");

            projectileEntity.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
            projectileEntity.shoot(getBarrelVector(1).x, getBarrelVector(1).y + 0.01f, getBarrelVector(1).z, 20, 0.3f);

            this.level().addFreshEntity(projectileEntity);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 1, 4, 12, 24);
            }

            this.entityData.set(GUN_FIRE_TIME, 2);
            this.entityData.set(HEAT, this.entityData.get(HEAT) + 4);

            Level level = player.level();
            final Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for (Entity target : level.getEntitiesOfClass(Entity.class, new AABB(center, center).inflate(4), e -> true).stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ShakeClientMessage(6, 4, 6, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            if (hasCreativeAmmo) return;

            ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                if (stack.is(ModItems.AMMO_BOX.get())) {
                    return Ammo.HEAVY.get(stack) > 0;
                }
                return false;
            }).findFirst().orElse(ItemStack.EMPTY);

            if (!ammoBox.isEmpty()) {
                Ammo.HEAVY.add(ammoBox, -1);
            } else {
                consumeItem(getWeapon(1).ammo, 1);
            }
        }
    }

    private void handleAmmo() {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers(); i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        if (hasCreativeAmmo) {
            this.entityData.set(MG_AMMO, 9999);
        } else {
            // Считаем патроны из ammo box или отдельных патронов
            int totalAmmo = 0;

            // Проверяем ammo box
            for (ItemStack stack : this.getItemStacks()) {
                if (stack.is(ModItems.AMMO_BOX.get())) {
                    totalAmmo += Ammo.HEAVY.get(stack);
                }
            }

            // Проверяем отдельные патроны
            totalAmmo += countItem(ModItems.HEAVY_AMMO.get());

            this.entityData.set(MG_AMMO, totalAmmo);
        }
    }



    @Override
    public int zoomFov() {
        return 3; // Нет оптического прицела
    }

    @Override
    public int getWeaponHeat(Player player) {
        return 0; // Нет нагрева оружия
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        // Пассажиры внутри автомобиля видны
        return false;
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return 0.3; // Нормальная чувствительность для всех пассажиров
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (isFirstPerson) {
            return new Vec2(Mth.lerp(partialTicks, player.yHeadRotO, player.getYHeadRot()),
                    Mth.lerp(partialTicks, player.xRotO, player.getXRot()));
        }
        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (isFirstPerson) {
            // В режиме от первого лица камера находится примерно на уровне глаз
            return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()),
                    Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()),
                    Mth.lerp(partialTicks, player.zo, player.getZ()));
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(tech.vvp.vvp.init.ModItems.TENT.get()) && !this.entityData.get(HAS_TENT_TURRET)) {
            return loadModule(player, stack, HAS_TENT_TURRET, tech.vvp.vvp.init.ModItems.TENT.get());
        }
        if (stack.is(tech.vvp.vvp.init.ModItems.TENT.get()) && !this.entityData.get(HAS_TENT_BODY)) {
            return loadModule(player, stack, HAS_TENT_BODY, tech.vvp.vvp.init.ModItems.TENT.get());
        }

        // Универсальное удаление с ключом (один if для всех флагов)
        if (stack.is(tech.vvp.vvp.init.ModItems.WRENCH.get())) {
            // Проверяем флаги по порядку (можно сделать цикл для всех)
             if (this.entityData.get(HAS_TENT_TURRET)) {
                return removeModule(player, HAS_TENT_TURRET, tech.vvp.vvp.init.ModItems.TENT.get());
            } else if (this.entityData.get(HAS_TENT_BODY)) {
                return removeModule(player, HAS_TENT_BODY, tech.vvp.vvp.init.ModItems.TENT.get());
            }
        }

            if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
                if (!this.level().isClientSide) {  // Только на сервере
                    int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                    int maxTypes = 3;  // Количество типов (default=0, desert=1, forest=2)
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

    public List<OBB> getOBBs() {
        if (this.entityData.get(HAS_TENT_BODY))
        {return List.of(this.obb, this.obb1, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7);
        } else {
            return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7);
        }
    }

    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0.000f, 1.375f, -0.031f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        if (this.entityData.get(HAS_TENT_BODY)) {
            Vector4f worldPositionMangal = transformPosition(transform, 0.000f, 1.688f, -2.156f);  // Примерная позиция мангала (относительно турели; подкорректируй x/y/z)
            this.obb1.center().set(new Vector3f(worldPositionMangal.x, worldPositionMangal.y, worldPositionMangal.z));
            this.obb1.setRotation(VectorTool.combineRotations(1, this));  // Ротация как у турели
        }

        Vector4f worldPosition2 = transformPosition(transform, 0.000f, 1.375f, 2.094f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT= getVehicleTransform(1);

        Vector4f worldPosition3 = transformPosition(transformT, 0.031f, 2.625f, -0.156f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotationsTurret(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 1.188f, 0.594f, 2.125f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, 1.188f, 0.594f, -1.938f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition6 = transformPosition(transform, -1.188f, 0.594f, 2.125f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition7 = transformPosition(transform, -1.188f, 0.594f, -1.938f);
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(1, this));

    }
}
