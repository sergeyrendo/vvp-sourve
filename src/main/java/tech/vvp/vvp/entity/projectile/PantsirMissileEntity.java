                                                    package tech.vvp.vvp.entity.projectile;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.network.NetworkRegistry;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

/**
 * Ракета 57Э6 для ЗРПК Панцирь-С1
 * Наследует от MissileProjectile для интеграции со стандартной системой SuperbWarfare
 * Ракета 57Э6 для ЗРПК Панцирь-С1
 * Реалистичные характеристики:
 * - Скорость: 1300 м/с (в игре ~10 блоков/тик для баланса)
 * - Перегрузка: до 30g (ограниченный угол поворота)
 * - Дальность: 20 км (в игре 2000 блоков)
 */
public class PantsirMissileEntity extends MissileProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Параметры ракеты 57Э6 (сбалансированные для игры)
    // БАЛАНС: Ракета эффективна против медленных целей, но может промахнуться по быстрым манёврам
    private static final double MISSILE_SPEED = 10.0;
    private static final double INITIAL_SPEED = 8.0;
    private static final double ACCELERATION = 0.25;
    private static final float MAX_TURN_RATE = 5.0f;
    private static final float BOOST_TURN_RATE = 6.0f;
    private static final float MAX_LEAD_ANGLE = 45.0f;
    private static final double PROXIMITY_FUSE_MISSILE = 2.5;
    private static final double PROXIMITY_FUSE_AIRCRAFT = 3.5;
    private static final double SHRAPNEL_RANGE = 12.0;
    private static final float SHRAPNEL_MAX_DAMAGE = 60.0f;
    private static final int MAX_LIFETIME = 400;
    private static final int BOOST_PHASE_TICKS = 25;
    private static final int MAX_TURN_ANGLE = 75;
    
    private static final int TARGET_UPDATE_INTERVAL = 3;
    private static final double FLARE_CONE_ANGLE = 15.0;
    
    private int launcherEntityId = -1;
    private int targetEntityId = -1;
    private boolean targetIsMissile = false;
    private double lastDistanceToTarget = Double.MAX_VALUE;
    private double minDistanceReached = Double.MAX_VALUE;
    private boolean lostTargetFromManeuver = false;
    
    private Vec3 cachedTargetPos = null;
    private Vec3 smoothedTargetVelocity = Vec3.ZERO;
    private int targetUpdateCooldown = 0;
    
    public PantsirMissileEntity(EntityType<? extends PantsirMissileEntity> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.MEDIUM_ANTI_AIR_MISSILE.get();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.launcherEntityId = compound.getInt("LauncherId");
        this.targetEntityId = compound.getInt("TargetEntityId");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LauncherId", launcherEntityId);
        compound.putInt("TargetEntityId", targetEntityId);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(targetPos != null);
        if (targetPos != null) {
            buffer.writeDouble(targetPos.x);
            buffer.writeDouble(targetPos.y);
            buffer.writeDouble(targetPos.z);
        }
        buffer.writeInt(launcherEntityId);
        buffer.writeInt(targetEntityId);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            this.targetPos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
        this.launcherEntityId = buffer.readInt();
        this.targetEntityId = buffer.readInt();
    }

    @Override
    public void tick() {
        super.tick();
        spawnTrailParticles();
        
        if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
            // КРИТИЧНО: На первых 5 тиках постоянно ищем панцирь и цель
            // Это нужно для надёжной синхронизации на сервере
            if (this.tickCount <= 5) {
                forceUpdateTargetFromRadar();
            }
            
            // Проверяем decoy/flare как в Agm65Entity
            checkForDecoy();
            tickGuidance();
        }
        
        if (this.tickCount > MAX_LIFETIME || this.isInWater()) {
            explodeAndDiscard();
        }
    }
    
    /**
     * Проверяет наличие decoy/flare и переключается на них
     * БАЛАНС: Флаеры имеют долгий кулдаун (20-25 сек), поэтому шанс отвлечения высокий
     */
    private void checkForDecoy() {
        // Ищем decoy в радиусе 32 блоков с углом 90 градусов
        List<Entity> decoy = SeekTool.seekLivingEntities(this, 32, 90);
        
        for (var e : decoy) {
            if (e.getType().is(ModTags.EntityTypes.DECOY) && !this.distracted) {
                // Проверяем что флаер на пути к цели (не за целью)
                if (!isFlareOnPath(e)) continue;
                
                // Вычисляем шанс отвлечения
                double distractionChance = calculateDistractionChance(e);
                
                // Проверяем шанс
                if (this.random.nextDouble() < distractionChance) {
                    // Переключаемся на decoy
                    this.entityData.set(TARGET_UUID, e.getStringUUID());
                    this.distracted = true;
                    break;
                }
            }
        }
    }
    
    /**
     * Проверяет что флаер находится МЕЖДУ ракетой и целью
     * Если флаер за целью (пилот летит на Панцирь и сбрасывает флаеры назад) - возвращает false
     * 
     * @param flare флаер
     * @return true если флаер на пути ракеты к цели
     */
    private boolean isFlareOnPath(Entity flare) {
        if (targetPos == null) return true;
        
        Vec3 missileVelocity = this.getDeltaMovement();
        if (missileVelocity.lengthSqr() < 0.01) return true;
        
        Vec3 missilePos = this.position();
        Vec3 flarePos = flare.position();
        Vec3 toFlare = flarePos.subtract(missilePos);
        
        Vec3 missileDirection = missileVelocity.normalize();
        Vec3 toFlareNorm = toFlare.normalize();
        
        double dot = missileDirection.dot(toFlareNorm);
        double angle = Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0, 1.0)));
        
        if (angle > FLARE_CONE_ANGLE) return false;
        
        double distToFlare = toFlare.length();
        Vec3 projectionOnPath = missileDirection.scale(distToFlare * dot);
        Vec3 lateralOffset = toFlare.subtract(projectionOnPath);
        double lateralDist = lateralOffset.length();
        
        double maxLateralDist = distToFlare * Math.tan(Math.toRadians(FLARE_CONE_ANGLE));
        return lateralDist <= maxLateralDist;
    }
    
    /**
     * Вычисляет шанс отвлечения ракеты на флаер
     * БАЛАНС: У самолётов долгий кулдаун флаеров (20-25 сек), поэтому базовый шанс высокий
     * 
     * Факторы:
     * 1. Базовый шанс 70% (высокий из-за долгого кулдауна)
     * 2. Расстояние до флаера (ближе = лучше)
     * 3. Фаза полёта ракеты (в начале легче отвлечь)
     * 4. Угол между флаером и целью (флаер должен быть на пути)
     * 5. Тепловая сигнатура цели (форсаж = сложнее отвлечь)
     * 
     * @param flare флаер
     * @return шанс от 0.0 до 1.0
     */
    private double calculateDistractionChance(Entity flare) {
        double baseChance = 0.725;
        
        double distanceToFlare = this.distanceTo(flare);
        double distanceBonus;
        if (distanceToFlare < 15) {
            distanceBonus = 0.10;
        } else {
            distanceBonus = 0.0;
        }
        
        double flightPhaseBonus = 0.0;
        if (targetPos != null && this.position().distanceTo(targetPos) < 20) {
            flightPhaseBonus = -0.10;
        }
        
        double anglePenalty = 0.0;
        if (targetPos != null) {
            Vec3 toTarget = targetPos.subtract(this.position()).normalize();
            Vec3 toFlare = flare.position().subtract(this.position()).normalize();
            
            double dot = toTarget.dot(toFlare);
            double angle = Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0, 1.0)));
            
            if (angle > 70) {
                anglePenalty = -0.15;
            }
        }
        
        double finalChance = baseChance + distanceBonus + flightPhaseBonus + anglePenalty;
        return Mth.clamp(finalChance, 0.60, 0.95);
    }
    
    private void tickGuidance() {
        if (!lostTargetFromManeuver) {
            updateTargetFromRadar();
        }
        
        targetUpdateCooldown--;
        
        Entity target = null;
        if (targetEntityId != -1) {
            target = this.level().getEntity(targetEntityId);
        }
        
        if (target == null && !lostTargetFromManeuver) {
            String targetUuid = this.entityData.get(TARGET_UUID);
            if (targetUuid != null && !targetUuid.equals("none")) {
                target = EntityFindUtil.findEntity(this.level(), targetUuid);
                if (target != null) {
                    targetEntityId = target.getId();
                }
            }
        }
        
        if (target != null && target.isAlive()) {
            if (isTargetManeuvering(target)) {
                float evasionChance = getManeuverEvasionChance(target);
                if (this.random.nextFloat() < evasionChance) {
                    lostTargetFromManeuver = true;
                    targetEntityId = -1;
                    this.entityData.set(TARGET_UUID, "none");
                }
            }
            
            if (!lostTargetFromManeuver) {
                if (targetUpdateCooldown <= 0) {
                    Vec3 newTargetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                    
                    if (cachedTargetPos != null) {
                        Vec3 newVelocity = newTargetPos.subtract(cachedTargetPos).scale(1.0 / TARGET_UPDATE_INTERVAL);
                        smoothedTargetVelocity = newVelocity.scale(0.6).add(smoothedTargetVelocity.scale(0.4));
                    } else {
                        smoothedTargetVelocity = target.getDeltaMovement();
                    }
                    
                    cachedTargetPos = newTargetPos;
                    targetUpdateCooldown = TARGET_UPDATE_INTERVAL;
                }
                
                targetPos = calculateInterceptPoint(cachedTargetPos, smoothedTargetVelocity);
                targetIsMissile = isTargetMissile(target);
            }
            
            int warningInterval = (int) Math.max(0.04 * this.distanceTo(target), 2);
            if (this.tickCount % warningInterval == 0) {
                boolean shouldWarn = target instanceof VehicleEntity 
                    || !target.getPassengers().isEmpty()
                    || target instanceof Player;
                
                if (shouldWarn) {
                    target.level().playSound(null, target.getOnPos(), 
                        target instanceof Pig ? SoundEvents.PIG_HURT : ModSounds.MISSILE_WARNING.get(), 
                        SoundSource.PLAYERS, 2, 1f);
                }
            }
        }
        
        if (targetPos == null) {
            maintainSpeed();
            return;
        }
        
        double distanceToTarget = this.position().distanceTo(targetPos);
        
        if (distanceToTarget < minDistanceReached) {
            minDistanceReached = distanceToTarget;
        }
        
        double fuseRange = targetIsMissile ? PROXIMITY_FUSE_MISSILE : PROXIMITY_FUSE_AIRCRAFT;
        
        if (distanceToTarget < fuseRange) {
            explodeWithShrapnel();
            return;
        }
        
        if (distanceToTarget > lastDistanceToTarget + 0.5 && minDistanceReached < fuseRange * 2) {
            explodeWithShrapnel();
            return;
        }
        
        lastDistanceToTarget = distanceToTarget;
        
        float turnRate = (this.tickCount < BOOST_PHASE_TICKS) ? BOOST_TURN_RATE : MAX_TURN_RATE;
        turnToTarget(turnRate);
        
        maintainSpeed();
        updateRotationFromVelocity();
    }
    
    private Vec3 calculateInterceptPoint(Vec3 targetPos, Vec3 targetVelocity) {
        Vec3 missilePos = this.position();
        double currentSpeed = this.getDeltaMovement().length();
        double missileSpeed = Math.max(currentSpeed, MISSILE_SPEED * 0.5);
        
        double targetSpeed = targetVelocity.length();
        
        if (targetSpeed < 0.1) {
            return targetPos.add(0, 0.5, 0);
        }
        
        double distance = missilePos.distanceTo(targetPos);
        double timeToIntercept = distance / missileSpeed;
        
        for (int i = 0; i < 5; i++) {
            Vec3 predictedPos = targetPos.add(targetVelocity.scale(timeToIntercept));
            distance = missilePos.distanceTo(predictedPos);
            timeToIntercept = distance / missileSpeed;
        }
        
        double leadCoefficient = 1.0;
        if (distance < 40) {
            leadCoefficient = 0.3;
        }
        if (distance < 25) {
            leadCoefficient = 0.0;
        }
        
        Vec3 interceptPoint = targetPos.add(targetVelocity.scale(timeToIntercept * leadCoefficient)).add(0, 0.3, 0);
        
        return interceptPoint;
    }
    
    /**
     * Принудительно ищет панцирь и цель при первом тике
     * Ищет только панцирь владельца ракеты (для мультиплеера)
     */
    private void forceUpdateTargetFromRadar() {
        PantsirS1Entity pantsir = null;
        
        // Сначала пробуем через owner.getVehicle() - это самый надёжный способ
        if (this.getOwner() != null) {
            Entity vehicle = this.getOwner().getVehicle();
            if (vehicle instanceof PantsirS1Entity p && p.hasLockedTarget()) {
                pantsir = p;
                launcherEntityId = p.getId();
            }
        }
        
        // Если owner вышел из панциря - ищем панцирь рядом с owner
        if (pantsir == null && this.getOwner() != null) {
            List<PantsirS1Entity> nearby = this.level().getEntitiesOfClass(
                PantsirS1Entity.class, 
                this.getOwner().getBoundingBox().inflate(20),
                p -> p.hasLockedTarget()
            );
            if (!nearby.isEmpty()) {
                pantsir = nearby.get(0);
                launcherEntityId = pantsir.getId();
            }
        }
        
        // Fallback - ищем ближайший панцирь к ракете (только если owner null)
        if (pantsir == null && this.getOwner() == null) {
            List<PantsirS1Entity> nearby = this.level().getEntitiesOfClass(
                PantsirS1Entity.class, 
                this.getBoundingBox().inflate(50),
                p -> p.hasLockedTarget()
            );
            if (!nearby.isEmpty()) {
                pantsir = nearby.stream()
                    .min((a, b) -> Double.compare(this.distanceTo(a), this.distanceTo(b)))
                    .orElse(nearby.get(0));
                launcherEntityId = pantsir.getId();
            }
        }
        
        if (pantsir != null) {
            Entity lockedTarget = pantsir.getLockedTarget();
            if (lockedTarget != null && lockedTarget.isAlive()) {
                targetPos = lockedTarget.position().add(0, lockedTarget.getBbHeight() * 0.5, 0);
                targetEntityId = lockedTarget.getId();
                this.entityData.set(TARGET_UUID, lockedTarget.getStringUUID());
            }
        }
    }
    
    /**
     * Обновляет targetPos от радара Панциря
     */
    private void updateTargetFromRadar() {
        // Пробуем получить Pantsir через launcherId
        PantsirS1Entity pantsir = null;
        
        if (launcherEntityId != -1) {
            Entity launcher = this.level().getEntity(launcherEntityId);
            if (launcher instanceof PantsirS1Entity p) {
                pantsir = p;
            }
        }
        
        // Если launcherId не установлен, пробуем получить через owner.getVehicle()
        if (pantsir == null && this.getOwner() != null) {
            Entity vehicle = this.getOwner().getVehicle();
            if (vehicle instanceof PantsirS1Entity p) {
                pantsir = p;
                launcherEntityId = p.getId();
            }
        }
        
        // Если всё ещё нет панциря - ищем ближайший в радиусе 100 блоков
        if (pantsir == null) {
            List<PantsirS1Entity> nearby = this.level().getEntitiesOfClass(
                PantsirS1Entity.class, 
                this.getBoundingBox().inflate(100),
                p -> p.hasLockedTarget()
            );
            if (!nearby.isEmpty()) {
                pantsir = nearby.get(0);
                launcherEntityId = pantsir.getId();
            }
        }
        
        if (pantsir != null) {
            Entity lockedTarget = pantsir.getLockedTarget();
            if (lockedTarget != null && lockedTarget.isAlive()) {
                // Обновляем позицию цели от радара
                targetPos = lockedTarget.position().add(0, lockedTarget.getBbHeight() * 0.5, 0);
                // Обновляем ID цели для быстрого поиска
                targetEntityId = lockedTarget.getId();
                // Обновляем UUID для совместимости
                this.entityData.set(TARGET_UUID, lockedTarget.getStringUUID());
            }
            // Если радар потерял цель - продолжаем лететь по последней позиции
        }
    }
    
    /**
     * Определяет является ли цель ракетой/баллистикой (для радиовзрывателя)
     */
    private boolean isTargetMissile(Entity target) {
        if (target == null) return false;
        
        // MissileProjectile из SBW
        if (target instanceof MissileProjectile) return true;
        
        // Проверяем по имени класса
        String className = target.getClass().getSimpleName();
        return className.contains("Missile") || className.contains("Rocket") || className.contains("Bomb");
    }
    
    /**
     * Проверяет делает ли цель резкий манёвр
     * БАЛАНС: Манёвры эффективнее когда ракета ближе (меньше времени на реакцию)
     */
    private boolean isTargetManeuvering(Entity target) {
        if (target == null) return false;
        
        // Только для VehicleEntity (самолёты/вертолёты)
        if (!(target instanceof VehicleEntity)) {
            return false;
        }
        
        Vec3 velocity = target.getDeltaMovement();
        double speed = velocity.length();
        
        // Если цель стоит или движется медленно - манёвра нет
        if (speed < 0.25) return false;
        
        // Резкий набор высоты или пикирование
        double verticalSpeed = Math.abs(velocity.y);
        if (verticalSpeed > 0.4) {
            return true;
        }
        
        // Резкий поворот (проверяем изменение yaw)
        float currentYaw = target.getYRot();
        float oldYaw = target.yRotO;
        float yawDelta = Math.abs(currentYaw - oldYaw);
        
        // Нормализуем угол в диапазон [0, 180]
        if (yawDelta > 180) yawDelta = 360 - yawDelta;
        
        // Резкий поворот (> 10 градусов за тик при движении)
        if (yawDelta > 10 && speed > 0.4) {
            return true;
        }
        
        return false;
    }
    
    private float getManeuverEvasionChance(Entity target) {
        if (target == null || targetPos == null) return 0.05f;
        
        double distance = this.position().distanceTo(target.position());
        
        if (distance < 30) {
            return 0.45f;
        } else if (distance < 60) {
            return 0.25f;
        } else if (distance < 100) {
            return 0.10f;
        } else {
            return 0.05f;
        }
    }
    
    private void turnToTarget(float maxTurnRate) {
        if (targetPos == null) return;
        
        Vec3 currentVelocity = this.getDeltaMovement();
        if (currentVelocity.lengthSqr() < 0.001) {
            Vec3 toTarget = targetPos.subtract(this.position()).normalize();
            this.setDeltaMovement(toTarget.scale(INITIAL_SPEED));
            return;
        }
        
        Vec3 toTarget = targetPos.subtract(this.position());
        Vec3 targetDirection = toTarget.normalize();
        Vec3 currentDirection = currentVelocity.normalize();
        
        double dot = currentDirection.dot(targetDirection);
        double angle = Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0, 1.0)));
        
        if (angle > MAX_TURN_ANGLE) {
            return;
        }
        
        double actualTurnRate = Math.min(maxTurnRate, angle);
        double turnFactor = actualTurnRate / Math.max(angle, 0.1);
        
        Vec3 newDirection = slerp(currentDirection, targetDirection, turnFactor);
        
        double speed = currentVelocity.length();
        this.setDeltaMovement(newDirection.scale(speed));
    }
    
    private Vec3 slerp(Vec3 start, Vec3 end, double t) {
        double dot = Mth.clamp(start.dot(end), -1.0, 1.0);
        double theta = Math.acos(dot) * t;
        
        Vec3 relative = end.subtract(start.scale(dot)).normalize();
        return start.scale(Math.cos(theta)).add(relative.scale(Math.sin(theta)));
    }
    
    /**
     * Поддерживает скорость ракеты с плавным ускорением
     * Ракета разгоняется от начальной скорости до максимальной
     */
    private void maintainSpeed() {
        Vec3 velocity = this.getDeltaMovement();
        double currentSpeed = velocity.length();
        
        // Целевая скорость зависит от фазы полёта
        double targetSpeed;
        if (this.tickCount < BOOST_PHASE_TICKS) {
            // Фаза разгона - плавно увеличиваем скорость
            double progress = (double) this.tickCount / BOOST_PHASE_TICKS;
            targetSpeed = INITIAL_SPEED + (MISSILE_SPEED - INITIAL_SPEED) * progress;
        } else {
            // Крейсерская фаза - максимальная скорость
            targetSpeed = MISSILE_SPEED;
        }
        
        // Плавно изменяем скорость
        if (Math.abs(currentSpeed - targetSpeed) > 0.1) {
            double newSpeed;
            if (currentSpeed < targetSpeed) {
                // Ускоряемся
                newSpeed = Math.min(currentSpeed + ACCELERATION, targetSpeed);
            } else {
                // Замедляемся (редко, но может быть при резких манёврах)
                newSpeed = Math.max(currentSpeed - ACCELERATION * 0.5, targetSpeed);
            }
            
            Vec3 direction = velocity.lengthSqr() > 0.001 ? velocity.normalize() : this.getLookAngle();
            this.setDeltaMovement(direction.scale(newSpeed));
        }
    }
    
    /**
     * Обновляет визуальный поворот ракеты по направлению движения
     */
    private void updateRotationFromVelocity() {
        Vec3 velocity = this.getDeltaMovement();
        if (velocity.lengthSqr() > 0.001) {
            double d0 = velocity.horizontalDistance();
            this.setYRot((float) (-Mth.atan2(velocity.x, velocity.z) * (180F / (float) Math.PI)));
            this.setXRot((float) (-Mth.atan2(velocity.y, d0) * (180F / (float) Math.PI)));
        }
    }

    /**
     * Спавнит частицы следа ракеты
     */
    /**
     * Спавнит красивый дымовой след за ракетой
     * Как в War Thunder - густой белый дым с огнём двигателя
     */
    private void spawnTrailParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            Vec3 velocity = this.getDeltaMovement();
            
            // Позиция позади ракеты (откуда идёт дым)
            Vec3 trailPos = pos.subtract(velocity.normalize().scale(0.3));
            
            // Основной густой белый дым (как у настоящих ЗУР)
            ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, 
                trailPos.x, trailPos.y, trailPos.z, 3, 0.05, 0.05, 0.05, 0.001, true);
            
            // Обычный дым для объёма
            ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, 
                trailPos.x, trailPos.y, trailPos.z, 2, 0.08, 0.08, 0.08, 0.01, false);
            
            // Огонь двигателя (ярче в фазе разгона)
            if (this.tickCount < BOOST_PHASE_TICKS) {
                // Фаза разгона - яркое пламя
                ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
                    trailPos.x, trailPos.y, trailPos.z, 3, 0.03, 0.03, 0.03, 0.02, false);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.SOUL_FIRE_FLAME, 
                    trailPos.x, trailPos.y, trailPos.z, 1, 0.02, 0.02, 0.02, 0.01, false);
            } else {
                // Крейсерская фаза - меньше огня
                if (this.tickCount % 2 == 0) {
                    ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
                        trailPos.x, trailPos.y, trailPos.z, 1, 0.02, 0.02, 0.02, 0.01, false);
                }
            }
            
            // Искры от двигателя (редко)
            if (this.tickCount % 5 == 0) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.LAVA, 
                    trailPos.x, trailPos.y, trailPos.z, 1, 0.05, 0.05, 0.05, 0.02, false);
            }
        }
    }
    
    /**
     * Взрыв и удаление ракеты
     */
    private void explodeAndDiscard() {
        if (this.level() instanceof ServerLevel) {
            ProjectileTool.causeCustomExplode(this,
                    ModDamageTypes.causeProjectileExplosionDamage(this.level().registryAccess(), this, this.getOwner()),
                    this, this.explosionDamage, this.explosionRadius);
        }
        this.discard();
    }
    
    /**
     * Взрыв с осколками (радиовзрыватель)
     */
    private void explodeWithShrapnel() {
        if (this.level() instanceof ServerLevel serverLevel) {
            // Основной взрыв
            ProjectileTool.causeCustomExplode(this,
                    ModDamageTypes.causeProjectileExplosionDamage(this.level().registryAccess(), this, this.getOwner()),
                    this, this.explosionDamage, this.explosionRadius);
            
            // Эффекты осколков
            spawnShrapnelEffects(serverLevel);
            
            // Урон осколками
            damageEntitiesWithShrapnel();
        }
        this.discard();
    }
    
    /**
     * Эффекты разлёта осколков
     */
    private void spawnShrapnelEffects(ServerLevel serverLevel) {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        
        // Искры (осколки)
        ParticleTool.sendParticle(serverLevel, ParticleTypes.CRIT, x, y, z, 100, 1.5, 1.5, 1.5, 1.2, true);
        
        // Огненные искры
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, x, y, z, 60, 1.0, 1.0, 1.0, 0.8, true);
        
        // Лава (раскалённые осколки)
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LAVA, x, y, z, 40, 1.0, 1.0, 1.0, 0.6, true);
        
        // Дым
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, x, y, z, 30, 1.0, 1.0, 1.0, 0.4, true);
        
        // Звук разлёта осколков
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, 
                SoundSource.PLAYERS, 2.0F, 0.8F);
    }
    
    /**
     * Наносит урон осколками всем entity в радиусе
     */
    private void damageEntitiesWithShrapnel() {
        net.minecraft.world.phys.AABB area = this.getBoundingBox().inflate(SHRAPNEL_RANGE);
        List<Entity> entities = this.level().getEntities(this, area);
        
        for (Entity entity : entities) {
            // Пропускаем владельца и его технику
            if (this.getOwner() != null) {
                if (entity == this.getOwner()) continue;
                if (entity == this.getOwner().getVehicle()) continue;
            }
            
            double dist = this.distanceTo(entity);
            if (dist > SHRAPNEL_RANGE) continue;
            
            // Урон уменьшается с расстоянием
            float damage = SHRAPNEL_MAX_DAMAGE * (float)(1.0 - (dist / SHRAPNEL_RANGE));
            
            if (damage > 0) {
                // Наносим урон от осколков
                entity.hurt(this.damageSources().explosion(this, this.getOwner()), damage);
                
                // Сбрасываем неуязвимость для повторного урона
                if (entity instanceof LivingEntity living) {
                    living.invulnerableTime = 0;
                }
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        Entity entity = result.getEntity();
        
        if (this.getOwner() != null) {
            if (entity == this.getOwner()) return;
            if (this.getOwner().getVehicle() != null && entity == this.getOwner().getVehicle()) return;
        }
        
        if (this.level() instanceof ServerLevel) {
            if (this.getOwner() instanceof ServerPlayer player) {
                player.level().playSound(null, player.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);
                NetworkRegistry.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
            }
            
            DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);
            
            if (entity instanceof LivingEntity) {
                entity.invulnerableTime = 0;
            }
            
            // Прямое попадание - обычный взрыв (без осколков)
            explodeAndDiscard();
        }
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            BlockPos resultPos = blockHitResult.getBlockPos();
            float hardness = this.level().getBlockState(resultPos).getBlock().defaultDestroyTime();
            
            if (hardness != -1 && ExplosionConfig.EXPLOSION_DESTROY.get() && ExplosionConfig.EXTRA_EXPLOSION_EFFECT.get()) {
                this.level().destroyBlock(resultPos, true);
            }
            explodeAndDiscard();
        }
    }

    private PlayState movementPredicate(AnimationState<PantsirMissileEntity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.jvm.idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @NotNull SoundEvent getSound() {
        return ModSounds.ROCKET_FLY.get();
    }

    @Override
    public float getVolume() {
        return 0.5f;
    }

    @Override
    public boolean forceLoadChunk() {
        // Ракета НЕ форсит загрузку чанков цели
        // Она летит по позиции, а не по entity
        return true;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public float getGravity() {
        return 0;
    }
    
    /**
     * Устанавливает ID пусковой установки для обновления targetPos от радара
     */
    public void setLauncherId(int id) {
        this.launcherEntityId = id;
    }
    
    /**
     * Возвращает ID пусковой установки
     */
    public int getLauncherId() {
        return this.launcherEntityId;
    }
    
    /**
     * Устанавливает ID цели для наведения (работает для всех entity включая projectile)
     */
    public void setTargetEntityId(int id) {
        this.targetEntityId = id;
    }
    
    /**
     * Возвращает ID цели
     */
    public int getTargetEntityId() {
        return this.targetEntityId;
    }
    
    /**
     * Устанавливает начальный поворот ракеты по направлению полёта
     */
    public void setInitialRotation(Vec3 direction) {
        double horizontalDist = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float yaw = (float) (-Math.atan2(direction.x, direction.z) * 180.0 / Math.PI);
        float pitch = (float) (-Math.atan2(direction.y, horizontalDist) * 180.0 / Math.PI);
        
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
    }
}
