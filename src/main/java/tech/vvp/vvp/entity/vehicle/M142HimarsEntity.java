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
    private static final EntityDataAccessor<Boolean> FIRING_MODE = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> AMMO = SynchedEntityData.defineId(M142HimarsEntity.class, EntityDataSerializers.INT);

    // Режимы работы
    public enum OperationMode {
        DRIVING,  // Режим движения - можно ездить, башня заблокирована
        FIRING    // Режим стрельбы - техника остановлена, можно наводить башню
    }

    // Автоматическое наведение (для стрельбы через координаты)
    private Vec3 targetPosition = null;
    private boolean isAutoAiming = false;
    private float targetYaw = 0;
    private float targetPitch = 0;

    // Кулдаун между выстрелами
    private int fireCooldown = 0;
    private static final int FIRE_COOLDOWN_TIME = 20; // 1 секунда (20 тиков)

    // Кулдаун перезарядки
    private int reloadCoolDown = 0;
    private static final int RELOAD_TIME = 100; // 5 секунд (100 тиков)

    // Отслеживание клика (не зажатия)
    private boolean wasFirePressed = false;

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
        this.entityData.define(FIRING_MODE, false); // false = DRIVING, true = FIRING
        this.entityData.define(AMMO, 0); // Начальное количество ракет - 0 (максимум 6)
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("PodRot", this.entityData.get(POD_ROT));
        compound.putBoolean("PodToggled", this.entityData.get(POD_TOGGLED));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putBoolean("FiringMode", this.entityData.get(FIRING_MODE));
        compound.putInt("Ammo", this.entityData.get(AMMO));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(POD_ROT, compound.getFloat("PodRot"));
        this.entityData.set(POD_TOGGLED, compound.getBoolean("PodToggled"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(FIRING_MODE, compound.getBoolean("FiringMode"));
        this.entityData.set(AMMO, compound.getInt("Ammo"));
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

    // Методы для работы с режимами
    public boolean isFiringMode() {
        return this.entityData.get(FIRING_MODE);
    }

    public void setFiringMode(boolean firing) {
        this.entityData.set(FIRING_MODE, firing);
    }

    public OperationMode getOperationMode() {
        return isFiringMode() ? OperationMode.FIRING : OperationMode.DRIVING;
    }

    public void toggleMode() {
        boolean currentMode = isFiringMode();
        setFiringMode(!currentMode);

        // При переключении в режим FIRING - останавливаем технику
        if (!currentMode) { // Переключаемся в FIRING
            this.setDeltaMovement(Vec3.ZERO);
        }

        // Отправляем сообщение игроку
        if (!this.level().isClientSide && this.getFirstPassenger() instanceof Player player) {
            String mode = isFiringMode() ? "§aFIRING" : "§6DRIVING";
            player.sendSystemMessage(Component.literal("Mode: " + mode));
        }
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{{
                new BallisticMissileWeapon()
                        .sound(ModSounds.INTO_MISSILE.get())
                        .ammo(tech.vvp.vvp.init.ModItems.GMLRS_M31.get())
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

    private void handleAmmo() {
        // Уменьшаем кулдаун перезарядки
        if (reloadCoolDown > 0) {
            reloadCoolDown--;
        }

        // Проверяем креативный ящик с патронами
        boolean hasCreativeAmmo = false;
        if (this.getFirstPassenger() instanceof Player player && com.atsuishio.superbwarfare.tools.InventoryTool.hasCreativeAmmoBox(player)) {
            hasCreativeAmmo = true;
        }

        if (hasCreativeAmmo) {
            // Креативный режим - бесконечные патроны
            this.entityData.set(AMMO, 6);
        } else {
            // Считаем патроны GMLRS M31 в инвентаре контейнера
            int ammoInContainer = countItem(tech.vvp.vvp.init.ModItems.GMLRS_M31.get());
            int currentAmmo = this.entityData.get(AMMO);

            // Если патронов меньше 6, есть в контейнере И кулдаун закончился - заряжаем
            if (currentAmmo < 6 && ammoInContainer > 0 && reloadCoolDown <= 0) {
                // Заряжаем по одной ракете за раз
                consumeItem(tech.vvp.vvp.init.ModItems.GMLRS_M31.get(), 1);
                this.entityData.set(AMMO, currentAmmo + 1);

                // Устанавливаем задержку перезарядки 5 секунд
                reloadCoolDown = RELOAD_TIME;

                // Звук и сообщение игроку
                if (this.getFirstPassenger() instanceof Player player) {
                    // Воспроизводим звук перезарядки
                    com.atsuishio.superbwarfare.tools.SoundTool.playLocalSound(player, tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get());

                    // Сообщение о зарядке
                    player.sendSystemMessage(Component.literal("§aReloading... " + (currentAmmo + 1) + "/6"));
                }
            }
        }
    }

    @Override
    public void baseTick() {
        // Обработка зарядки патронов
        if (!this.level().isClientSide) {
            handleAmmo();

            // Продолжительный дым после запуска (5 секунд) - 4 блока в высоту
            if (launchSmokeTicks > 0) {
                launchSmokeTicks--;

                // Генерируем дым возле пусковой установки
                Matrix4f transform = this.getBarrelTransform(1.0F);
                Vector4f smokePos = this.transformPosition(transform, 0.0F, 0.5F, 2.0F);

                ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE,
                        smokePos.x, smokePos.y, smokePos.z,
                        5, 0.8, 4.0, 0.8, 0.1, false);

                ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        smokePos.x, smokePos.y, smokePos.z,
                        3, 0.6, 4.0, 0.6, 0.08, false);
            }
        }

        // Обработка переключения режима (клавиша координат)
        // Используем coordinateInputDown для переключения режимов
        // Это будет обрабатываться через клиентский код

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

        // Управление башней в режиме FIRING
        if (isFiringMode() && !isAutoAiming && this.getFirstPassenger() instanceof Player player) {
            // В режиме FIRING башня следует за камерой игрока
            float playerYaw = player.getYHeadRot();
            float playerPitch = player.getXRot();

            // Поворот башни - используем относительный угол к машине
            // Вычитаем yaw машины чтобы получить относительный угол
            float relativeYaw = Mth.wrapDegrees(playerYaw - this.getYRot());
            this.setTurretYRot(relativeYaw);

            // Подъем башни с ограничениями
            // playerPitch: отрицательный = вверх, положительный = вниз
            // Ограничиваем от -60 (вверх) до 0 (горизонт)
            this.setTurretXRot(Mth.clamp(playerPitch, -60F, 0F));

            // Сбрасываем MOUSE_SPEED чтобы не накапливалось
            this.entityData.set(MOUSE_SPEED_X, 0f);
            this.entityData.set(MOUSE_SPEED_Y, 0f);
        } else if (!isFiringMode()) {
            // В режиме DRIVING башня заблокирована
            // Yaw = 0 (относительно машины), Pitch = 0 (горизонтально)
            this.setTurretYRot(0F);
            this.setTurretXRot(0F);

            // Сбрасываем MOUSE_SPEED
            this.entityData.set(MOUSE_SPEED_X, 0f);
            this.entityData.set(MOUSE_SPEED_Y, 0f);
        }

        // Сохраняем значения башни ПОСЛЕ автонаведения
        this.turretYRotO = this.getTurretYRot();
        this.turretXRotO = this.getTurretXRot();

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

        // Уменьшаем кулдаун стрельбы
        if (fireCooldown > 0) {
            fireCooldown--;
        }

        // Обработка стрельбы ЛКМ (только по клику, не при зажатии)
        if (this.getFirstPassenger() instanceof Player player) {
            // Стреляем только когда кнопка только что нажата (не была нажата в прошлом тике)
            if (fireInputDown && !wasFirePressed && fireCooldown == 0) {
                vehicleShoot(player, 0);
            }
            // Запоминаем состояние кнопки
            wasFirePressed = fireInputDown;
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

        // Проверяем что мы в режиме FIRING
        if (!isFiringMode()) {
            player.sendSystemMessage(Component.literal("§cCannot fire in DRIVING mode! Switch to FIRING mode first."));
            return;
        }

        // Стреляем сразу без задержки
        actuallyLaunchMissile(player, targetPos);
    }

    // Счетчик для дыма после запуска
    private int launchSmokeTicks = 0;

    // Фактический запуск ракеты (вызывается после задержки)
    private void actuallyLaunchMissile(Player player, Vec3 targetPos) {
        // Запускаем ракету
        Matrix4f transform = this.getBarrelTransform(1.0F);
        float x = shotToggled ? -0.5F : 0.5F; // Левая/правая сторона установки
        float y = 0.5F; // Чуть выше
        float z = 2.0F; // ВПЕРЕДИ башни (положительное значение)
        this.shotToggled = !this.shotToggled;

        Vector4f worldPosition = this.transformPosition(transform, x, y, z);

        BallisticMissileEntity missile = ((BallisticMissileWeapon) this.getWeapon(0)).create(player);
        missile.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        missile.setXRot(90);
        missile.setYRot(180);
        missile.setTargetPosition(targetPos);

        this.level().addFreshEntity(missile);

        // ОГРОМНОЕ ОБЛАКО ДЫМА при запуске (4 блока в высоту)
        ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE,
                worldPosition.x, worldPosition.y, worldPosition.z,
                100, 2.0, 4.0, 2.0, 0.3, false);

        ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.CAMPFIRE_COSY_SMOKE,
                worldPosition.x, worldPosition.y, worldPosition.z,
                80, 1.5, 4.0, 1.5, 0.2, false);

        ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.SMOKE,
                worldPosition.x, worldPosition.y, worldPosition.z,
                60, 1.0, 4.0, 1.0, 0.15, false);

        ParticleTool.sendParticle((ServerLevel) this.level(), ParticleTypes.FLAME,
                worldPosition.x, worldPosition.y, worldPosition.z,
                40, 1.0, 4.0, 1.0, 0.2, false);

        // Запускаем таймер для продолжительного дыма (5 секунд = 100 тиков)
        launchSmokeTicks = 100;

        // Звук запуска ракеты (без звука поворота башни)
        this.level().playSound(null, this.blockPosition(), tech.vvp.vvp.init.ModSounds.TOW_1P.get(), net.minecraft.sounds.SoundSource.PLAYERS, 3.0F, 0.8F);
        ShakeClientMessage.sendToNearbyPlayers(this, 6.0, 8.0, 6.0, 12.0);

        this.entityData.set(CANNON_RECOIL_TIME, 60);
        this.entityData.set(FIRE_ANIM, 3);

        // Отправляем сообщение игроку - targetPos это уже правильные координаты куда летит ракета
        player.sendSystemMessage(Component.literal("§aMissile launched to: X=" + (int)targetPos.x + " Y=" + (int)targetPos.y + " Z=" + (int)targetPos.z));
    }

    @Override
    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.09 || Mth.abs(this.entityData.get(POWER)) > 0.15;
    }

    @Override
    public void vehicleShoot(LivingEntity living, int type) {
        if (!(living instanceof Player player)) return;

        // Проверяем режим
        if (!isFiringMode()) {
            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal("§cCannot fire in DRIVING mode! Switch to FIRING mode first."));
            }
            return;
        }

        // Проверяем наличие патронов
        int currentAmmo = this.entityData.get(AMMO);
        if (currentAmmo <= 0) {
            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal("§cNo missiles left! Ammo: 0/6"));
            }
            return;
        }

        // Вычисляем направление башни
        float turretYaw = getTurretYRot(); // Относительный yaw башни
        float pitch = getTurretXRot(); // Pitch башни

        // Абсолютный yaw = yaw машины + относительный yaw башни
        float absoluteYaw = this.getYRot() + turretYaw;

        // Конвертируем углы в радианы
        float yawRad = (float) Math.toRadians(absoluteYaw);
        float pitchRad = (float) Math.toRadians(pitch);

        // Вычисляем направление (pitch отрицательный = вверх)
        double dx = -Math.sin(yawRad) * Math.cos(pitchRad);
        double dy = -Math.sin(pitchRad); // Отрицательный pitch = вверх
        double dz = Math.cos(yawRad) * Math.cos(pitchRad);

        Vec3 direction = new Vec3(dx, dy, dz).normalize();

        // Получаем позицию ствола
        Matrix4f transform = this.getBarrelTransform(1.0F);
        Vector4f barrelPos = this.transformPosition(transform, 0.0F, 0.0F, 2.0F);
        Vec3 startPos = new Vec3(barrelPos.x, barrelPos.y, barrelPos.z);

        // Делаем raycast чтобы найти точку попадания
        Vec3 endPos = startPos.add(direction.scale(1000)); // Максимум 1000 блоков
        net.minecraft.world.phys.BlockHitResult hitResult = this.level().clip(
                new net.minecraft.world.level.ClipContext(
                        startPos,
                        endPos,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        this
                )
        );

        // Используем точку попадания или конечную точку если ничего не попали
        Vec3 targetPos = hitResult.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK
                ? hitResult.getLocation()
                : endPos;

        shootMissileTo(player, targetPos);

        // Тратим патрон
        if (!this.level().isClientSide) {
            this.entityData.set(AMMO, currentAmmo - 1);
            player.sendSystemMessage(Component.literal("§aMissile fired! Ammo: " + (currentAmmo - 1) + "/6"));
        }

        // Устанавливаем кулдаун после выстрела
        fireCooldown = FIRE_COOLDOWN_TIME;
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

            // В режиме FIRING блокируем движение
            if (isFiringMode()) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.sprintInputDown = false;
                this.entityData.set(POWER, 0.0F);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1.0, 0.8)); // Плавная остановка
            }

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
    public int getAmmoCount(LivingEntity living) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        return this.entityData.get(AMMO) > 0 && isFiringMode() && !this.cannotFire && getPodToggled();
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
        return tech.vvp.vvp.VVP.loc("textures/gui/vehicle/type/land.png");
    }

    // Отключаем звук поворота башни
    @Override
    public void turretTurnSound(float diffX, float diffY, float pitch) {
        // Пустой метод - не воспроизводим звук поворота башни
    }
}