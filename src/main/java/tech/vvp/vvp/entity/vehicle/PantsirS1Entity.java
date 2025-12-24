package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.PantsirRadarSyncMessage;

import tech.vvp.vvp.entity.projectile.PantsirMissileEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Pantsir-S1 ЗРК - требует захват цели для стрельбы ракетами
 */
public class PantsirS1Entity extends CamoVehicleBase {

    // Синхронизированные данные для автонаведения башни
    private static final EntityDataAccessor<Boolean> AUTO_AIM_ACTIVE = SynchedEntityData.defineId(PantsirS1Entity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> AIM_DIR_X = SynchedEntityData.defineId(PantsirS1Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> AIM_DIR_Y = SynchedEntityData.defineId(PantsirS1Entity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> AIM_DIR_Z = SynchedEntityData.defineId(PantsirS1Entity.class, EntityDataSerializers.FLOAT);

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/pantsir_s1.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_haki.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo2.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo3.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo4.png")
    };
    
    private static final String[] CAMO_NAMES = {"Standard", "Haki", "Camo2", "Camo3", "Camo4"};
    
    // Параметры радара
    private static final double RADAR_RANGE = 800.0;           // Дальность обзорного радара
    private static final double RADAR_TRACK_RANGE = 700.0;     // Дальность сопровождения
    private static final int LOCK_TIME_TICKS = 40;             // Время захвата (2 секунды)
    
    // Параметры вращающегося радара (синхронизация с анимацией)
    private static final float RADAR_ROTATION_TICKS = 42.5f;   // Тиков на полный оборот
    private static final float RADAR_BEAM_WIDTH = 30.0f;       // Ширина луча обзорного радара
    
    // Состояние радара (серверная сторона)
    private int radarState = PantsirRadarSyncMessage.STATE_IDLE;
    private Entity trackedTarget = null;
    private Entity lockedTarget = null;
    private int lockingProgress = 0;
    private int syncTimer = 0;
    private float radarRotationTicks = 0;  // Угол вращения обзорного радара
    
    // Список всех обнаруженных целей (для отображения на радаре)
    private final List<Entity> detectedTargets = new ArrayList<>();
    private int targetScanTimer = 0;
    
    // Список выпущенных ракет (для отображения на радаре)
    private final List<PantsirMissileEntity> activeMissiles = new ArrayList<>();

    public PantsirS1Entity(EntityType<PantsirS1Entity> type, Level world) {
        super(type, world);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AUTO_AIM_ACTIVE, false);
        this.entityData.define(AIM_DIR_X, 0f);
        this.entityData.define(AIM_DIR_Y, 0f);
        this.entityData.define(AIM_DIR_Z, 1f); // По умолчанию смотрим вперёд
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }
    
    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }
    
    /**
     * Переопределяем управление башней - при захвате цели используем автонаведение
     * Работает и на сервере и на клиенте через синхронизированные данные
     */
    @Override
    public void adjustTurretAngle() {
        // Проверяем синхронизированный флаг автонаведения
        if (this.entityData.get(AUTO_AIM_ACTIVE)) {
            // Получаем вектор направления стрельбы из синхронизированных данных
            Vec3 aimDirection = new Vec3(
                this.entityData.get(AIM_DIR_X),
                this.entityData.get(AIM_DIR_Y),
                this.entityData.get(AIM_DIR_Z)
            );
            
            // Передаём вектор направления напрямую в turretAutoAimFromVector
            if (aimDirection.lengthSqr() > 0.001) {
                this.turretAutoAimFromVector(aimDirection);
            }
            return;
        }
        
        // Иначе - стандартное ручное управление
        super.adjustTurretAngle();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        if (!this.level().isClientSide) {
            tickRadar();
        }
    }
    
    /**
     * Серверная логика радара
     */
    private void tickRadar() {
        // Вращаем обзорный радар ВСЕГДА (даже без оператора)
        radarRotationTicks += 1.0f;
        if (radarRotationTicks >= RADAR_ROTATION_TICKS) {
            radarRotationTicks -= RADAR_ROTATION_TICKS;
        }
        
        LivingEntity operator = getOperator();
        if (operator == null) {
            resetRadar();
            // Синхронизируем угол радара всем игрокам рядом даже без оператора
            syncRadarAngleToNearbyPlayers();
            return;
        }
        
        // Сканируем цели каждые 5 тиков
        targetScanTimer++;
        if (targetScanTimer >= 5) {
            targetScanTimer = 0;
            scanForTargets();
            scanForMissiles();
        }
        
        // Обновляем состояние радара
        updateRadarState(operator);
        
        // Синхронизируем с клиентом каждые 2 тика
        syncTimer++;
        if (syncTimer >= 2) {
            syncTimer = 0;
            syncRadarToClient(operator);
        }
    }
    
