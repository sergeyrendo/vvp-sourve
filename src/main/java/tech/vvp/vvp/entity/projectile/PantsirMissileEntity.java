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

    // Параметры ракеты 57Э6 (реалистичные, адаптированные для игры)
    private static final double MISSILE_SPEED = 10.0;         // Блоков/тик (~200 м/с в игре)
    private static final float MAX_TURN_RATE = 8.0f;          // Градусов/тик - реалистичный поворот (перегрузка ~20g)
    private static final float MAX_LEAD_ANGLE = 45.0f;        // Максимальный угол упреждения
    private static final double PROXIMITY_FUSE_MISSILE = 3.0; // Радиовзрыватель для ракет/баллистики - 3 блока
    private static final double PROXIMITY_FUSE_AIRCRAFT = 7.0;// Радиовзрыватель для самолётов/вертолётов - 7 блоков
    private static final double SHRAPNEL_RANGE = 15.0;        // Радиус поражения осколками
    private static final float SHRAPNEL_MAX_DAMAGE = 80.0f;   // Максимальный урон осколками
    private static final int MAX_LIFETIME = 400;              // Максимальное время жизни (20 сек)
    
    // Состояние
    private int launcherEntityId = -1;
    private int targetEntityId = -1;  // ID цели для поиска (работает для всех entity, не только LivingEntity)
    private boolean targetIsMissile = false; // Тип цели - ракета/баллистика или самолёт/вертолёт
    private double lastDistanceToTarget = Double.MAX_VALUE;   // Для радиовзрывателя
    private double minDistanceReached = Double.MAX_VALUE;     // Минимальная дистанция до цели
    
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
            // КРИТИЧНО: На первом тике сразу ищем панцирь и цель
            if (this.tickCount == 1) {
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
     * Проверяет наличие decoy/flare и переключается на них (как в Agm65Entity)
     */
    private void checkForDecoy() {
        // Ищем decoy в радиусе 32 блоков с углом 90 градусов
        List<Entity> decoy = SeekTool.seekLivingEntities(this, 32, 90);
        
        for (var e : decoy) {
            if (e.getType().is(ModTags.EntityTypes.DECOY) && !this.distracted) {
                // Переключаемся на decoy
                this.entityData.set(TARGET_UUID, e.getStringUUID());
                this.distracted = true;
                break;
            }
        }
    }
    
    /**
     * Основная логика наведения (только на сервере)
     * Использует пропорциональное наведение для перехвата быстрых целей
     */
    private void tickGuidance() {
        // Обновляем цель от радара каждый тик
        updateTargetFromRadar();
        
        // Ищем цель по ID (работает для ВСЕХ entity, включая projectile)
        Entity target = null;
        if (targetEntityId != -1) {
            target = this.level().getEntity(targetEntityId);
        }
        
        // Fallback на UUID для совместимости с обычными ракетами
        if (target == null) {
            String targetUuid = this.entityData.get(TARGET_UUID);
            if (targetUuid != null && !targetUuid.equals("none")) {
                target = EntityFindUtil.findEntity(this.level(), targetUuid);
                if (target != null) {
                    targetEntityId = target.getId();
                    targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                }
            }
        }
        
        // Если нашли цель - вычисляем точку перехвата
        if (target != null && target.isAlive()) {
            Vec3 targetCenter = target.position().add(0, target.getBbHeight() * 0.5, 0);
            Vec3 targetVelocity = target.getDeltaMovement();
            
            // Вычисляем точку перехвата с упреждением
            targetPos = calculateInterceptPoint(targetCenter, targetVelocity);
            
            // Определяем тип цели для радиовзрывателя
            targetIsMissile = isTargetMissile(target);
            
            // Оповещаем цель о приближающейся ракете
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
        
        // Если нет targetPos - летим прямо (но продолжаем искать цель)
        if (targetPos == null) {
            maintainSpeed();
            return;
        }
        
        double distanceToTarget = this.position().distanceTo(targetPos);
        
        // Запоминаем минимальную дистанцию
        if (distanceToTarget < minDistanceReached) {
            minDistanceReached = distanceToTarget;
        }
        
        // Радиовзрыватель: разный радиус для ракет (3) и самолётов/вертолётов (7)
        double fuseRange = targetIsMissile ? PROXIMITY_FUSE_MISSILE : PROXIMITY_FUSE_AIRCRAFT;
        
        // Вариант 1: Сразу взрываемся если достаточно близко
        if (distanceToTarget < fuseRange) {
            explodeWithShrapnel();
            return;
        }
        
        // Вариант 2: Если пролетели мимо (дистанция начала увеличиваться) и были близко
        if (distanceToTarget > lastDistanceToTarget + 0.5 && minDistanceReached < fuseRange * 2) {
            explodeWithShrapnel();
            return;
        }
        
        lastDistanceToTarget = distanceToTarget;
        
        // Наводимся на точку перехвата с реалистичным ограничением поворота
        turnToTarget(MAX_TURN_RATE);
        
        maintainSpeed();
        updateRotationFromVelocity();
    }
    
    /**
     * Вычисляет точку перехвата с учётом скорости цели
     * Использует итеративный метод для нахождения точки где ракета встретит цель
     */
    private Vec3 calculateInterceptPoint(Vec3 targetPos, Vec3 targetVelocity) {
        Vec3 missilePos = this.position();
        
        // Используем константную скорость ракеты
        double missileSpeed = MISSILE_SPEED;
        
        // Скорость цели
        double targetSpeed = targetVelocity.length();
        
        // Если цель стоит - целимся прямо на неё
        if (targetSpeed < 0.1) {
            return targetPos;
        }
        
        // Итеративно вычисляем время до перехвата
        double distance = missilePos.distanceTo(targetPos);
        double timeToIntercept = distance / missileSpeed;
        
        // 3 итерации для уточнения
        for (int i = 0; i < 3; i++) {
            // Где будет цель через timeToIntercept тиков
            Vec3 predictedPos = targetPos.add(targetVelocity.scale(timeToIntercept));
            
            // Новое расстояние до предсказанной позиции
            distance = missilePos.distanceTo(predictedPos);
            timeToIntercept = distance / missileSpeed;
        }
        
        // Финальная предсказанная позиция
        Vec3 interceptPoint = targetPos.add(targetVelocity.scale(timeToIntercept));
        
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
     * Поворот ракеты к цели с реалистичным ограничением
     * Ракета НЕ может разворачиваться на 180° - если цель позади, ракета промахивается
     */
    private void turnToTarget(float maxTurnRate) {
        if (targetPos == null) return;
        
        Vec3 currentVelocity = this.getDeltaMovement();
        if (currentVelocity.lengthSqr() < 0.001) return;
        
        Vec3 toTarget = targetPos.subtract(this.position());
        Vec3 targetDirection = toTarget.normalize();
        Vec3 currentDirection = currentVelocity.normalize();
        
        double dot = currentDirection.dot(targetDirection);
        double angle = Math.toDegrees(Math.acos(Mth.clamp(dot, -1.0, 1.0)));
        
        // РЕАЛИСТИЧНОЕ ОГРАНИЧЕНИЕ: если цель под углом > 60° - ракета не может навестись
        // Это предотвращает нереалистичные развороты на 180°
        if (angle > 60) {
            // Ракета продолжает лететь прямо - промах неизбежен
            return;
        }
        
        // Ограничиваем скорость поворота (реалистичная перегрузка)
        double actualTurnRate = Math.min(maxTurnRate, MAX_TURN_RATE);
        double turnFactor = Math.min(actualTurnRate / Math.max(angle, 0.1), 1.0);
        
        Vec3 newDirection = new Vec3(
            Mth.lerp(turnFactor, currentDirection.x, targetDirection.x),
            Mth.lerp(turnFactor, currentDirection.y, targetDirection.y),
            Mth.lerp(turnFactor, currentDirection.z, targetDirection.z)
        ).normalize();
        
        double speed = currentVelocity.length();
        this.setDeltaMovement(newDirection.scale(speed));
    }
    
    /**
     * Поддерживает скорость ракеты на постоянном уровне
     */
    private void maintainSpeed() {
        Vec3 velocity = this.getDeltaMovement();
        double currentSpeed = velocity.length();
        
        // Поддерживаем постоянную скорость ракеты
        if (currentSpeed < MISSILE_SPEED * 0.9 || currentSpeed > MISSILE_SPEED * 1.1) {
            Vec3 direction = velocity.lengthSqr() > 0.001 ? velocity.normalize() : this.getLookAngle();
            this.setDeltaMovement(direction.scale(MISSILE_SPEED));
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
    private void spawnTrailParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 2, 0.1, 0.1, 0.1, 0.02, false);
            if (this.tickCount % 2 == 0) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, pos.x, pos.y, pos.z, 1, 0.05, 0.05, 0.05, 0.01, false);
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
}
