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
 * 
 * Mid-course: наведение по Vec3 от радара (>200 блоков)
 * Terminal: активное самонаведение по UUID (<200 блоков)
 */
public class PantsirMissileEntity extends MissileProjectile implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Параметры наведения
    private static final float MID_COURSE_TURN_RATE = 8.0f;   // Градусов/тик на дальней дистанции
    private static final float TERMINAL_TURN_RATE = 15.0f;    // Градусов/тик на ближней дистанции
    private static final double TERMINAL_RANGE = 200.0;       // Дистанция перехода в terminal mode
    private static final double PROXIMITY_FUSE_RANGE = 5.0;   // Дистанция срабатывания взрывателя
    private static final int MAX_LIFETIME = 400;              // Максимальное время жизни (20 сек)
    private static final int RADAR_UPDATE_INTERVAL = 10;      // Интервал обновления от радара (тиков)
    
    // Состояние
    private int launcherEntityId = -1;
    private boolean inTerminalPhase = false;
    private int updateCounter = 0;
    
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
        this.inTerminalPhase = compound.getBoolean("Terminal");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LauncherId", launcherEntityId);
        compound.putBoolean("Terminal", inTerminalPhase);
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
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            this.targetPos = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
        this.launcherEntityId = buffer.readInt();
    }

    @Override
    public void tick() {
        super.tick();
        spawnTrailParticles();
        
        if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
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
     */
    private void tickGuidance() {
        // Проверяем есть ли цель по UUID
        String targetUuid = this.entityData.get(TARGET_UUID);
        boolean hasUuidTarget = targetUuid != null && !targetUuid.equals("none");
        
        // Если нет ни UUID ни позиции - летим прямо
        if (!hasUuidTarget && targetPos == null) {
            maintainSpeed();
            return;
        }
        
        // Обновляем targetPos от цели по UUID
        Entity target = null;
        if (hasUuidTarget) {
            target = EntityFindUtil.findEntity(this.level(), targetUuid);
            if (target != null && target.isAlive()) {
                targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
                
                // Оповещаем цель о приближающейся ракете
                // Интервал зависит от дистанции - чем ближе, тем чаще пищит
                int warningInterval = (int) Math.max(0.04 * this.distanceTo(target), 2);
                if (this.tickCount % warningInterval == 0) {
                    // Оповещаем: VehicleEntity, технику с пассажирами, или игроков (флаеров)
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
        }
        
        if (targetPos == null) {
            maintainSpeed();
            return;
        }
        
        double distanceToTarget = this.position().distanceTo(targetPos);
        
        // Взрыватель близости
        if (distanceToTarget < PROXIMITY_FUSE_RANGE) {
            explodeAndDiscard();
            return;
        }
        
        // Переход в terminal phase
        if (!inTerminalPhase && distanceToTarget < TERMINAL_RANGE) {
            inTerminalPhase = true;
        }
        
        if (inTerminalPhase) {
            tickTerminalGuidance();
        } else {
            tickMidCourseGuidance();
        }
        
        maintainSpeed();
        updateRotationFromVelocity();
    }
    
    /**
     * Mid-course guidance: наведение по позиции от радара
     * Обновление targetPos каждые RADAR_UPDATE_INTERVAL тиков
     */
    private void tickMidCourseGuidance() {
        updateCounter++;
        if (updateCounter >= RADAR_UPDATE_INTERVAL) {
            updateCounter = 0;
            updateTargetFromRadar();
        }
        turnToTarget(MID_COURSE_TURN_RATE);
    }
    
    /**
     * Terminal guidance: активное самонаведение по UUID
     * Если цель доступна (чанк загружен) - обновляем позицию
     * Иначе летим по последней известной позиции
     */
    private void tickTerminalGuidance() {
        String targetUuid = this.entityData.get(TARGET_UUID);
        if (!targetUuid.equals("none")) {
            Entity target = EntityFindUtil.findEntity(this.level(), targetUuid);
            if (target != null && target.isAlive()) {
                // Цель доступна - обновляем позицию
                targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
            }
            // Если цель недоступна - продолжаем лететь по последней targetPos
        }
        turnToTarget(TERMINAL_TURN_RATE);
    }
    
    /**
     * Обновляет targetPos от радара Панциря
     * Вызывается только в mid-course phase
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
        
        if (pantsir != null) {
            Entity lockedTarget = pantsir.getLockedTarget();
            if (lockedTarget != null && lockedTarget.isAlive()) {
                // Обновляем позицию цели от радара
                targetPos = lockedTarget.position().add(0, lockedTarget.getBbHeight() * 0.5, 0);
                // Обновляем UUID для terminal phase
                this.entityData.set(TARGET_UUID, lockedTarget.getStringUUID());
            }
            // Если радар потерял цель - продолжаем лететь по последней позиции
        }
    }
    
    /**
     * Поворот ракеты к цели с ограничением скорости поворота
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
        
        // Не поворачиваем если цель позади (>90°)
        if (angle > 90) return;
        
        double turnFactor = Math.min(maxTurnRate / Math.max(angle, 0.1), 1.0);
        
        Vec3 newDirection = new Vec3(
            Mth.lerp(turnFactor, currentDirection.x, targetDirection.x),
            Mth.lerp(turnFactor, currentDirection.y, targetDirection.y),
            Mth.lerp(turnFactor, currentDirection.z, targetDirection.z)
        ).normalize();
        
        double speed = currentVelocity.length();
        this.setDeltaMovement(newDirection.scale(speed));
    }
    
    /**
     * Поддерживает скорость ракеты
     */
    private void maintainSpeed() {
        Vec3 velocity = this.getDeltaMovement();
        double currentSpeed = velocity.length();
        
        if (currentSpeed < 0.1) {
            this.setDeltaMovement(this.getLookAngle().scale(8.0));
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
}