    /**
     * Синхронизирует угол радара всем игрокам рядом (когда нет оператора)
     */
    private void syncRadarAngleToNearbyPlayers() {
        syncTimer++;
        if (syncTimer < 5) return;
        syncTimer = 0;
        
        float radarAngle = getRadarAngle();
        
        // Угол башни из вектора направления ствола
        // atan2(x, z) даёт угол где 0=юг, 90=восток, но Minecraft yaw: 0=юг, -90=восток
        // Поэтому инвертируем знак
        Vec3 barrelDir = this.getBarrelVector(1.0f);
        float turretAngle = (float) Math.toDegrees(-Math.atan2(barrelDir.x, barrelDir.z));
        
        // Пустое сообщение только с углами радара и башни
        PantsirRadarSyncMessage message = new PantsirRadarSyncMessage(
            this.getId(), PantsirRadarSyncMessage.STATE_IDLE, -1, 0, 0, 0, 
            0, 0, 0, // скорость цели
            0, 0, radarAngle, turretAngle,
            new int[0], new double[0], new double[0], new double[0],
            new double[0], new double[0], new double[0]
        );
        
        // Отправляем всем игрокам в радиусе 100 блоков
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(100), e -> e instanceof ServerPlayer)) {
            if (entity instanceof ServerPlayer player) {
                VVPNetwork.VVP_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), message);
            }
        }
    }
    
    /**
     * Сканирует все цели в радиусе радара
     */
    private void scanForTargets() {
        Vec3 radarPos = this.position().add(0, 2.5, 0);
        detectedTargets.clear();
        
        // Ищем все entity в радиусе
        AABB searchBox = new AABB(radarPos, radarPos).inflate(RADAR_RANGE);
        List<Entity> entities = this.level().getEntities(this, searchBox, this::isValidRadarTarget);
        
        detectedTargets.addAll(entities);
    }
    
    /**
     * Сканирует выпущенные ракеты этого панциря
     */
    private void scanForMissiles() {
        Vec3 radarPos = this.position().add(0, 2.5, 0);
        activeMissiles.clear();
        
        // Ищем ракеты в радиусе радара
        AABB searchBox = new AABB(radarPos, radarPos).inflate(RADAR_RANGE);
        List<PantsirMissileEntity> missiles = this.level().getEntitiesOfClass(
            PantsirMissileEntity.class, searchBox, 
            m -> m.getLauncherId() == this.getId() && m.isAlive()
        );
        
        activeMissiles.addAll(missiles);
    }
    
    /**
     * Проверяет является ли entity валидной целью для радара
     * Только игроки и техника (VehicleEntity)
     */
    private boolean isValidRadarTarget(Entity entity) {
        if (entity == null || !entity.isAlive()) return false;
        if (entity == this) return false;
        
        // Не считаем пассажиров этой машины
        if (this.hasPassenger(entity)) return false;
        
        Vec3 radarPos = this.position().add(0, 2.5, 0);
        double distance = entity.position().distanceTo(radarPos);
        if (distance > RADAR_RANGE) return false;
        
        // VehicleEntity из SBW/VVP - проверяем по типу техники
        if (entity instanceof VehicleEntity vehicle) {
            VehicleType vehicleType = vehicle.getVehicleType();
            
            // Вертолёты и самолёты - ВСЕГДА показываем (это воздушные цели)
            if (vehicleType == VehicleType.HELICOPTER || vehicleType == VehicleType.AIRPLANE) {
                return true;
            }
            
            // Остальная техника - только если в воздухе (прыгает, падает и т.д.)
            double heightAboveRadar = vehicle.position().y - radarPos.y;
            return !vehicle.onGround() && heightAboveRadar > 3.0;
        }
        
        // Игроков НЕ показываем на радаре (нельзя залочить)
        if (entity instanceof Player) {
            return false;
        }
        
        // Мобов НЕ показываем (летучие мыши, пчёлы и т.д. - пофиг на них)
        return false;
    }
    
    /**
     * Проигрывает звук предупреждения о локе для цели
     * @param target цель
     * @param locked true = залочен, false = идёт захват
     */
    private void playLockWarningSound(Entity target, boolean locked) {
        if (target == null) return;
        
        // Проигрываем звук только если у цели есть пассажиры или это VehicleEntity
        if (!target.getPassengers().isEmpty() || target instanceof VehicleEntity) {
            target.level().playSound(null, target.getOnPos(), 
                target instanceof Pig ? SoundEvents.PIG_HURT : 
                    (locked ? ModSounds.LOCKED_WARNING.get() : ModSounds.LOCKING_WARNING.get()), 
                SoundSource.PLAYERS, 2, 1f);
        }
    }
    
    /**
     * Получает оператора оружия (игрока на сидушке 1 - с пушкой и ракетами)
     * TurretControllerIndex: 1 в JSON означает что оператор управляет башней
     */
    @Nullable
    private LivingEntity getOperator() {
        List<Entity> passengers = this.getPassengers();
        if (passengers.isEmpty()) return null;
        
        // Ищем игрока который управляет башней (seatIndex == 1)
        for (Entity passenger : passengers) {
            if (passenger instanceof LivingEntity living) {
                int seatIndex = this.getSeatIndex(living);
                if (seatIndex == 1) {
                    return living;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Обновляет состояние радара
     * Захват работает на любую цель в радиусе радара (не требует ССЦ)
     */
    private void updateRadarState(LivingEntity operator) {
        Vec3 radarPos = this.position().add(0, 2.5, 0);
        
        switch (radarState) {
            case PantsirRadarSyncMessage.STATE_IDLE:
                // Ищем ближайшую цель
                trackedTarget = findClosestTarget(radarPos);
                disableAutoAim();
                if (trackedTarget != null) {
                    radarState = PantsirRadarSyncMessage.STATE_DETECTED;
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_DETECTED:
                // Цель обнаружена, ждём команды захвата
                disableAutoAim();
                if (!isTargetValidForTracking(trackedTarget, radarPos)) {
                    trackedTarget = findClosestTarget(radarPos);
                    if (trackedTarget == null) {
                        radarState = PantsirRadarSyncMessage.STATE_IDLE;
                    }
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_LOCKING:
                // Идёт захват - цель должна быть в радиусе радара
                if (!isTargetValidForTracking(trackedTarget, radarPos)) {
                    // Цель потеряна (вышла из радиуса или уничтожена)
                    lockingProgress = 0;
                    disableAutoAim();
                    radarState = PantsirRadarSyncMessage.STATE_LOST;
                } else {
                    // Проверяем наличие decoy/flare рядом с целью - они могут перехватить лок
                    Entity decoyNearTarget = findDecoyNearTarget(trackedTarget, 50.0);
                    if (decoyNearTarget != null) {
                        // Флаер перехватил лок! Переключаемся на него
                        trackedTarget = decoyNearTarget;
                    }
                    
                    // Продолжаем захват (без автонаведения - ручное управление)
                    lockingProgress++;
                    disableAutoAim();
                    
                    // Оповещаем цель о том что её лочат (каждые 3 тика)
                    if (lockingProgress % 3 == 0) {
                        playLockWarningSound(trackedTarget, false);
                    }
                    
                    if (lockingProgress >= LOCK_TIME_TICKS) {
                        lockedTarget = trackedTarget;
                        radarState = PantsirRadarSyncMessage.STATE_LOCKED;
                    }
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_LOCKED:
                // Цель захвачена - должна оставаться в радиусе
                if (!isTargetValidForLock(lockedTarget, radarPos)) {
                    // Цель потеряна (вышла из радиуса или уничтожена)
                    lockedTarget = null;
                    disableAutoAim();
                    radarState = PantsirRadarSyncMessage.STATE_LOST;
                } else {
                    // Автонаведение башни на захваченную цель
                    updateAutoAimTarget(lockedTarget);
                    
                    // Оповещаем цель о том что она залочена (каждые 2 тика)
                    if (this.tickCount % 2 == 0) {
                        playLockWarningSound(lockedTarget, true);
                    }
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_LOST:
                // Захват потерян - пауза перед возвратом в IDLE
                disableAutoAim();
                lockingProgress++;
                if (lockingProgress > 20) {
                    lockingProgress = 0;
                    radarState = PantsirRadarSyncMessage.STATE_IDLE;
                }
                break;
        }
    }
    
    /**
     * Обновляет синхронизированные данные для автонаведения башни (только на сервере)
     * Использует упреждение для движущихся целей
     */
    private void updateAutoAimTarget(Entity target) {
        if (target == null) {
            this.entityData.set(AUTO_AIM_ACTIVE, false);
            return;
        }
        
        // Получаем оператора для определения параметров оружия
        LivingEntity operator = getOperator();
        if (operator == null) {
            this.entityData.set(AUTO_AIM_ACTIVE, false);
            return;
        }
        
        // Позиция цели (центр)
        Vec3 targetPos = target.getBoundingBox().getCenter();
        
        // Позиция откуда стреляем
        Vec3 shootPos = this.getShootPos(operator, 1);
        
        // Проверяем какое оружие выбрано (0 = пушка, 1 = ракеты)
        int seatIndex = this.getSeatIndex(operator);
        int weaponIndex = this.getSelectedWeapon(seatIndex);
        
        Vec3 aimVector;
        
        if (weaponIndex == 1) {
            // Ракеты - целимся прямо на цель (ракеты сами наводятся)
            aimVector = targetPos.subtract(shootPos).normalize();
        } else {
            // Пушка - используем упреждение с учётом скорости и гравитации
            Vec3 targetVel = target.getDeltaMovement();
            
            // Получаем реальные параметры пушки
            float velocity = this.getProjectileVelocity(operator);
            float gravity = this.getProjectileGravity(operator);
            
            // Используем RangeTool.calculateFiringSolution для правильного упреждения
            aimVector = com.atsuishio.superbwarfare.tools.RangeTool.calculateFiringSolution(
                shootPos, targetPos, targetVel, velocity, gravity
            );
            
            // Если calculateFiringSolution вернул null - используем прямое направление
            if (aimVector == null || aimVector.lengthSqr() < 0.001) {
                aimVector = targetPos.subtract(shootPos).normalize();
            }
        }
        
        // Вычисляем точку куда целиться (для синхронизации)
        // Нормализуем вектор направления
        aimVector = aimVector.normalize();
        
        // Обновляем синхронизированные данные (вектор направления)
        this.entityData.set(AUTO_AIM_ACTIVE, true);
        this.entityData.set(AIM_DIR_X, (float) aimVector.x);
        this.entityData.set(AIM_DIR_Y, (float) aimVector.y);
        this.entityData.set(AIM_DIR_Z, (float) aimVector.z);
    }
    
    /**
     * Отключает автонаведение башни
     */
    private void disableAutoAim() {
        this.entityData.set(AUTO_AIM_ACTIVE, false);
    }
    
    /**
     * Возвращает текущий угол обзорного радара в градусах
     */
    public float getRadarAngle() {
        return -(radarRotationTicks / RADAR_ROTATION_TICKS) * 360.0f;
    }
    
    /**
     * Ищет ближайшую цель из обнаруженных
     */
    @Nullable
    private Entity findClosestTarget(Vec3 radarPos) {
        return detectedTargets.stream()
                .filter(e -> e.isAlive())
                .min(Comparator.comparingDouble(e -> e.position().distanceTo(radarPos)))
                .orElse(null);
    }
    
    /**
     * Проверяет валидность цели для отслеживания
     */
    private boolean isTargetValidForTracking(Entity target, Vec3 radarPos) {
        if (target == null || !target.isAlive()) return false;
        double distance = target.position().distanceTo(radarPos);
        return distance <= RADAR_RANGE;
    }
    
    /**
     * Проверяет валидность цели для удержания захвата (более мягкие условия)
     */
    private boolean isTargetValidForLock(Entity target, Vec3 radarPos) {
        if (target == null || !target.isAlive()) return false;
        
        double distance = target.position().distanceTo(radarPos);
        return distance <= RADAR_TRACK_RANGE * 1.2; // Немного больший радиус для удержания
    }
    
    /**
     * Ищет decoy/flare рядом с целью который может перехватить лок
     * @param target текущая цель
     * @param radius радиус поиска вокруг цели
     * @return decoy entity или null
     */
    @Nullable
    private Entity findDecoyNearTarget(Entity target, double radius) {
        if (target == null) return null;
        
        Vec3 targetPos = target.position();
        AABB searchBox = new AABB(targetPos, targetPos).inflate(radius);
        
        List<Entity> entities = this.level().getEntities(target, searchBox, e -> {
            if (e == null || !e.isAlive()) return false;
            // Проверяем что это decoy (флаер)
            return e.getType().is(ModTags.EntityTypes.DECOY);
        });
        
        // Возвращаем ближайший decoy к цели
        return entities.stream()
                .min(Comparator.comparingDouble(e -> e.position().distanceTo(targetPos)))
                .orElse(null);
    }
    
    /**
     * Синхронизирует состояние радара с клиентом
     * Передаёт: состояние, координаты цели, скорость цели, угол обзорного радара, угол башни, список всех целей, ракеты
     */
    private void syncRadarToClient(LivingEntity operator) {
        if (!(operator instanceof ServerPlayer serverPlayer)) return;
        
        // Цель для отображения (tracked или locked)
        Entity radarTarget = (radarState == PantsirRadarSyncMessage.STATE_LOCKED) ? lockedTarget : trackedTarget;
        
        int targetId = (radarTarget != null) ? radarTarget.getId() : -1;
        
        // Координаты цели
        double targetX = (radarTarget != null) ? radarTarget.getX() : 0;
        double targetY = (radarTarget != null) ? radarTarget.getY() + radarTarget.getBbHeight() / 2 : 0;
        double targetZ = (radarTarget != null) ? radarTarget.getZ() : 0;
        
        // Скорость цели (блоков/тик)
        double targetVelX = (radarTarget != null) ? radarTarget.getDeltaMovement().x : 0;
        double targetVelY = (radarTarget != null) ? radarTarget.getDeltaMovement().y : 0;
        double targetVelZ = (radarTarget != null) ? radarTarget.getDeltaMovement().z : 0;
        
        int progress = (radarState == PantsirRadarSyncMessage.STATE_LOCKING) 
                ? (lockingProgress * 100 / LOCK_TIME_TICKS) : 0;
        double distance = (radarTarget != null) ? radarTarget.position().distanceTo(operator.position()) : 0;
        
        // Угол обзорного радара (абсолютный, для кости модели и GUI)
        float radarAngle = getRadarAngle();
        
        // Угол башни (пассивный радар) - вычисляем из вектора направления ствола
        Vec3 barrelDir = this.getBarrelVector(1.0f);
        float turretAngle = (float) Math.toDegrees(-Math.atan2(barrelDir.x, barrelDir.z));
        
        // Собираем все обнаруженные цели для отображения на радаре
        int[] targetIds = new int[Math.min(detectedTargets.size(), 16)]; // Максимум 16 целей
        double[] targetXs = new double[targetIds.length];
        double[] targetYs = new double[targetIds.length];
        double[] targetZs = new double[targetIds.length];
        
        for (int i = 0; i < targetIds.length; i++) {
            Entity target = detectedTargets.get(i);
            targetIds[i] = target.getId();
            targetXs[i] = target.getX();
            targetYs[i] = target.getY() + target.getBbHeight() / 2;
            targetZs[i] = target.getZ();
        }
        
        // Собираем позиции ракет
        int missileCount = Math.min(activeMissiles.size(), 8); // Максимум 8 ракет
        double[] missileX = new double[missileCount];
        double[] missileY = new double[missileCount];
        double[] missileZ = new double[missileCount];
        
        for (int i = 0; i < missileCount; i++) {
            PantsirMissileEntity missile = activeMissiles.get(i);
            missileX[i] = missile.getX();
            missileY[i] = missile.getY();
            missileZ[i] = missile.getZ();
        }
        
        PantsirRadarSyncMessage message = new PantsirRadarSyncMessage(
            this.getId(), radarState, targetId, targetX, targetY, targetZ, 
            targetVelX, targetVelY, targetVelZ,
            progress, distance, radarAngle, turretAngle,
            targetIds, targetXs, targetYs, targetZs, missileX, missileY, missileZ
        );
        
        VVPNetwork.VVP_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
    }
    
    /**
     * Сбрасывает состояние радара
     */
    private void resetRadar() {
        radarState = PantsirRadarSyncMessage.STATE_IDLE;
        trackedTarget = null;
        lockedTarget = null;
        lockingProgress = 0;
        disableAutoAim();
        // radarRotationTicks продолжает вращаться даже без оператора
    }
    
    /**
     * Вызывается клиентом для запроса захвата цели
     */
    public void requestLock(LivingEntity operator) {
        if (radarState == PantsirRadarSyncMessage.STATE_DETECTED && trackedTarget != null) {
            radarState = PantsirRadarSyncMessage.STATE_LOCKING;
            lockingProgress = 0;
        }
    }
    
    /**
     * Вызывается клиентом для отмены захвата
     */
    public void cancelLock(LivingEntity operator) {
        if (radarState == PantsirRadarSyncMessage.STATE_LOCKING || 
            radarState == PantsirRadarSyncMessage.STATE_LOCKED) {
            lockedTarget = null;
            lockingProgress = 0;
            radarState = PantsirRadarSyncMessage.STATE_DETECTED;
        }
    }
    
    /**
     * Выбирает следующую цель из списка обнаруженных
     */
    public void selectNextTarget() {
        if (detectedTargets.isEmpty()) return;
        if (radarState == PantsirRadarSyncMessage.STATE_LOCKING || 
            radarState == PantsirRadarSyncMessage.STATE_LOCKED) return;
        
        int currentIndex = -1;
        if (trackedTarget != null) {
            currentIndex = detectedTargets.indexOf(trackedTarget);
        }
        
        int nextIndex = (currentIndex + 1) % detectedTargets.size();
        trackedTarget = detectedTargets.get(nextIndex);
        
        if (radarState == PantsirRadarSyncMessage.STATE_IDLE) {
            radarState = PantsirRadarSyncMessage.STATE_DETECTED;
        }
    }
    
    /**
     * Выбирает предыдущую цель из списка обнаруженных
     */
    public void selectPrevTarget() {
        if (detectedTargets.isEmpty()) return;
        if (radarState == PantsirRadarSyncMessage.STATE_LOCKING || 
            radarState == PantsirRadarSyncMessage.STATE_LOCKED) return;
        
        int currentIndex = -1;
        if (trackedTarget != null) {
            currentIndex = detectedTargets.indexOf(trackedTarget);
        }
        
        int prevIndex = currentIndex - 1;
        if (prevIndex < 0) prevIndex = detectedTargets.size() - 1;
        trackedTarget = detectedTargets.get(prevIndex);
        
        if (radarState == PantsirRadarSyncMessage.STATE_IDLE) {
            radarState = PantsirRadarSyncMessage.STATE_DETECTED;
        }
    }
    
    /**
     * Возвращает захваченную цель (для стрельбы ракетами)
     */
    @Nullable
    public Entity getLockedTarget() {
        return lockedTarget;
    }
    
    /**
     * Проверяет захвачена ли цель
     */
    public boolean hasLockedTarget() {
        return radarState == PantsirRadarSyncMessage.STATE_LOCKED && lockedTarget != null;
    }

    /**
     * Переопределяем canShoot для блокировки стрельбы ракетами без захвата цели на клиенте
     * Это предотвращает проигрывание звука стрельбы когда цель не захвачена
     */
    @Override
    public boolean canShoot(LivingEntity living) {
        int seatIndex = getSeatIndex(living);
        int weaponIndex = getSelectedWeapon(seatIndex);
        
        // Пушка (индекс 0) - всегда можно стрелять
        if (weaponIndex == 0) {
            return super.canShoot(living);
        }
        
        // Ракеты (индекс 1) - требуется захват цели
        if (weaponIndex == 1) {
            // На клиенте проверяем статус захвата через ClientEventHandler
            if (this.level().isClientSide) {
                return isTargetLocked() && super.canShoot(living);
            }
            // На сервере проверяем наличие захваченной цели
            return hasLockedTarget() && super.canShoot(living);
        }
        
        return super.canShoot(living);
    }
    
    /**
     * Проверяет захвачена ли цель на клиенте
     */
    @OnlyIn(Dist.CLIENT)
    private boolean isTargetLocked() {
        // Используем наш PantsirClientHandler для проверки статуса захвата
        return tech.vvp.vvp.client.PantsirClientHandler.isTargetLocked();
    }

    /**
     * Переопределяем vehicleShoot для блокировки стрельбы ракетами без захвата цели на сервере
     * Индекс оружия 0 = пушка (всегда можно стрелять)
     * Индекс оружия 1 = ракеты (требуется захват цели - uuid != null)
     */
    @Override
    public void vehicleShoot(@Nullable LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        if (living == null) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        
        int seatIndex = getSeatIndex(living);
        int weaponIndex = getSelectedWeapon(seatIndex);
        
        // Пушка (индекс 0) - всегда можно стрелять
        if (weaponIndex == 0) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        
        // Ракеты (индекс 1) - требуется захват цели
        if (weaponIndex == 1) {
            // Используем захваченную цель из радара
            if (hasLockedTarget()) {
                UUID targetUuid = lockedTarget.getUUID();
                Vec3 targetPosition = lockedTarget.position().add(0, lockedTarget.getBbHeight() / 2, 0);
                super.vehicleShoot(living, targetUuid, targetPosition);
            }
            // Если цель не захвачена - не стреляем
            return;
        }
        
        // Для других оружий - стандартное поведение
        super.vehicleShoot(living, uuid, targetPos);
    }
}
