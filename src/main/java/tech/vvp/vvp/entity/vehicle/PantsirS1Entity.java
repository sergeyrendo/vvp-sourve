package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/pantsir_s1.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_haki.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo2.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo3.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo4.png")
    };
    
    private static final String[] CAMO_NAMES = {"Standard", "Haki", "Camo2", "Camo3", "Camo4"};
    
    // Параметры радара
    private static final double RADAR_RANGE = 700.0;           // Дальность обзорного радара
    private static final double RADAR_TRACK_RANGE = 600.0;     // Дальность сопровождения
    private static final double SSC_HALF_ANGLE = 3.0;          // ССЦ - полуугол сектора (±3° = 6° всего)
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
            this.getId(), PantsirRadarSyncMessage.STATE_IDLE, -1, 0, 0, 0, 0, 0, radarAngle, turretAngle,
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
        
        // Летающие мобы (драконы, фантомы, пчёлы и т.д.)
        if (entity instanceof Mob mob) {
            return !mob.onGround() || mob.position().y > radarPos.y + 5;
        }
        
        // LivingEntity в воздухе (игроки с элитрой и т.д.)
        if (entity instanceof LivingEntity living) {
            return !living.onGround() && living.position().y > radarPos.y + 3;
        }
        
        return false;
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
     * Два режима:
     * 1. Обзорный радар (вращающийся) - обнаруживает цели
     * 2. ССЦ (Станция Сопровождения Целей) - узкий сектор ±3° от башни для захвата
     */
    private void updateRadarState(LivingEntity operator) {
        Vec3 radarPos = this.position().add(0, 2.5, 0);
        Vec3 turretDirection = getBarrelVector(1.0f);  // Направление башни
        
        switch (radarState) {
            case PantsirRadarSyncMessage.STATE_IDLE:
                // Ищем ближайшую цель
                trackedTarget = findClosestTarget(radarPos);
                if (trackedTarget != null) {
                    radarState = PantsirRadarSyncMessage.STATE_DETECTED;
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_DETECTED:
                // Цель обнаружена, ждём команды захвата
                if (!isTargetValidForTracking(trackedTarget, radarPos)) {
                    trackedTarget = findClosestTarget(radarPos);
                    if (trackedTarget == null) {
                        radarState = PantsirRadarSyncMessage.STATE_IDLE;
                    }
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_LOCKING:
                // Идёт захват - цель ДОЛЖНА быть в секторе ССЦ (±3°)
                if (!isTargetValidForTracking(trackedTarget, radarPos)) {
                    // Цель потеряна (вышла из радиуса или уничтожена)
                    lockingProgress = 0;
                    radarState = PantsirRadarSyncMessage.STATE_LOST;
                } else if (!isTargetInSSC(trackedTarget, radarPos, turretDirection)) {
                    // Цель вышла из сектора ССЦ - захват прерван
                    lockingProgress = 0;
                    radarState = PantsirRadarSyncMessage.STATE_LOST;
                } else {
                    // Цель в секторе ССЦ - продолжаем захват
                    lockingProgress++;
                    
                    if (lockingProgress >= LOCK_TIME_TICKS) {
                        lockedTarget = trackedTarget;
                        radarState = PantsirRadarSyncMessage.STATE_LOCKED;
                    }
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_LOCKED:
                // Цель захвачена - должна оставаться в секторе ССЦ
                if (!isTargetValidForLock(lockedTarget, radarPos)) {
                    // Цель потеряна
                    lockedTarget = null;
                    radarState = PantsirRadarSyncMessage.STATE_LOST;
                } else if (!isTargetInSSC(lockedTarget, radarPos, turretDirection)) {
                    // Цель вышла из сектора ССЦ - захват потерян
                    lockedTarget = null;
                    radarState = PantsirRadarSyncMessage.STATE_LOST;
                }
                break;
                
            case PantsirRadarSyncMessage.STATE_LOST:
                // Захват потерян - пауза перед возвратом в IDLE
                lockingProgress++;
                if (lockingProgress > 20) {
                    lockingProgress = 0;
                    radarState = PantsirRadarSyncMessage.STATE_IDLE;
                }
                break;
        }
    }
    
    /**
     * Проверяет находится ли цель в секторе ССЦ (±3° от направления башни)
     * ССЦ = Станция Сопровождения Целей
     */
    private boolean isTargetInSSC(Entity target, Vec3 radarPos, Vec3 turretDirection) {
        if (target == null) return false;
        
        Vec3 toTarget = target.position().add(0, target.getBbHeight() / 2, 0).subtract(radarPos).normalize();
        
        // Вычисляем угол между направлением башни и направлением на цель
        double dot = turretDirection.dot(toTarget);
        // Ограничиваем dot чтобы избежать NaN от acos
        dot = Math.max(-1.0, Math.min(1.0, dot));
        double angle = Math.toDegrees(Math.acos(dot));
        
        return angle <= SSC_HALF_ANGLE;
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
     * Синхронизирует состояние радара с клиентом
     * Передаёт: состояние, координаты цели, угол обзорного радара, угол башни, список всех целей, ракеты
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
            this.getId(), radarState, targetId, targetX, targetY, targetZ, progress, distance, radarAngle, turretAngle,
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
