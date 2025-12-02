package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.ParticleTool;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.joml.Vector4d;
import tech.vvp.vvp.entity.projectile.BallisticMissileEntity;
import tech.vvp.vvp.init.CoordinateTargetVehicle;

import javax.annotation.Nullable;
import java.util.UUID;

public class M142HimarsEntity extends GeoVehicleEntity implements CoordinateTargetVehicle {

    private static final EntityDataAccessor<Boolean> FIRING_MODE = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> AMMO = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> GUIDED_YAW = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GUIDED_PITCH = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> HAS_GUIDED_TARGET = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.BOOLEAN);

    private Vec3 targetPosition = null;
    private float targetYaw = 0;
    private float targetPitch = 0;
    private float currentYawError = 0f;
    private float currentPitchError = 0f;
    private static final float AIM_TOLERANCE_DEGREES = 1.5F;
    private static final float MIN_FIRING_PITCH = -18f;

    private int fireCooldown = 0;
    private int reloadCooldown = 0;
    private int launchSmokeTicks = 0;
    private boolean shotToggled = false;
    private boolean wasFirePressed = false;

    public M142HimarsEntity(EntityType<M142HimarsEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(FIRING_MODE, false);
        entityData.define(AMMO, 0);
        entityData.define(CAMOUFLAGE_TYPE, 0);
        entityData.define(GUIDED_YAW, 0f);
        entityData.define(GUIDED_PITCH, 0f);
        entityData.define(HAS_GUIDED_TARGET, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FiringMode", entityData.get(FIRING_MODE));
        compound.putInt("MissileAmmo", entityData.get(AMMO));
        compound.putInt("CamouflageType", entityData.get(CAMOUFLAGE_TYPE));
        compound.putFloat("GuidedYaw", entityData.get(GUIDED_YAW));
        compound.putFloat("GuidedPitch", entityData.get(GUIDED_PITCH));
        compound.putBoolean("HasGuidedTarget", entityData.get(HAS_GUIDED_TARGET));
        compound.putFloat("StoredTargetYaw", targetYaw);
        compound.putFloat("StoredTargetPitch", targetPitch);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        entityData.set(FIRING_MODE, compound.getBoolean("FiringMode"));
        entityData.set(AMMO, compound.getInt("MissileAmmo"));
        entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        targetYaw = compound.getFloat("StoredTargetYaw");
        targetPitch = compound.getFloat("StoredTargetPitch");
        boolean hasGuidance = compound.getBoolean("HasGuidedTarget");
        entityData.set(GUIDED_YAW, compound.getFloat("GuidedYaw"));
        entityData.set(GUIDED_PITCH, compound.getFloat("GuidedPitch"));
        entityData.set(HAS_GUIDED_TARGET, hasGuidance);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.25f) * damage);
    }

    // === Turret control ===
    public float turretYSpeed() {
        return 0f;
    }

    public float turretXSpeed() {
        return 0f;
    }

    public float turretMinPitch() {
        return -45f;
    }

    public float turretMaxPitch() {
        return 0f;
    }

    // === Режимы работы ===
    public boolean isFiringMode() {
        return entityData.get(FIRING_MODE);
    }

    public void setFiringMode(boolean firing) {
        entityData.set(FIRING_MODE, firing);
    }

    public void toggleMode() {
        boolean currentMode = isFiringMode();
        setFiringMode(!currentMode);

        if (!currentMode) {
            setDeltaMovement(Vec3.ZERO);
        } else {
            targetPosition = null;
            targetYaw = 0;
            targetPitch = 0;
            syncGuidanceState(false);
        }
    }


    // === Система координатного наведения ===
    public void setTargetCoordinates(Player player, Vec3 targetPos) {
        if (level().isClientSide) return;
        if (getFirstPassenger() != player) return;

        Vec3 currentPos = position();
        double distance = currentPos.distanceTo(targetPos);
        if (distance < 200.0 || distance > 1500.0) return;

        Vec3 diff = targetPos.subtract(position());
        double angle = Math.atan2(diff.z, diff.x);
        float worldYaw = (float) Math.toDegrees(angle) - 90.0F;
        float relativeYaw = Mth.wrapDegrees(worldYaw - getYRot());

        double horizontal = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        double ballisticPitch = tech.vvp.vvp.tools.HimarsBallistics.computePitch(horizontal);
        float relativePitch = (float) -ballisticPitch;

        this.targetYaw = relativeYaw;
        this.targetPitch = relativePitch;
        this.targetPosition = targetPos;
        syncGuidanceState(true);
    }

    private void syncGuidanceState(boolean hasTarget) {
        if (level().isClientSide) return;
        entityData.set(HAS_GUIDED_TARGET, hasTarget);
        entityData.set(GUIDED_YAW, hasTarget ? targetYaw : 0f);
        entityData.set(GUIDED_PITCH, hasTarget ? targetPitch : 0f);
    }

    public boolean hasGuidanceData() {
        return entityData.get(HAS_GUIDED_TARGET);
    }

    public float getGuidanceYaw() {
        return entityData.get(GUIDED_YAW);
    }

    public float getGuidancePitch() {
        return entityData.get(GUIDED_PITCH);
    }

    private float getRequiredYaw() {
        return level().isClientSide ? entityData.get(GUIDED_YAW) : targetYaw;
    }

    private float getRequiredPitch() {
        return level().isClientSide ? entityData.get(GUIDED_PITCH) : targetPitch;
    }

    private void updateAimGuidance() {
        if (!hasGuidanceData()) {
            currentYawError = 0f;
            currentPitchError = 0f;
            return;
        }

        float requiredYaw = getRequiredYaw();
        float requiredPitch = getRequiredPitch();

        currentYawError = Math.abs(Mth.wrapDegrees(requiredYaw - getTurretYRot()));
        currentPitchError = Math.abs(requiredPitch - getTurretXRot());
    }

    private boolean isAimAligned() {
        if (!hasGuidanceData()) {
            currentYawError = 0f;
            currentPitchError = 0f;
            return false;
        }
        float yawDiff = Math.abs(Mth.wrapDegrees(getRequiredYaw() - getTurretYRot()));
        float pitchDiff = Math.abs(getRequiredPitch() - getTurretXRot());
        currentYawError = yawDiff;
        currentPitchError = pitchDiff;
        return yawDiff <= AIM_TOLERANCE_DEGREES && pitchDiff <= AIM_TOLERANCE_DEGREES;
    }

    // === Ammo ===
    public int getMissileAmmo() {
        return entityData.get(AMMO);
    }

    public void setMissileAmmo(int ammo) {
        entityData.set(AMMO, Math.max(0, Math.min(6, ammo)));
    }

    // === Tick ===
    @Override
    public void baseTick() {
        super.baseTick();

        if (!level().isClientSide) {
            handleAmmoReload();
            handleLaunchSmoke();
        }

        if (fireCooldown > 0) fireCooldown--;

        if (targetPosition != null) {
            updateAimGuidance();
        } else {
            currentYawError = 0f;
            currentPitchError = 0f;
        }

        // Управление башней в режиме стрельбы
        if (isFiringMode() && getFirstPassenger() instanceof Player player) {
            float playerYaw = player.getYHeadRot();
            float playerPitch = player.getXRot();

            float relativeYaw = Mth.wrapDegrees(playerYaw - getYRot());
            setTurretYRot(relativeYaw);
            setTurretXRot(Mth.clamp(playerPitch, -45f, 0f));

            // Отключаем автоматическое управление башней SuperbWarfare
            this.entityData.set(MOUSE_SPEED_X, 0f);
            this.entityData.set(MOUSE_SPEED_Y, 0f);

            // Полностью блокируем движение в firing mode
            setDeltaMovement(getDeltaMovement().multiply(0, 1.0, 0));
            this.entityData.set(POWER, 0f);
        } else if (!isFiringMode()) {
            setTurretYRot(0F);
            setTurretXRot(0F);

            // Отключаем автоматическое управление башней SuperbWarfare
            this.entityData.set(MOUSE_SPEED_X, 0f);
            this.entityData.set(MOUSE_SPEED_Y, 0f);
        }

        // Обработка стрельбы
        if (getFirstPassenger() instanceof Player player) {
            if (fireInputDown() && !wasFirePressed && fireCooldown == 0) {
                vehicleShoot(player, null, null);
            }
            wasFirePressed = fireInputDown();
        }
    }

    private void handleAmmoReload() {
        if (reloadCooldown > 0) {
            reloadCooldown--;
            return;
        }

        boolean hasCreativeAmmo = false;
        if (getFirstPassenger() instanceof Player player) {
            hasCreativeAmmo = com.atsuishio.superbwarfare.tools.InventoryTool.hasCreativeAmmoBox(player);
        }

        if (hasCreativeAmmo) {
            setMissileAmmo(6);
        } else {
            int currentAmmo = getMissileAmmo();
            if (currentAmmo < 6) {
                int ammoInContainer = countItem(tech.vvp.vvp.init.ModItems.GMLRS_M31.get());
                if (ammoInContainer > 0) {
                    consumeItem(tech.vvp.vvp.init.ModItems.GMLRS_M31.get(), 1);
                    setMissileAmmo(currentAmmo + 1);
                    reloadCooldown = 100;
                    if (getFirstPassenger() instanceof Player p) {
                        com.atsuishio.superbwarfare.tools.SoundTool.playLocalSound(p, tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get());
                    }
                }
            }
        }
    }

    private void handleLaunchSmoke() {
        if (launchSmokeTicks > 0) {
            launchSmokeTicks--;
            if (level() instanceof ServerLevel serverLevel) {
                Matrix4d transform = getBarrelTransform(1.0f);
                Vector4d pos = transformPosition(transform, 0, 0.5, 2);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 5, 0.8, 4.0, 0.8, 0.1, false);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y, pos.z, 3, 0.6, 4.0, 0.6, 0.08, false);
            }
        }
    }


    // === Стрельба ===
    @Override
    public void vehicleShoot(LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        if (!(living instanceof Player player)) return;
        if (!isFiringMode()) return;
        if (getMissileAmmo() <= 0) return;
        if (fireCooldown > 0) return;
        if (targetPosition == null) return;
        if (getTurretXRot() > MIN_FIRING_PITCH) return;
        if (!isAimAligned()) return;

        actuallyLaunchMissile(player, targetPosition);
        fireCooldown = 20;
    }

    private void actuallyLaunchMissile(Player player, Vec3 target) {
        int currentAmmo = getMissileAmmo();
        if (currentAmmo <= 0) return;

        Matrix4d transform = getBarrelTransform(1.0f);
        double x = shotToggled ? -0.5 : 0.5;
        shotToggled = !shotToggled;
        Vector4d worldPos = transformPosition(transform, x, 0.5, 2.0);

        BallisticMissileEntity missile = new BallisticMissileEntity(player, level());
        missile.setPos(worldPos.x, worldPos.y, worldPos.z);
        missile.setXRot(90);
        missile.setYRot(180);

        // Погрешность 3-6 блоков
        double inaccuracyX = (random.nextDouble() - 0.5) * 2 * (3 + random.nextDouble() * 3);
        double inaccuracyZ = (random.nextDouble() - 0.5) * 2 * (3 + random.nextDouble() * 3);
        Vec3 inaccurateTarget = new Vec3(target.x + inaccuracyX, target.y, target.z + inaccuracyZ);
        missile.setTargetPosition(inaccurateTarget);

        level().addFreshEntity(missile);

        // Эффекты
        if (level() instanceof ServerLevel serverLevel) {
            ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, worldPos.x, worldPos.y, worldPos.z, 100, 2.0, 4.0, 2.0, 0.3, false);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, worldPos.x, worldPos.y, worldPos.z, 80, 1.5, 4.0, 1.5, 0.2, false);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, worldPos.x, worldPos.y, worldPos.z, 60, 1.0, 4.0, 1.0, 0.15, false);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, worldPos.x, worldPos.y, worldPos.z, 40, 1.0, 4.0, 1.0, 0.2, false);
        }

        level().playSound(null, blockPosition(), tech.vvp.vvp.init.ModSounds.TOW_1P.get(), net.minecraft.sounds.SoundSource.PLAYERS, 3.0f, 0.8f);
        ShakeClientMessage.sendToNearbyPlayers(this, 12.0, 8.0, 6.0);
        launchSmokeTicks = 100;
        setMissileAmmo(currentAmmo - 1);
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        if (!isFiringMode()) return false;
        if (!hasGuidanceData()) return false;
        if (getMissileAmmo() <= 0) return false;
        if (getTurretXRot() > MIN_FIRING_PITCH) return false;
        if (!isAimAligned()) return false;
        if (fireCooldown > 0) return false;
        return super.canShoot(living);
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        return getMissileAmmo();
    }

    // === Отключаем звук поворота башни ===
    @Override
    public void turretTurnSound(float diffX, float diffY, float pitch) {
        // Пустой метод - без звука
    }

    // === Свободная камера в driving mode ===
    @Override
    public void onPassengerTurned(net.minecraft.world.entity.Entity entity) {
        // Не блокируем поворот головы пассажира
    }

    protected void clampRotation(net.minecraft.world.entity.Entity entity) {
        // Не ограничиваем поворот
    }

    // === Всегда скрываем руки в технике ===
    @Override
    public boolean banHand(LivingEntity entity) {
        return true;
    }
}
