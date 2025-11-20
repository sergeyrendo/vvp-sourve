package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.entity.weapon.BallisticMissileEntity;
import tech.vvp.vvp.entity.weapon.BallisticMissileWeapon;
import tech.vvp.vvp.init.CoordinateTargetVehicle;
import tech.vvp.vvp.init.ModEntities;

import javax.annotation.ParametersAreNonnullByDefault;

public class M142HimarsEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, CoordinateTargetVehicle {
    private final AnimatableInstanceCache cache;
    private boolean shotToggled;

    private static final EntityDataAccessor<Float> POD_ROT = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> POD_TOGGLED = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.INT);
    
    // Автоматическое наведение
    private Vec3 targetPosition = null;
    private Vec3 lastTargetPosition = null;
    private boolean isAutoAiming = false;
    private float targetYaw = 0;
    private float targetPitch = 0;
    
    // Система запуска с задержкой
    private Vec3 pendingLaunchTarget = null;
    private Player pendingLaunchPlayer = null;
    private int launchDelayTicks = 0;
    private static final int LAUNCH_DELAY = 40; // 2 секунды (40 тиков)

    public M142HimarsEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.M142_HIMARS.get(), world);
    }

    public M142HimarsEntity(EntityType<M142HimarsEntity> type, Level world) {
        super(type, world);
        this.cache = GeckoLibUtil.createInstanceCache(this);
        this.setMaxUpStep(1.5f);
        
        // Устанавливаем начальное положение башни
        this.setTurretYRot(0);
        this.setTurretXRot(0);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POD_ROT, 0.0F);
        this.entityData.define(POD_TOGGLED, false);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("PodRot", this.entityData.get(POD_ROT));
        compound.putBoolean("PodToggled", this.entityData.get(POD_TOGGLED));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(POD_ROT, compound.getFloat("PodRot"));
        this.entityData.set(POD_TOGGLED, compound.getBoolean("PodToggled"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
    }

    public void setPodRot(float value) {
        this.entityData.set(POD_ROT, value);
    }

    public float getPodRot() {
        return this.entityData.get(POD_ROT);
    }

    public void setPodToggled(boolean value) {
        this.entityData.set(POD_TOGGLED, value);
    }

    public boolean getPodToggled() {
        return this.entityData.get(POD_TOGGLED);
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
            new BallisticMissileWeapon().sound(ModSounds.INTO_MISSILE.get())
        }};
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(2.75F + ClientMouseHandler.custom3pDistanceLerp, 1.0F, 0.0F);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.3), this.random.nextFloat() * 0.15F + 1.05F);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.25F) * damage);
    }

    @Override
    public void baseTick() {
        // Сохраняем старые значения ДО автонаведения
        this.rudderRotO = this.getRudderRot();
        this.leftWheelRotO = this.getLeftWheelRot();
        this.rightWheelRotO = this.getRightWheelRot();

        // Автоматическое наведение на цель (плавное)
        if (isAutoAiming && targetPosition != null) {
            autoAimToTarget();
            
            // Проверяем, довернулась ли башня
            float currentYaw = this.getTurretYRot();
            float currentPitch = this.getTurretXRot();
            float yawDiff = Math.abs(Mth.wrapDegrees(targetYaw - currentYaw));
            float pitchDiff = Math.abs(targetPitch - currentPitch);
            
            // Если довернулись (разница меньше 0.5 градуса) - отключаем автонаведение
            if (yawDiff < 0.5F && pitchDiff < 0.5F) {
                isAutoAiming = false;
                // НЕ сбрасываем targetPosition - башня остается в этом положении
            }
        }
        
        // Сохраняем значения башни ПОСЛЕ автонаведения
        this.turretYRotO = this.getTurretYRot();
        this.turretXRotO = this.getTurretXRot();
        
        // Обработка отложенного запуска ракеты
        if (pendingLaunchTarget != null && pendingLaunchPlayer != null) {
            launchDelayTicks++;
            
            // Проверяем что башня довернулась
            float currentYaw = this.getTurretYRot();
            float currentPitch = this.getTurretXRot();
            float yawDiff = Math.abs(Mth.wrapDegrees(targetYaw - currentYaw));
            float pitchDiff = Math.abs(targetPitch - currentPitch);
            
            // Запускаем ракету когда башня довернулась И прошла задержка
            if (launchDelayTicks >= LAUNCH_DELAY && yawDiff < 1.0F && pitchDiff < 1.0F) {
                actuallyLaunchMissile(pendingLaunchPlayer, pendingLaunchTarget);
                pendingLaunchTarget = null;
                pendingLaunchPlayer = null;
                launchDelayTicks = 0;
            }
            
            // Таймаут - если прошло слишком много времени
            if (launchDelayTicks > 200) { // 10 секунд
                if (!this.level().isClientSide) {
                    pendingLaunchPlayer.sendSystemMessage(Component.literal("§cLaunch cancelled - turret alignment timeout"));
                }
                pendingLaunchTarget = null;
                pendingLaunchPlayer = null;
                launchDelayTicks = 0;
            }
        }

        double fluidFloat = 0.052 * this.getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add(0.0F, fluidFloat, 0.0F));
        
        if (this.onGround()) {
            float f0 = 0.54F + 0.25F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.05 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.99, f0));
        } else if (this.isInWater()) {
            float f1 = 0.74F + 0.09F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.04 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.85, f1));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        this.lowHealthWarning();
        this.terrainCompact(2.7F, 3.61F);
        this.releaseSmokeDecoy(this.getTurretVector(1.0F));
        this.inertiaRotate(1.2F);
        this.refreshDimensions();
        super.baseTick();
    }

    @Override
    public float turretYSpeed() {
        return 0f; // Блокируем управление мышью
    }

    @Override
    public float turretXSpeed() {
        return 0f; // Блокируем управление мышью
    }

    @Override
    public float turretMinPitch() {
        return -7.5f;
    }

    @Override
    public float turretMaxPitch() {
        return 60f;
    }

    // Автоматическое наведение на цель
    private void autoAimToTarget() {
        if (targetPosition == null) return;

        // Плавно поворачиваем башню к целевым углам
        float currentYaw = this.getTurretYRot();
        float yawDiff = Mth.wrapDegrees(targetYaw - currentYaw);
        float yawSpeed = 2.5F; // Скорость поворота башни (градусов за тик)
        
        if (Math.abs(yawDiff) > 0.1F) {
            float newYaw = currentYaw + Mth.clamp(yawDiff, -yawSpeed, yawSpeed);
            this.setTurretYRot(newYaw);
        }

        // Плавно поднимаем/опускаем ствол
        float currentPitch = this.getTurretXRot();
        float pitchDiff = targetPitch - currentPitch;
        float pitchSpeed = 2.0F; // Скорость подъема ствола (градусов за тик)
        
        if (Math.abs(pitchDiff) > 0.1F) {
            float newPitch = currentPitch + Mth.clamp(pitchDiff, -pitchSpeed, pitchSpeed);
            this.setTurretXRot(Mth.clamp(newPitch, -7.5F, 60F));
        }
    }

    public void shootMissileTo(Player player, Vec3 targetPos) {
        // Проверяем что мы на сервере
        if (this.level().isClientSide) {
            return;
        }
        
        // Проверяем, те же ли координаты
        boolean sameTarget = lastTargetPosition != null && 
                             Math.abs(lastTargetPosition.x - targetPos.x) < 0.1 &&
                             Math.abs(lastTargetPosition.y - targetPos.y) < 0.1 &&
                             Math.abs(lastTargetPosition.z - targetPos.z) < 0.1;
        
        if (!sameTarget) {
            // Новая цель - включаем плавное наведение
            this.targetPosition = targetPos;
            this.lastTargetPosition = targetPos;
            this.isAutoAiming = true;

            // Вычисляем направление к цели и сохраняем в targetYaw/targetPitch
            Vec3 turretPos = getTurretShootPos(this, 1.0F);
            Vec3 toTarget = targetPos.subtract(turretPos);
            double horizontalDist = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
            this.targetYaw = (float) Math.toDegrees(Mth.atan2(-toTarget.x, toTarget.z));
            // Добавляем минимальный угол подъема 15 градусов для реалистичности
            this.targetPitch = Math.max(15.0F, (float) Math.toDegrees(Mth.atan2(toTarget.y, horizontalDist)));
            
            player.sendSystemMessage(Component.literal("§6Turret rotating to target..."));
            player.sendSystemMessage(Component.literal("§6Target: Yaw=" + (int)this.targetYaw + "° Pitch=" + (int)this.targetPitch + "°"));
        }

        // Ставим запуск в очередь - ракета запустится после поворота башни
        this.pendingLaunchTarget = targetPos;
        this.pendingLaunchPlayer = player;
        this.launchDelayTicks = 0;
        
        player.sendSystemMessage(Component.literal("§eMissile launch queued. Waiting for turret alignment..."));
    }
    
    // Фактический запуск ракеты (вызывается после задержки)
    private void actuallyLaunchMissile(Player player, Vec3 targetPos) {
        // Запускаем ракету
        Matrix4f transform = this.getBarrelTransform(1.0F);
        float x = shotToggled ? -0.5F : 0.5F; // Левая/правая сторона установки
        float y = 0.5F; // Чуть выше
        float z = 2.0F; // ВПЕРЕДИ башни (положительное значение)
        this.shotToggled = !this.shotToggled;

        Vector4f worldPosition = this.transformPosition(transform, x, y, z);
        
        player.sendSystemMessage(Component.literal("§7Missile spawn: " + (int)worldPosition.x + ", " + (int)worldPosition.y + ", " + (int)worldPosition.z));

        BallisticMissileEntity missile = ((BallisticMissileWeapon) this.getWeapon(0)).create(player);
        missile.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        missile.setXRot(90);
        missile.setYRot(180);
        missile.setTargetPosition(targetPos);

        this.level().addFreshEntity(missile);

        // Эффекты запуска
        ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE,
                worldPosition.x, worldPosition.y, worldPosition.z,
                10, 0.1, 0.1, 0.1, 0.0, false);

        // Звук запуска ракеты (без звука поворота башни)
        this.level().playSound(null, this.blockPosition(), tech.vvp.vvp.init.ModSounds.TOW_1P.get(), net.minecraft.sounds.SoundSource.PLAYERS, 3.0F, 0.8F);
        ShakeClientMessage.sendToNearbyPlayers(this, 6.0, 8.0, 6.0, 12.0);

        this.entityData.set(CANNON_RECOIL_TIME, 60);
        this.entityData.set(FIRE_ANIM, 3);

        // Отправляем сообщение игроку
        player.sendSystemMessage(Component.literal("§aMissile launched to: " + (int)targetPos.x + ", " + (int)targetPos.y + ", " + (int)targetPos.z));
    }

    @Override
    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.09 || Mth.abs(this.entityData.get(POWER)) > 0.15;
    }

    @Override
    public void vehicleShoot(LivingEntity living, int type) {
        // Не используется - стрельба только через координаты
    }

    @Override
    public boolean hasPassengerTurretWeapon() {
        return true;
    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = this.getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    @Override
    public void travel() {
        Entity passenger0 = this.getFirstPassenger();
        if (this.getEnergy() > 0) {
            if (passenger0 == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.sprintInputDown = false;
                this.entityData.set(POWER, 0.0F);
            }
            
            // Башня поднимается автоматически

            if (this.forwardInputDown) {
                this.entityData.set(POWER, org.joml.Math.min(this.entityData.get(POWER) + (this.entityData.get(POWER) < 0.0F ? 0.012F : 0.0024F), 0.18F));
            }

            if (this.backInputDown) {
                this.entityData.set(POWER, org.joml.Math.max(this.entityData.get(POWER) - (this.entityData.get(POWER) > 0.0F ? 0.012F : 0.0024F), -0.13F));
            }

            if (this.rightInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.1F);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.1F);
            }

            this.entityData.set(POWER, this.entityData.get(POWER) * (this.upInputDown ? 0.5F : (!this.rightInputDown && !this.leftInputDown ? 0.99F : 0.977F)));
            this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * (float) org.joml.Math.max(0.76F - 0.1F * this.getDeltaMovement().horizontalDistance(), 0.3));
            
            double s0 = this.getDeltaMovement().dot(this.getViewVector(1.0F));
            this.setLeftWheelRot((float)(this.getLeftWheelRot() - 1.25F * s0 - this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5F * this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRightWheelRot((float)(this.getRightWheelRot() - 1.25F * s0 + this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5F * this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRudderRot(Mth.clamp(this.getRudderRot() - this.entityData.get(DELTA_ROT), -0.8F, 0.8F) * 0.75F);

            this.setYRot((float)(this.getYRot() - Math.max((this.isInWater() && !this.onGround() ? 5 : 10) * this.getDeltaMovement().horizontalDistance(), 0.0F) * this.getRudderRot() * (this.entityData.get(POWER) > 0.0F ? 1 : -1)));
            
            if (this.isInWater() || this.onGround()) {
                float power = this.entityData.get(POWER) * Mth.clamp(1.0F + (float)(s0 > 0.0F ? 1 : -1) * this.getXRot() / 35.0F, 0.0F, 2.0F);
                this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((!this.isInWater() && !this.onGround() ? 0.05F : (this.isInWater() && !this.onGround() ? 0.3F : 1.0F)) * power)));
            }
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.LAV_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Mth.abs(this.entityData.get(POWER)) * 2.0F;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            int i = this.getSeatIndex(passenger);
            Vector4f worldPosition;
            if (i == 0) {
                // Позиция водителя: Pivot Point -23.6158 41.653 -56.5009
                // Конвертируем из Blockbench координат (делим на 16)
                float x = -23.6158F / 16.0F; // -1.476
                float y = 41.653F / 16.0F;   // 2.603
                float z = -56.5009F / 16.0F; // -3.531
                worldPosition = this.transformPosition(transform, x, y, z);
            } else {
                worldPosition = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);
            }
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
            this.copyEntityData(passenger);
        }
    }

    public void copyEntityData(Entity entity) {
        if (entity == this.getNthEntity(0)) {
            entity.setYBodyRot(this.getBarrelYRot(1.0F));
        }
    }

    @Override
    public int getMaxPassengers() {
        return 3;
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getTurretTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.3F, 0.75F, 0.56F);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Vec3 getTurretShootPos(Entity entity, float ticks) {
        Matrix4f transform = getBarrelTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0.0f, 0.0f, 2.0f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = this.getTurretTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, 0.0234375F, 0.33795F, 0.825F);
        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        
        float a = this.getTurretYaw(ticks);
        float r = (Mth.abs(a) - 90.0F) / 90.0F;
        float r2;
        if (Mth.abs(a) <= 90.0F) {
            r2 = a / 90.0F;
        } else if (a < 0.0F) {
            r2 = -(180.0F + a) / 90.0F;
        } else {
            r2 = (180.0F - a) / 90.0F;
        }

        float x = Mth.lerp(ticks, this.turretXRotO, this.getTurretXRot());
        float xV = Mth.lerp(ticks, this.xRotO, this.getXRot());
        float z = Mth.lerp(ticks, this.prevRoll, this.getRoll());
        transformT.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformT;
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = this.getTurretTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    protected void clampRotation(Entity entity) {
        // Не делаем ничего - башня управляется только через координаты
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        // Не делаем ничего - башня управляется только через координаты
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int mainGunRpm(LivingEntity living) {
        return 60;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        return !this.cannotFire && getPodToggled();
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean banHand(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return false;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(LivingEntity living) {
        return this.entityData.get(HEAT);
    }

    @Override
    public net.minecraft.resources.ResourceLocation getVehicleIcon() {
        return Mod.loc("textures/vehicle_icon/lav150_icon.png");
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.23 : (net.minecraft.client.Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.3 : 0.4);
    }

    @Override
    public boolean isEnclosed(int index) {
        return true;
    }

    @Override
    public net.minecraft.resources.ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }
}
