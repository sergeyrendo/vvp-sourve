package tech.vvp.vvp.entity.weapon;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.init.ModEntities;

import java.util.List;

public class BallisticMissileEntity extends ThrowableProjectile implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private Vec3 targetPos;
    private int ticksAlive = 0;
    private boolean exploded = false;
    
    private static final double GRAVITY = 0.05; // Гравитация
    private static final double MAX_SPEED = 2.0; // Максимальная скорость (уменьшена)
    private static final double GUIDANCE_STRENGTH = 0.3; // Сила наведения (увеличена)
    
    private static final TicketType<Entity> MISSILE_TICKET = TicketType.create("ballistic_missile", (entity1, entity2) -> 0);
    private ChunkPos currentTicketChunk = null;

    public BallisticMissileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.BALLISTIC_MISSILE.get(), shooter, level);
    }

    public BallisticMissileEntity(EntityType<? extends BallisticMissileEntity> type, Level level) {
        super(type, level);
    }

    public BallisticMissileEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.BALLISTIC_MISSILE.get(), level);
    }

    public void setTargetPosition(Vec3 targetPos) {
        this.targetPos = targetPos;
        
        // Простая начальная скорость - направление к цели + вверх
        Vec3 toTarget = targetPos.subtract(this.position()).normalize();
        
        // Начальная скорость: направление к цели + подъем вверх
        Vec3 initialVelocity = toTarget.scale(1.0).add(0, 1.5, 0);
        this.setDeltaMovement(initialVelocity);
    }

    @Override
    public void tick() {
        if (this.exploded || this.isRemoved()) {
            return;
        }
        
        super.tick();
        ticksAlive++;

        // Управление чанками
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            ChunkPos newChunk = new ChunkPos(this.blockPosition());
            if (currentTicketChunk == null || !newChunk.equals(currentTicketChunk)) {
                if (currentTicketChunk != null) {
                    serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                }
                serverLevel.getChunkSource().addRegionTicket(MISSILE_TICKET, newChunk, 10, this);
                currentTicketChunk = newChunk;
            }
        }

        if (this.targetPos != null) {
            updateMovement();
            spawnTrailParticles();
            checkImpact();
        }
    }

    private void updateMovement() {
        Vec3 currentVel = this.getDeltaMovement();
        
        // Применяем гравитацию
        Vec3 newVel = currentVel.add(0, -GRAVITY, 0);
        
        // Активное наведение на цель
        Vec3 toTarget = targetPos.subtract(this.position());
        double distToTarget = toTarget.length();
        
        // Постоянно корректируем направление к цели
        Vec3 targetDir = toTarget.normalize();
        Vec3 correction = targetDir.scale(GUIDANCE_STRENGTH);
        newVel = newVel.add(correction);
        
        // Ограничиваем скорость
        if (newVel.length() > MAX_SPEED) {
            newVel = newVel.normalize().scale(MAX_SPEED);
        }
        
        this.setDeltaMovement(newVel);
        
        // Поворачиваем ракету по направлению движения ПЕРЕД перемещением
        double horizontalSpeed = Math.sqrt(newVel.x * newVel.x + newVel.z * newVel.z);
        
        // Yaw (горизонтальное направление)
        float yaw = (float)(Math.atan2(newVel.x, newVel.z) * 180.0 / Math.PI);
        
        // Pitch (вертикальное направление)
        // Вычисляем угол направления полета
        float pitch = (float)(Math.atan2(newVel.y, horizontalSpeed) * 180.0 / Math.PI);
        
        // Добавляем offset 90 градусов если модель направлена вниз по умолчанию
        pitch = pitch - 90.0f;
        
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
        
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void spawnTrailParticles() {
        if (this.level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) this.level();
        
        // Дымный след
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, 
            this.getX(), this.getY(), this.getZ(), 3, 0.1, 0.1, 0.1, 0.01, true);
        
        ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, 
            this.getX(), this.getY(), this.getZ(), 2, 0.08, 0.08, 0.08, 0.008, true);
        
        // Огонь от двигателя (первые 2 секунды)
        if (ticksAlive < 40) {
            ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
                this.getX(), this.getY(), this.getZ(), 4, 0.1, 0.1, 0.1, 0.015, true);
        }
    }

    private void checkImpact() {
        if (this.targetPos == null) return;
        
        // Проверяем расстояние до цели
        double distToTarget = this.position().distanceTo(targetPos);
        
        // Взрываемся если близко к цели (в радиусе 5 блоков)
        if (distToTarget < 5.0) {
            explode();
            return;
        }
        
        // Взрываемся если ударились о блок
        if (this.horizontalCollision || this.verticalCollision) {
            explode();
            return;
        }
        
        // Самоуничтожение через 30 секунд (600 тиков)
        if (ticksAlive > 600) {
            explode();
        }
    }

    private void explode() {
        if (this.exploded) return;
        this.exploded = true;
        
        if (this.level().isClientSide()) {
            return;
        }
        
        ServerLevel serverLevel = (ServerLevel) this.level();
        
        // Эффекты взрыва разделения
        ParticleTool.sendParticle(serverLevel, ParticleTypes.EXPLOSION_EMITTER, 
            this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0, true);
        
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, 
            this.getX(), this.getY(), this.getZ(), 20, 1.0, 1.0, 1.0, 0.1, true);
        
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
            this.getX(), this.getY(), this.getZ(), 15, 0.8, 0.8, 0.8, 0.08, true);
        
        // Звук взрыва
        this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, 
            SoundSource.PLAYERS, 4.0F, 0.8F);
        
        // Маленький взрыв разделения (не наносит урон блокам)
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 
            2.0F, Level.ExplosionInteraction.NONE);
        
        // ШРАПНЕЛЬ - красивые эффекты
        spawnShrapnelEffects(serverLevel);
        
        // Прямой урон от шрапнели всем сущностям в радиусе
        // Радиус: 20 * 1.3 = 26 блоков
        // Урон: 100 * 2 = 200 (гарантированно убивает игрока в незерите)
        damageEntitiesWithShrapnel(26.0, 200.0f);
        
        stopChank();
        this.discard();
    }
    
    private void spawnShrapnelEffects(ServerLevel serverLevel) {
        // КРАСИВЫЕ ЭФФЕКТЫ ШРАПНЕЛИ - разлетаются на 20 блоков
        
        // Огненные искры во все стороны (как фейерверк) - быстрые
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FIREWORK, 
            this.getX(), this.getY(), this.getZ(), 200, 3.0, 3.0, 3.0, 1.5, true);
        
        // Лава (раскаленные осколки) - средняя скорость
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LAVA, 
            this.getX(), this.getY(), this.getZ(), 150, 2.5, 2.5, 2.5, 1.2, true);
        
        // Огонь - быстрый
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
            this.getX(), this.getY(), this.getZ(), 100, 2.0, 2.0, 2.0, 1.0, true);
        
        // Искры - очень быстрые
        ParticleTool.sendParticle(serverLevel, ParticleTypes.CRIT, 
            this.getX(), this.getY(), this.getZ(), 120, 2.5, 2.5, 2.5, 1.8, true);
        
        // Белые вспышки в центре
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLASH, 
            this.getX(), this.getY(), this.getZ(), 5, 0.5, 0.5, 0.5, 0, true);
        
        // Дым от осколков - медленный
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, 
            this.getX(), this.getY(), this.getZ(), 80, 2.0, 2.0, 2.0, 0.8, true);
        
        // Дополнительные эффекты для дальности
        // Взрывные частицы
        ParticleTool.sendParticle(serverLevel, ParticleTypes.EXPLOSION, 
            this.getX(), this.getY(), this.getZ(), 50, 3.0, 3.0, 3.0, 1.5, true);
        
        // Красная пыль (как кровь от осколков)
        ParticleTool.sendParticle(serverLevel, ParticleTypes.CRIMSON_SPORE, 
            this.getX(), this.getY(), this.getZ(), 100, 3.0, 3.0, 3.0, 1.3, true);
        
        // Дополнительный звук разлета осколков
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, 
            SoundSource.PLAYERS, 3.0F, 0.7F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, 
            SoundSource.PLAYERS, 3.0F, 0.9F);
    }
    
    private void damageEntitiesWithShrapnel(double range, float maxDamage) {
        net.minecraft.world.phys.AABB area = this.getBoundingBox().inflate(range);
        java.util.List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, area);
        
        for (LivingEntity entity : entities) {
            if (entity != this.getOwner()) {
                double dist = this.distanceTo(entity);
                
                // Урон уменьшается с расстоянием
                float damage = maxDamage * (float)(1.0 - (dist / range));
                
                if (damage > 0) {
                    // Наносим урон от шрапнели
                    entity.hurt(this.damageSources().explosion(this, this.getOwner()), damage);
                }
            }
        }
    }


    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            explode();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) {
            explode();
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    
    @Override
    public double getTick(Object object) {
        return ticksAlive;
    }
    
    private void stopChank() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (currentTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                currentTicketChunk = null;
            }
        }
    }
}
