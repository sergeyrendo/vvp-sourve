package tech.vvp.vvp.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.ExplosiveProjectile;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.init.ModEntities;

public class SosnaMissileEntity extends FastThrowableProjectile implements GeoEntity, ExplosiveProjectile {

    // Константы радиовзрывателя
    private static final float MIN_PROXIMITY = 5.0f;
    private static final float MAX_PROXIMITY = 15.0f; // Увеличен (было 10)
    private static final float PROXIMITY_CONE_ANGLE = 20.0f;
    
    // Константы предвзрывателя
    private static final float EMERGENCY_TRIGGER_DISTANCE = 12.0f;
    private static final float EMERGENCY_CONE_ANGLE = 90.0f;
    private static final float EMERGENCY_RAYCAST_DISTANCE = 5.0f;
    private static final float EMERGENCY_DISTANCE_THRESHOLD = 3.0f;
    
    // Константы улучшенного наведения
    private static final int STABILIZATION_TICKS = 4; // 0.2 сек стабилизации
    private static final float BASE_PREDICTION_FACTOR = 1.5f; // Ещё больше (было 1.2)
    private static final float MAX_PREDICTION_FACTOR = 2.5f;  // Ещё больше (было 2.0)
    private static final float CLOSE_RANGE_DISTANCE = 300.0f; // Увеличенная манёвренность
    private static final float CLOSE_RANGE_TURN_MULTIPLIER = 2.0f; // Увеличен (было 1.5)
    private static final float EVASION_TURN_MULTIPLIER = 2.0f;
    private static final int EVASION_DURATION_TICKS = 6; // 0.3 сек
    private static final float TARGET_LOST_ANGLE = 60.0f; // Угол потери цели (было 80°)
    private static final int TARGET_LOST_TIMEOUT = 8; // 0.4 сек (было 0.5)
    private static final float MAX_FLIGHT_DISTANCE = 1200.0f;
    private static final float TARGET_LOST_DISTANCE = 400.0f;
    private static final float SPHERE_TRACE_DISTANCE = 15.0f; // Радиовзрыватель впереди (было 12)
    private static final float MIN_SPEED_FOR_PROXIMITY = 0.15f; // 15% от макс скорости (было 20%)
    
    // Константы реалистичности (погрешность наведения)
    private static final float BASE_INACCURACY = 0.02f; // Базовая погрешность 2%
    private static final float DISTANCE_INACCURACY_FACTOR = 0.00005f; // Погрешность растёт с дистанцией
    private static final float SPEED_INACCURACY_FACTOR = 0.01f; // Погрешность для быстрых целей
    
