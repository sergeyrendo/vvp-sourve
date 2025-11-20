package tech.vvp.vvp.entity.weapon;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
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
import java.util.List;
import net.minecraftforge.network.PlayMessages;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.init.ModEntities;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BallisticMissileEntity extends ThrowableProjectile implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Фазы полета GMLRS
    private enum FlightPhase {
        BOOST,      // Разгон (2-3 сек)
        COAST,      // Баллистический полет с коррекцией
        TERMINAL    // Финальное наведение и разделение
    }

    private Vec3 targetPos;
    private Vec3 launchPos;
    private FlightPhase currentPhase = FlightPhase.BOOST;
    private int ticksLived;
    private int phaseTimer = 0;
    private boolean warheadSeparated = false;
    private int correctionCounter = 0;

    // Константы GMLRS
    private static final int BOOST_DURATION = 50; // 2.5 секунды
    private static final double BOOST_ACCELERATION = 0.1; // Плавный разгон
    private static final double MAX_SPEED = 3.5; // Кинематографичная скорость
    private static final double GRAVITY = 0.05;
    private static final double COURSE_CORRECTION_RATE = 0.7;
    private static final int CORRECTION_INTERVAL = 5;
    private static final double SEPARATION_DISTANCE_PERCENT = 0.88;
    private static final int SUBMUNITIONS_COUNT = 4; // 4 Смарт-суббоеприпаса
    
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
        // Добавляем похибку ±10 блоков (CEP = 10м для GMLRS)
        java.util.Random random = new java.util.Random();
        double errorX = (random.nextDouble() - 0.5) * 20.0; // ±10 блоков
        double errorZ = (random.nextDouble() - 0.5) * 20.0; // ±10 блоков
        
        this.targetPos = targetPos.add(errorX, 0, errorZ);
        this.launchPos = this.position();
        this.ticksLived = 0;
        
        // Начальная скорость вверх
        Vec3 toTarget = this.targetPos.subtract(this.position()).normalize();
        this.setDeltaMovement(toTarget.scale(0.5).add(0, 1.5, 0)); // Медленный старт
    }

    @Override
    public void tick() {
        super.tick();
        this.ticksLived++;
        this.phaseTimer++;
        this.correctionCounter++;

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

        if (this.targetPos != null && !warheadSeparated) {
            updateFlightPhase();
            updateMovement();
            spawnTrailParticles();
            checkSeparationConditions();
        }
    }


    private void updateFlightPhase() {
        switch (currentPhase) {
            case BOOST:
                if (phaseTimer >= BOOST_DURATION) {
                    currentPhase = FlightPhase.COAST;
                    phaseTimer = 0;
                }
                break;
            case COAST:
                double distToTarget = this.position().distanceTo(targetPos);
                double totalDist = launchPos.distanceTo(targetPos);
                if (distToTarget / totalDist < (1.0 - SEPARATION_DISTANCE_PERCENT)) {
                    currentPhase = FlightPhase.TERMINAL;
                    phaseTimer = 0;
                }
                break;
            case TERMINAL:
                // Финальная фаза - ждем разделения
                break;
        }
    }

    private void checkSeparationConditions() {
        // Надёжное разделение боеголовки
        // 1) После определённого времени полёта (30 тиков)
        // 2) При достаточном приближении к цели по горизонтали (< 60 блоков)
        // 3) Экстренный взрыв, если слишком близко к цели (<5 блоков)
        if (!warheadSeparated) {
            if (ticksLived > 30) {
                separateWarhead();
                return;
            }
            double dx = this.getX() - targetPos.x;
            double dz = this.getZ() - targetPos.z;
            double horizontalDist = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDist < 60.0) {
                separateWarhead();
                return;
            }
            if (this.position().distanceTo(targetPos) < 5.0) {
                explodeOnImpact();
            }
        }
    }

    private void updateMovement() {
        Vec3 currentVel = this.getDeltaMovement();
        
        if (currentPhase == FlightPhase.BOOST) {
            // Фаза разгона - плавное ускорение
            Vec3 toTarget = targetPos.subtract(this.position()).normalize();
            Vec3 acceleration = toTarget.scale(BOOST_ACCELERATION);
            Vec3 newVel = currentVel.add(acceleration).add(0, -GRAVITY * 0.5, 0);
            
            if (newVel.length() > MAX_SPEED) {
                newVel = newVel.normalize().scale(MAX_SPEED);
            }
            this.setDeltaMovement(newVel);
            
        } else if (currentPhase == FlightPhase.COAST) {
            // Баллистический полет с плавной коррекцией
            if (correctionCounter >= CORRECTION_INTERVAL) {
                Vec3 toTarget = targetPos.subtract(this.position()).normalize();
                Vec3 currentDir = currentVel.normalize();
                Vec3 correction = toTarget.subtract(currentDir).scale(COURSE_CORRECTION_RATE * 0.01);
                Vec3 newVel = currentVel.add(correction).add(0, -GRAVITY, 0);
                this.setDeltaMovement(newVel);
                correctionCounter = 0;
            } else {
                this.setDeltaMovement(currentVel.add(0, -GRAVITY, 0));
            }
            
        } else if (currentPhase == FlightPhase.TERMINAL) {
            // Финальное наведение
            Vec3 toTarget = targetPos.subtract(this.position()).normalize();
            Vec3 newVel = currentVel.scale(0.98).add(toTarget.scale(0.1)).add(0, -GRAVITY * 1.5, 0);
            this.setDeltaMovement(newVel);
        }
        
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void spawnTrailParticles() {
        if (this.level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) this.level();
        
        if (currentPhase == FlightPhase.BOOST) {
            // Мощный след при разгоне
            ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
                this.getX(), this.getY(), this.getZ(), 5, 0.1, 0.1, 0.1, 0.01, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, 
                this.getX(), this.getY(), this.getZ(), 4, 0.2, 0.2, 0.2, 0.01, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, 
                this.getX(), this.getY(), this.getZ(), 2, 0.1, 0.1, 0.1, 0.01, true);
        } else {
            // След в полете
            if (ticksLived % 2 == 0) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, 
                    this.getX(), this.getY(), this.getZ(), 1, 0.05, 0.05, 0.05, 0, true);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, 
                    this.getX(), this.getY(), this.getZ(), 1, 0.1, 0.1, 0.1, 0, true);
            }
        }
    }

    private void separateWarhead() {
        if (warheadSeparated || this.level().isClientSide) return;
        warheadSeparated = true;
        
        ServerLevel serverLevel = (ServerLevel) this.level();
        
        // Эффект разделения
        ParticleTool.sendParticle(serverLevel, ParticleTypes.EXPLOSION_EMITTER, 
            this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0, true);
        
        ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, 
            this.getX(), this.getY(), this.getZ(), 20, 1.0, 1.0, 1.0, 0.1, true);
        
        this.level().playSound(null, this.blockPosition(), ModSounds.MISSILE_START.get(), 
            SoundSource.PLAYERS, 3.0F, 0.8F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, 
            SoundSource.PLAYERS, 2.0F, 1.5F);
        
        // Выпускаем 4 суббоеприпаса по квадрантам
        releaseSubmunitions();
        
        // Удаляем корпус
        this.stopChank();
        this.discard();
    }

    private void releaseSubmunitions() {
        // 4 квадранта: NE, SE, SW, NW
        double[][] quadrants = {
            {1, 1}, {1, -1}, {-1, -1}, {-1, 1}
        };
        
        for (int i = 0; i < SUBMUNITIONS_COUNT; i++) {
            double[] quad = quadrants[i % 4];
            
            // Смещение в сторону квадранта (15 блоков для покрытия большей площади)
            double offsetX = quad[0] * (15.0 + Math.random() * 5.0);
            double offsetZ = quad[1] * (15.0 + Math.random() * 5.0);
            
            Vec3 subTargetPos = this.targetPos.add(offsetX, 0, offsetZ);
            
            // Вектор выброса в сторону квадранта (уменьшаем скорость разлета)
            Vec3 ejectVelocity = new Vec3(quad[0] * 0.8, 0.5, quad[1] * 0.8);
            
            spawnSubmunition(this.position(), ejectVelocity, subTargetPos);
        }
    }

    private void spawnSubmunition(Vec3 pos, Vec3 velocity, Vec3 target) {
        SubmunitionEntity submunition = new SubmunitionEntity(this.level(), pos, velocity, this.getOwner(), target);
        this.level().addFreshEntity(submunition);
    }

    private void explodeOnImpact() {
        if (!warheadSeparated) {
            // Основной взрыв (если не разделилась) - уменьшенный радиус
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 
                3.0F, (Boolean)ExplosionConfig.EXPLOSION_DESTROY.get() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
            
            // Ручной урон по сущностям в большом радиусе
            damageEntitiesInRange(23.0, 50.0f);
        }
        this.stopChank();
        this.discard();
    }
    
    private void damageEntitiesInRange(double range, float damage) {
        AABB area = this.getBoundingBox().inflate(range);
        List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            if (entity != this.getOwner()) {
                double dist = this.distanceTo(entity);
                float finalDamage = damage * (float)(1.0 - (dist / range));
                if (finalDamage > 0) {
                    entity.hurt(this.damageSources().explosion(this, this.getOwner()), finalDamage);
                }
            }
        }
    }

    @Override
    public void onHitBlock(BlockHitResult blockHitResult) {
        explodeOnImpact();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        explodeOnImpact();
    }

    @Override
    public boolean isNoGravity() {
        return false;
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
        return this.cache;
    }

    @Override
    public double getTick(Object o) {
        return this.tickCount;
    }

    @Override
    protected void defineSynchedData() {
    }

    private void stopChank(){
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (currentTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 10, this);
                currentTicketChunk = null;
            }
        }
    }

    @Override
    public boolean shouldBeSaved() {
        return true;
    }

    // Внутренний класс для суббоеприпасов GMLRS SMArt
    private static class SubmunitionEntity extends ThrowableProjectile implements net.minecraft.world.entity.projectile.ItemSupplier {
        private Vec3 velocity;
        private Vec3 targetPos;
        private int ticksAlive = 0;
        
        private enum Phase {
            EJECT,      // Выброс в сторону
            CHUTE,      // Спуск на парашюте (медленно)
            FREEFALL    // Свободное падение (атака)
        }
        
        private Phase phase = Phase.EJECT;
        private int phaseTimer = 0;

        public SubmunitionEntity(Level level, Vec3 pos, Vec3 velocity, Entity owner, Vec3 targetPos) {
            super(EntityType.SNOWBALL, level); // Используем SNOWBALL как базу
            this.setPos(pos.x, pos.y, pos.z);
            this.setOwner(owner);
            this.setInvisible(true); // Скрываем сам предмет
            this.velocity = velocity;
            this.targetPos = targetPos;
            this.noPhysics = false;
        }

        @Override
        public ItemStack getItem() {
            return new ItemStack(Items.TNT);
        }

        @Override
        public void tick() {
            // Не вызываем super.tick() чтобы полностью контролировать движение
            // Но нам нужно обновлять базовые поля
            this.baseTick();
            
            ticksAlive++;
            phaseTimer++;
            
            if (this.level().isClientSide) {
                spawnParticles();
                // Клиентская интерполяция
                this.setPos(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
                return;
            }

            // Логика фаз
            switch (phase) {
                case EJECT:
                    // Быстрое торможение горизонтальной скорости
                    velocity = velocity.multiply(0.96, 0.98, 0.96);
                    velocity = velocity.add(0, -0.05, 0); // Гравитация
                    
                    if (phaseTimer > 20) {
                        phase = Phase.CHUTE;
                        phaseTimer = 0;
                        this.level().playSound(null, this.blockPosition(), SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 1.0F, 0.5F);
                    }
                    break;
                    
                case CHUTE:
                    // Медленный спуск
                    double descentSpeed = -0.15;
                    // Случайный дрейф (увеличен)
                    double driftX = (Math.random() - 0.5) * 0.1;
                    double driftZ = (Math.random() - 0.5) * 0.1;
                    
                    velocity = new Vec3(velocity.x * 0.95 + driftX, descentSpeed, velocity.z * 0.95 + driftZ);
                    
                    if (phaseTimer > 60 + Math.random() * 40) {
                        phase = Phase.FREEFALL;
                        phaseTimer = 0;
                        this.level().playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0F, 0.5F);
                    }
                    break;
                    
                case FREEFALL:
                    // Ускорение вниз
                    velocity = velocity.add(0, -0.1, 0);
                    velocity = velocity.add((Math.random() - 0.5) * 0.02, 0, (Math.random() - 0.5) * 0.02);
                    break;
            }
            
            this.setPos(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);
            
            // Взрыв при контакте
            if (this.onGround() || (this.getY() - targetPos.y < 1.0 && phase == Phase.FREEFALL) || ticksAlive > 400) {
                explode();
            }
            
            if (!this.level().getBlockState(this.blockPosition()).isAir()) {
                explode();
            }
        }

        @Override
        protected void onHitBlock(BlockHitResult result) {
            super.onHitBlock(result);
            explode();
        }

        @Override
        protected void onHitEntity(EntityHitResult result) {
            super.onHitEntity(result);
            explode();
        }
        
        private void spawnParticles() {
            if (phase == Phase.CHUTE) {
                for (int i = 0; i < 2; i++) {
                    this.level().addParticle(ParticleTypes.CLOUD, 
                        this.getX() + (Math.random() - 0.5) * 0.5, 
                        this.getY() + 0.5, 
                        this.getZ() + (Math.random() - 0.5) * 0.5, 
                        0, 0.05, 0);
                }
            } else if (phase == Phase.FREEFALL) {
                for (int i = 0; i < 3; i++) {
                    this.level().addParticle(ParticleTypes.FLAME, 
                        this.getX() + (Math.random() - 0.5) * 0.2, 
                        this.getY(), 
                        this.getZ() + (Math.random() - 0.5) * 0.2, 
                        0, 0, 0);
                }
                this.level().addParticle(ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0.1, 0);
            }
        }

        private void explode() {
            if (this.level().isClientSide || this.isRemoved()) {
                this.discard();
                return;
            }
            
            ServerLevel serverLevel = (ServerLevel) this.level();
            
            // Взрыв суббоеприпаса GMLRS M77 - шрапнельный, БЕЗ воронки
            // Малый взрыв только для эффекта, не разрушает блоки
            this.level().explode(
                this, 
                this.getX(), 
                this.getY(), 
                this.getZ(), 
                0.5F, // Малый взрыв без воронки
                Level.ExplosionInteraction.NONE // Не разрушает блоки
            );
            
            // Визуальные эффекты взрыва
            ParticleTool.sendParticle(serverLevel, ParticleTypes.EXPLOSION, 
                this.getX(), this.getY(), this.getZ(), 3, 0.3, 0.3, 0.3, 0, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, 
                this.getX(), this.getY(), this.getZ(), 8, 0.4, 0.4, 0.4, 0.1, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, 
                this.getX(), this.getY(), this.getZ(), 5, 0.5, 0.5, 0.5, 0.05, true);
            
            // Звук взрыва
            this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, 
                SoundSource.PLAYERS, 1.2F, 0.9F + (float)Math.random() * 0.3F);
            
            // ШРАПНЕЛЬНЫЙ урон - одинаковый по всей площади поражения
            // M77 содержит готовые поражающие элементы
            AABB damageArea = new AABB(
                this.getX() - 6.0, this.getY() - 6.0, this.getZ() - 6.0,
                this.getX() + 6.0, this.getY() + 6.0, this.getZ() + 6.0
            );
            
            List<Entity> allEntities = this.level().getEntities(this, damageArea);
            for (Entity entity : allEntities) {
                if (entity == this || entity == this.getOwner()) continue;
                
                double distance = entity.position().distanceTo(this.position());
                if (distance < 6.0) {
                    // Шрапнельный урон - ОДИНАКОВЫЙ по всей площади
                    float damage = 20.0F;
                    
                    // Наносим урон от шрапнели
                    entity.hurt(
                        this.damageSources().explosion(this, this.getOwner() instanceof LivingEntity ? (LivingEntity)this.getOwner() : null), 
                        damage
                    );
                }
            }
            
            ParticleTool.sendParticle(serverLevel, ParticleTypes.EXPLOSION, 
                this.getX(), this.getY(), this.getZ(), 3, 0.5, 0.5, 0.5, 0, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.CRIT, 
                this.getX(), this.getY(), this.getZ(), 50, 5.0, 5.0, 5.0, 0.5, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, 
                this.getX(), this.getY(), this.getZ(), 20, 3.0, 3.0, 3.0, 0.1, true);
            
            this.discard();
        }

        @Override
        protected void defineSynchedData() {
        }

        @Override
        public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        }

        @Override
        public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag compound) {
        }
    }
}