    public static final EntityDataAccessor<Boolean> TOP_ATTACK = SynchedEntityData.defineId(SosnaMissileEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<String> TARGET_UUID = SynchedEntityData.defineId(SosnaMissileEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    private int guideType = 0;
    private Vec3 targetPos;
    private Vec3 lastKnownTargetPos = null;
    private Vec3 launchPos = null;
    private int targetLostTicks = 0;
    private int evasionModeTicks = 0;
    private Vec3 lastTargetVelocity = Vec3.ZERO;
    
    // Случайная погрешность наведения (генерируется при создании)
    private Vec3 inaccuracyOffset = null;
    
    private float damage = 1200f;        // ИМБА урон (было 900)
    private float explosionDamage = 350f; // ИМБА урон взрыва (было 250)
    private float explosionRadius = 12f;  // ИМБА радиус (было 10)
    private float gravity = 0f;
    
    private boolean hasExploded = false;
    private java.util.Map<String, Double> lastDistances = new java.util.HashMap<>();

    public SosnaMissileEntity(EntityType<? extends SosnaMissileEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    public SosnaMissileEntity(LivingEntity shooter, Level level, float damage, float explosionDamage, 
                                   float explosionRadius, int guideType, @Nullable Vec3 targetPos) {
        super(ModEntities.SOSNA_MISSILE.get(), shooter, level);
        this.noCulling = true;
        this.damage = damage;
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
        this.launchPos = shooter.position();
        this.guideType = guideType;
        this.targetPos = targetPos;
        this.durability = 50;
        
        // Генерируем случайную погрешность наведения (реалистичность)
        this.inaccuracyOffset = new Vec3(
            (this.random.nextDouble() - 0.5) * BASE_INACCURACY,
            (this.random.nextDouble() - 0.5) * BASE_INACCURACY,
            (this.random.nextDouble() - 0.5) * BASE_INACCURACY
        );
    }

    public SosnaMissileEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.SOSNA_MISSILE.get(), level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return Items.ARROW; // Placeholder
    }

    public void setTopAttack(boolean topAttack) {
        this.entityData.set(TOP_ATTACK, topAttack);
    }

    public void setTargetUuid(String uuid) {
        this.entityData.set(TARGET_UUID, uuid);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TOP_ATTACK, true);
        this.entityData.define(TARGET_UUID, "");
    }

    @Override
    public void tick() {
        super.tick();
        
        // Белый дымовой след (в 20 раз больше) - долгоживущий
        if (this.level().isClientSide && this.tickCount > 2) {
            // Белые облака - основной след
            for (int i = 0; i < 80; i++) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                    this.getY() + (this.random.nextDouble() - 0.5) * 0.5,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                    (this.random.nextDouble() - 0.5) * 0.01,
                    (this.random.nextDouble() - 0.5) * 0.01,
                    (this.random.nextDouble() - 0.5) * 0.01);
            }
            // Большие белые облака для плотности
            for (int i = 0; i < 30; i++) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.CLOUD,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.6,
                    this.getY() + (this.random.nextDouble() - 0.5) * 0.6,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.6,
                    (this.random.nextDouble() - 0.5) * 0.005,
                    (this.random.nextDouble() - 0.5) * 0.005,
                    (this.random.nextDouble() - 0.5) * 0.005);
            }
            // Белый дым от костра для долгого висения
            for (int i = 0; i < 15; i++) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.4,
                    this.getY() + (this.random.nextDouble() - 0.5) * 0.4,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.4,
                    (this.random.nextDouble() - 0.5) * 0.003,
                    0.02,
                    (this.random.nextDouble() - 0.5) * 0.003);
            }
        }
        
        // АГРЕССИВНАЯ загрузка чанков вокруг ракеты (против прогруза)
        if (!this.level().isClientSide) {
            // КАЖДЫЙ ТИК загружаем чанки вокруг ракеты и цели
            BlockPos missilePos = this.blockPosition();
            
            // Загружаем чанк ракеты
            if (!this.level().isLoaded(missilePos)) {
                this.level().getChunk(missilePos);
            }
            
            // Загружаем чанки впереди ракеты (на 20 блоков)
            Vec3 velocity = this.getDeltaMovement();
            if (velocity.lengthSqr() > 0.01) {
                Vec3 ahead = this.position().add(velocity.normalize().scale(20));
                BlockPos aheadPos = new BlockPos((int)ahead.x, (int)ahead.y, (int)ahead.z);
                if (!this.level().isLoaded(aheadPos)) {
                    this.level().getChunk(aheadPos);
                }
            }
            
            // Загружаем чанк цели
            String targetUuid = this.entityData.get(TARGET_UUID);
            if (!targetUuid.isEmpty()) {
                Entity target = findEntityByUuid(targetUuid);
                if (target != null) {
                    BlockPos targetPos = target.blockPosition();
                    if (!this.level().isLoaded(targetPos)) {
                        this.level().getChunk(targetPos);
                    }
                }
            }
        }
        
        // Предвзрыватель (emergency trigger) - проверка каждый тик
        if (!this.level().isClientSide && !hasExploded && this.tickCount > 5) {
            checkEmergencyTrigger();
        }
        
        // Радиовзрыватель - проверка каждый тик (ПРИОРИТЕТ!)
        if (!this.level().isClientSide && !hasExploded && this.tickCount > 5) {
            checkProximityFuse();
        }
        
        // Проверка лимитов полёта
        if (!this.level().isClientSide && !hasExploded) {
            checkFlightLimits();
        }
        
        // Улучшенная система наведения - ВСЕГДА активна после стабилизации
        if (!this.level().isClientSide && this.guideType == 0 && this.tickCount > STABILIZATION_TICKS) {
            improvedGuidanceSystem();
        } else if (!this.level().isClientSide && this.targetPos != null && this.tickCount > STABILIZATION_TICKS) {
            // Наведение на позицию (режим без цели)
            guidanceToPosition();
        }
    }

    /**
     * Проверка лимитов полёта - самоликвидация при превышении
     */
    private void checkFlightLimits() {
        if (launchPos == null) return;
        
        double distanceFromLaunch = this.position().distanceTo(launchPos);
        
        // Лимит 1200 блоков от точки запуска
        if (distanceFromLaunch > MAX_FLIGHT_DISTANCE) {
            hasExploded = true;
            this.discard();
            return;
        }
        
        // Если цель потеряна и улетели > 400 блоков
        String targetUuid = this.entityData.get(TARGET_UUID);
        if (!targetUuid.isEmpty()) {
            Entity target = findEntityByUuid(targetUuid);
            if (target == null || !target.isAlive()) {
                if (distanceFromLaunch > TARGET_LOST_DISTANCE) {
                    hasExploded = true;
                    this.discard();
                }
            }
        }
    }

    /**
     * Улучшенная система наведения - ВСЕГДА корректирует курс
     */
    private void improvedGuidanceSystem() {
        String targetUuid = this.entityData.get(TARGET_UUID);
        if (targetUuid.isEmpty()) return;
        
        Entity target = findEntityByUuid(targetUuid);
        Vec3 currentVel = this.getDeltaMovement();
        double currentSpeed = currentVel.length();
        
        // Постепенный разгон
        double acceleration = this.tickCount < 10 ? 0.5 : 0.8;
        double targetSpeed = Math.min(12.0, currentSpeed + acceleration);
        
        if (target != null && target.isAlive()) {
            // ЦЕЛЬ НАЙДЕНА - сбрасываем счётчик потери
            targetLostTicks = 0;
            
            Vec3 targetCurrentPos = target.position().add(0, target.getBbHeight() / 2, 0);
            Vec3 targetVel = target.getDeltaMovement();
            lastTargetVelocity = targetVel;
            lastKnownTargetPos = targetCurrentPos;
            
            double targetVelocity = targetVel.length();
            double distanceToTarget = this.position().distanceTo(targetCurrentPos);
            
            // Улучшенный prediction factor - больше упреждения
            float predictionFactor = BASE_PREDICTION_FACTOR;
            if (targetVelocity > 1.5) { // Быстрая цель
                predictionFactor = Math.min(MAX_PREDICTION_FACTOR, BASE_PREDICTION_FACTOR + (float)(targetVelocity * 0.3));
            }
            
            // Увеличенное предсказание для дальних целей
            float distanceFactor = Math.min(2.0f, (float)(distanceToTarget / 500.0));
            
            // Предсказанная позиция с улучшенным упреждением
            Vec3 predictedPos = targetCurrentPos.add(targetVel.scale(predictionFactor * distanceFactor));
            
            // ДОБАВЛЯЕМ РЕАЛИСТИЧНУЮ ПОГРЕШНОСТЬ
            if (inaccuracyOffset != null) {
                // Погрешность увеличивается с дистанцией и скоростью цели
                float inaccuracyMultiplier = 1.0f + 
                    (float)(distanceToTarget * DISTANCE_INACCURACY_FACTOR) +
                    (float)(targetVelocity * SPEED_INACCURACY_FACTOR);
                
                Vec3 scaledInaccuracy = inaccuracyOffset.scale(inaccuracyMultiplier * distanceToTarget);
                predictedPos = predictedPos.add(scaledInaccuracy);
            }
            
            Vec3 toTarget = predictedPos.subtract(this.position());
            Vec3 direction = toTarget.normalize();
            
            // Проверка угла до цели
            Vec3 missileDirection = currentVel.normalize();
            double dotProduct = missileDirection.dot(direction);
            double angleToTarget = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dotProduct))));
            
            // Если угол > 80° - режим догнать
            if (angleToTarget > TARGET_LOST_ANGLE) {
                targetLostTicks++;
                if (targetLostTicks > TARGET_LOST_TIMEOUT) {
                    // Самоликвидация - не можем догнать
                    hasExploded = true;
                    this.discard();
                    return;
                }
            }
            
            // УВЕЛИЧЕННАЯ сила поворота - меньше кружения
            double baseTurnStrength;
            if (distanceToTarget < 10) {
                baseTurnStrength = 0.8;  // Было 0.4
            } else if (distanceToTarget < 50) {
                baseTurnStrength = 1.0;  // Было 0.6
            } else if (distanceToTarget < 150) {
                baseTurnStrength = 1.2;  // Было 0.8
            } else if (distanceToTarget < CLOSE_RANGE_DISTANCE) {
                baseTurnStrength = 1.4;  // Было 0.9
            } else {
                baseTurnStrength = 1.6;  // Было 1.0
            }
            
            // Увеличенная манёвренность на < 300 блоков
            if (distanceToTarget < CLOSE_RANGE_DISTANCE) {
                baseTurnStrength *= CLOSE_RANGE_TURN_MULTIPLIER; // × 1.5
            }
            
            // Режим уклонения - если цель резко меняет траекторию
            if (evasionModeTicks > 0) {
                baseTurnStrength *= EVASION_TURN_MULTIPLIER;
                evasionModeTicks--;
            } else {
                // Проверка резкого изменения траектории цели
                if (lastTargetVelocity.lengthSqr() > 0.01) {
                    double velocityChange = targetVel.subtract(lastTargetVelocity).length();
                    if (velocityChange > 0.5) { // Резкий манёвр
                        evasionModeTicks = EVASION_DURATION_TICKS;
                        baseTurnStrength *= EVASION_TURN_MULTIPLIER;
                    }
                }
            }
            
            // Применяем наведение
            Vec3 newVel = currentVel.add(direction.scale(baseTurnStrength)).normalize().scale(targetSpeed);
            this.setDeltaMovement(newVel);
            
        } else {
            // ЦЕЛЬ ПОТЕРЯНА - пытаемся вернуться к последней известной позиции
            targetLostTicks++;
            
            if (targetLostTicks > TARGET_LOST_TIMEOUT) {
                // Самоликвидация через 0.5 сек
                hasExploded = true;
                this.discard();
                return;
            }
            
            if (lastKnownTargetPos != null) {
                // Поворачиваем к последней известной позиции
                Vec3 direction = lastKnownTargetPos.subtract(this.position()).normalize();
                Vec3 newVel = currentVel.add(direction.scale(0.5)).normalize().scale(targetSpeed);
                this.setDeltaMovement(newVel);
            }
        }
    }

    /**
     * Наведение на позицию (режим без захвата цели)
     */
    private void guidanceToPosition() {
        if (this.targetPos == null) return;
        
        Vec3 direction = this.targetPos.subtract(this.position()).normalize();
        Vec3 currentVel = this.getDeltaMovement();
        
        double currentSpeed = currentVel.length();
        double acceleration = this.tickCount < 10 ? 0.5 : 0.8;
        double targetSpeed = Math.min(12.0, currentSpeed + acceleration);
        
        Vec3 newVel = currentVel.add(direction.scale(0.6)).normalize().scale(targetSpeed);
        this.setDeltaMovement(newVel);
    }

    /**
     * Предвзрыватель (Emergency Trigger) - срабатывает при резком пересечении траектории
     * Активируется на дистанции 10-12м, широкий конус 90°, урон × 0.5
     */
    private void checkEmergencyTrigger() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        
        Vec3 missilePos = this.position();
        Vec3 missileVelocity = this.getDeltaMovement();
        
        // Направление полета ракеты
        if (missileVelocity.lengthSqr() < 0.01) return;
        Vec3 missileDirection = missileVelocity.normalize();
        
        // Ищем цели в расширенном радиусе (10-12м)
        var nearbyEntities = serverLevel.getEntities(this,
            this.getBoundingBox().inflate(EMERGENCY_TRIGGER_DISTANCE),
            entity -> entity instanceof LivingEntity &&
                      entity != this.getOwner() &&
                      (this.getOwner() == null || entity != this.getOwner().getVehicle()) &&
                      entity.isAlive());
        
        for (Entity target : nearbyEntities) {
            Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
            Vec3 toTarget = targetPos.subtract(missilePos);
            double distance = toTarget.length();
            
            // Активация только в зоне 10-12м
            if (distance < MAX_PROXIMITY || distance > EMERGENCY_TRIGGER_DISTANCE) continue;
            
            // Проверка угла: широкий конус 90° (не сзади)
            Vec3 toTargetNorm = toTarget.normalize();
            double dotProduct = missileDirection.dot(toTargetNorm);
            
            // Если цель сзади (dot < 0), игнорируем
            if (dotProduct < 0) continue;
            
            double angleRadians = Math.acos(Math.max(-1.0, Math.min(1.0, dotProduct)));
            double angleDegrees = Math.toDegrees(angleRadians);
            
            if (angleDegrees > EMERGENCY_CONE_ANGLE) continue;
            
            String targetId = target.getStringUUID();
            Double lastDistance = lastDistances.get(targetId);
            
            // Проверка 1: Резкое сближение (> 3м за тик)
            boolean rapidApproach = false;
            if (lastDistance != null) {
                double distanceChange = lastDistance - distance;
                if (distanceChange > EMERGENCY_DISTANCE_THRESHOLD) {
                    rapidApproach = true;
                }
            }
            
            // Проверка 2: Raycast - цель пересекает траекторию впереди
            boolean crossingPath = false;
            Vec3 raycastEnd = missilePos.add(missileDirection.scale(EMERGENCY_RAYCAST_DISTANCE));
            double distanceToRay = distancePointToLineSegment(targetPos, missilePos, raycastEnd);
            if (distanceToRay < target.getBbWidth() + 1.0) { // Цель близко к траектории
                crossingPath = true;
            }
            
            // Обновляем дистанцию для следующего тика
            lastDistances.put(targetId, distance);
            
            // EMERGENCY TRIGGER!
            if (rapidApproach || crossingPath) {
                hasExploded = true;
                
                // Урон × 0.5 (emergency trigger слабее)
                float emergencyDamageMultiplier = 0.5f;
                
                DamageHandler.doDamage(target,
                    ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()),
                    this.damage * emergencyDamageMultiplier);
                
                if (target instanceof LivingEntity) {
                    target.invulnerableTime = 0;
                }
                
                // Взрыв слабее: урон × 0.5, радиус × 0.7
                Vec3 explosionPos = missilePos.add(toTarget.scale(0.3));
                causeExplode(explosionPos, emergencyDamageMultiplier, 0.7f);
                
                this.discard();
                return;
            }
        }
    }
    
    /**
     * Вычисляет расстояние от точки до отрезка (для raycast)
     */
    private double distancePointToLineSegment(Vec3 point, Vec3 lineStart, Vec3 lineEnd) {
        Vec3 line = lineEnd.subtract(lineStart);
        Vec3 toPoint = point.subtract(lineStart);
        
        double lineLengthSq = line.lengthSqr();
        if (lineLengthSq < 0.0001) return toPoint.length();
        
        double t = Math.max(0, Math.min(1, toPoint.dot(line) / lineLengthSq));
        Vec3 projection = lineStart.add(line.scale(t));
        
        return point.distanceTo(projection);
    }

    /**
     * Улучшенный радиовзрыватель - УПРОЩЁННЫЙ для надёжности
     * Срабатывает на 2-12 блоков впереди ракеты
     */
    private void checkProximityFuse() {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;
        
        Vec3 missilePos = this.position();
        Vec3 missileVelocity = this.getDeltaMovement();
        double currentSpeed = missileVelocity.length();
        
        // Проверка минимальной скорости (20% от макс = 2.4 блока/тик)
        if (currentSpeed < 12.0 * MIN_SPEED_FOR_PROXIMITY) return;
        
        if (missileVelocity.lengthSqr() < 0.01) return;
        Vec3 missileDirection = missileVelocity.normalize();
        
        // УПРОЩЁННАЯ ПРОВЕРКА - просто ищем цели рядом
        var nearbyEntities = serverLevel.getEntities(this, 
            this.getBoundingBox().inflate(SPHERE_TRACE_DISTANCE),
            entity -> entity instanceof LivingEntity && 
                      entity != this.getOwner() && 
                      (this.getOwner() == null || entity != this.getOwner().getVehicle()) &&
                      entity.isAlive());
        
        for (Entity target : nearbyEntities) {
            Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
            Vec3 toTarget = targetPos.subtract(missilePos);
            double distance = toTarget.length();
            
            // ГЛАВНОЕ ИЗМЕНЕНИЕ: убрал MIN_PROXIMITY проверку!
            // Теперь взрывается на любой дистанции до 12 блоков
            if (distance > SPHERE_TRACE_DISTANCE) continue;
            
            // Проверка: цель впереди (не сзади)
            Vec3 toTargetNorm = toTarget.normalize();
            double dotProduct = missileDirection.dot(toTargetNorm);
            
            // Если цель хоть немного впереди (угол < 90°) - взрываемся
            if (dotProduct < 0.3) continue; // Только если совсем сзади - игнорируем
            
            // РАДИОВЗРЫВАТЕЛЬ СРАБОТАЛ!
            hasExploded = true;
            
            // Упрощённый расчёт урона: чем ближе - тем больше
            // 2м = 1.0, 12м = 0.6 (не слишком слабый даже вдали)
            float damageMultiplier = (float) Math.max(0.6, 1.0 - (distance / SPHERE_TRACE_DISTANCE) * 0.4);
            
            // Прямой урон цели
            DamageHandler.doDamage(target,
                ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()),
                this.damage * damageMultiplier * 0.85f); // 0.85 вместо 0.7 - сильнее
            
            if (target instanceof LivingEntity) {
                target.invulnerableTime = 0;
            }
            
            // Взрыв ближе к цели
            Vec3 explosionPos = missilePos.add(toTarget.scale(0.6));
            causeExplode(explosionPos, damageMultiplier * 0.9f, 1.0f); // Полный радиус
            
            this.discard();
            return; // Взорвались, выходим
        }
    }

    private Entity findEntityByUuid(String uuid) {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity.getStringUUID().equals(uuid)) {
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (hasExploded) return; // Предотвращаем двойной взрыв
        
        Entity entity = result.getEntity();
        
        // Игнорируем владельца и его технику
        if (entity == this.getOwner()) return;
        if (this.getOwner() != null && entity == this.getOwner().getVehicle()) return;
            
        if (!this.level().isClientSide) {
            hasExploded = true;
            
            // Урон с бонусом за top-attack (прямое попадание - максимальный урон)
            float damageMultiplier = this.entityData.get(TOP_ATTACK) ? 1.25f : 1f;
            DamageHandler.doDamage(entity, 
                ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), 
                damageMultiplier * this.damage);

            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }

            // ВЗРЫВ (прямое попадание - полный урон)
            causeExplode(result.getLocation(), 1.0f);
            this.discard();
        }
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        if (hasExploded) return; // Предотвращаем двойной взрыв
        
        if (!this.level().isClientSide) {
            hasExploded = true;
            
            // ВЗРЫВ при попадании в блок
            causeExplode(blockHitResult.getLocation(), 1.0f);
            this.discard();
        }
    }

    public void causeExplode(Vec3 pos, float damageMultiplier) {
        causeExplode(pos, damageMultiplier, 1.0f);
    }
    
    public void causeExplode(Vec3 pos, float damageMultiplier, float radiusMultiplier) {
        if (this.level() instanceof ServerLevel serverLevel) {
            // Создаем взрыв с учетом множителей урона и радиуса
            new CustomExplosion.Builder(this)
                    .attacker(this.getOwner())
                    .damage(this.explosionDamage * damageMultiplier)
                    .radius(this.explosionRadius * radiusMultiplier)
                    .position(pos)
                    .withParticleType(ParticleTool.ParticleType.HUGE)
                    .explode();
            
            // Дополнительный звук взрыва
            serverLevel.playSound(null, pos.x, pos.y, pos.z, 
                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, 
                net.minecraft.sounds.SoundSource.BLOCKS, 
                4.0F, (1.0F + (serverLevel.random.nextFloat() - serverLevel.random.nextFloat()) * 0.2F) * 0.7F);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SosnaMissileEntity> event) {
        event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.SOSNA_MISSILE.idle"));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public void setExplosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
    }

    @Override
    public void setExplosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    @Override
    public float getGravity() {
        return this.gravity;
    }

    @Override
    public boolean forceLoadChunk() {
        return true;
    }
}


