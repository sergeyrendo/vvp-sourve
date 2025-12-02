package tech.vvp.vvp.entity.projectile;

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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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

public class BallisticMissileEntity extends ThrowableProjectile implements GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private Vec3 targetPos;
    private int ticksAlive = 0;
    private boolean exploded = false;
    private Vec3 explosionPos = null;
    private int explosionSmokeTicks = 0;

    private static final TicketType<Entity> MISSILE_TICKET = TicketType.create("ballistic_missile", (e1, e2) -> 0);
    private ChunkPos currentTicketChunk = null;
    private ChunkPos targetTicketChunk = null;
    private ChunkPos aheadTicketChunk = null;

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
        this.ticksAlive = 0;
        this.setDeltaMovement(new Vec3(0, 5.0, 0));
    }

    @Override
    public void tick() {
        super.tick();
        ticksAlive++;

        if (this.exploded && explosionSmokeTicks > 0 && explosionPos != null) {
            explosionSmokeTicks--;
            if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
                ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, explosionPos.x, explosionPos.y + 1.0, explosionPos.z, 8, 1.5, 0.5, 1.5, 0.1, false);
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, explosionPos.x, explosionPos.y + 1.0, explosionPos.z, 5, 1.2, 0.4, 1.2, 0.08, false);
            }
            if (explosionSmokeTicks <= 0) {
                this.discard();
            }
            return;
        }

        if (this.exploded || this.isRemoved()) return;

        // Chunk loading
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            ChunkPos newChunk = new ChunkPos(this.blockPosition());
            if (currentTicketChunk == null || !newChunk.equals(currentTicketChunk)) {
                if (currentTicketChunk != null) {
                    serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 3, this);
                }
                serverLevel.getChunkSource().addRegionTicket(MISSILE_TICKET, newChunk, 3, this);
                currentTicketChunk = newChunk;
            }
            if (this.targetPos != null) {
                ChunkPos newTargetChunk = new ChunkPos((int) Math.floor(targetPos.x) >> 4, (int) Math.floor(targetPos.z) >> 4);
                if (targetTicketChunk == null || !newTargetChunk.equals(targetTicketChunk)) {
                    if (targetTicketChunk != null) {
                        serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, targetTicketChunk, 3, this);
                    }
                    serverLevel.getChunkSource().addRegionTicket(MISSILE_TICKET, newTargetChunk, 3, this);
                    targetTicketChunk = newTargetChunk;
                }
            }
        }

        if (this.targetPos != null) {
            Vec3 currentPos = this.position();
            Vec3 currentVelocity = this.getDeltaMovement();
            Vec3 toTarget = targetPos.subtract(currentPos);
            double distance = toTarget.length();
            double horizontalDistance = Math.sqrt(Math.pow(targetPos.x - currentPos.x, 2) + Math.pow(targetPos.z - currentPos.z, 2));
            double heightAboveTarget = currentPos.y - targetPos.y;

            boolean shouldExplode = distance <= 3.0 || (horizontalDistance <= 5.0 && heightAboveTarget <= 2.0) || this.isInWater() || this.onGround();

            if (shouldExplode) {
                this.stopChunk();
                this.explode();
                this.discard();
                return;
            }

            double maxSpeed = 3.0;
            int boostTicks = 20;
            if (distance < 200) boostTicks = 10;
            if (distance < 100) boostTicks = 5;
            if (distance < 50) boostTicks = 2;

            if (ticksAlive < boostTicks) {
                this.setDeltaMovement(currentVelocity.scale(0.98));
            } else {
                Vec3 targetDirection = toTarget.normalize();
                Vec3 currentDirection = currentVelocity.normalize();
                double turnRate = 0.15;
                if (distance < 100) turnRate = 0.3;
                if (distance < 50) turnRate = 0.5;
                if (distance < 20) turnRate = 1.0;

                Vec3 newDirection = new Vec3(
                        Mth.lerp(turnRate, currentDirection.x, targetDirection.x),
                        Mth.lerp(turnRate, currentDirection.y, targetDirection.y),
                        Mth.lerp(turnRate, currentDirection.z, targetDirection.z)
                ).normalize();

                double currentSpeed = currentVelocity.length();
                double newSpeed = currentSpeed < maxSpeed ? currentSpeed + 0.15 : maxSpeed;
                this.setDeltaMovement(newDirection.scale(newSpeed));

                this.setXRot((float)(Mth.atan2(newDirection.y, Math.sqrt(newDirection.x * newDirection.x + newDirection.z * newDirection.z)) * (180F / (float)Math.PI)));
                this.setYRot((float)(Mth.atan2(newDirection.x, newDirection.z) * (180F / (float)Math.PI)));
            }

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.ticksAlive == 1 && !this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 15, 0.8, 0.8, 0.8, 0.01, true);
                this.level().playSound(null, this.blockPosition(), ModSounds.MISSILE_START.get(), SoundSource.PLAYERS, 4.0F, 1.0F);
            }

            spawnTrailParticles();
        }
    }


    private void spawnTrailParticles() {
        if (this.level().isClientSide) return;
        ServerLevel serverLevel = (ServerLevel) this.level();
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 15, 0.5, 0.5, 0.5, 0.05, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 10, 0.4, 0.4, 0.4, 0.04, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY(), this.getZ(), 8, 0.3, 0.3, 0.3, 0.03, true);
        if (ticksAlive < 40) {
            ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 12, 0.3, 0.3, 0.3, 0.05, true);
            ParticleTool.sendParticle(serverLevel, ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 3, 0.1, 0.1, 0.1, 0.02, true);
        }
    }

    private void explode() {
        if (this.exploded) return;
        this.exploded = true;
        if (this.level().isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) this.level();
        explosionPos = this.position();
        explosionSmokeTicks = 200;

        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 80, 2.0, 2.0, 2.0, 0.3, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 60, 1.5, 1.5, 1.5, 0.2, true);
        this.level().playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 4.0F, 0.8F);

        Level.ExplosionInteraction interaction = ExplosionConfig.EXPLOSION_DESTROY.get() ? Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE;
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 8.0F, interaction);

        spawnShrapnelEffects(serverLevel);
        damageEntitiesWithShrapnel(26.0, 200.0f);
        stopChunk();
    }

    private void spawnShrapnelEffects(ServerLevel serverLevel) {
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(), 800, 3.0, 3.0, 3.0, 1.5, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LAVA, this.getX(), this.getY(), this.getZ(), 600, 2.5, 2.5, 2.5, 1.2, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(), 400, 2.0, 2.0, 2.0, 1.0, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 480, 2.5, 2.5, 2.5, 1.8, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 20, 0.5, 0.5, 0.5, 0, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 320, 2.0, 2.0, 2.0, 0.8, true);
        ParticleTool.sendParticle(serverLevel, ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 200, 3.0, 3.0, 3.0, 1.5, true);
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 3.0F, 0.7F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.PLAYERS, 3.0F, 0.9F);
    }

    private void damageEntitiesWithShrapnel(double range, float maxDamage) {
        var area = this.getBoundingBox().inflate(range);
        var entities = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity entity : entities) {
            if (entity != this.getOwner()) {
                double dist = this.distanceTo(entity);
                float damage = maxDamage * (float)(1.0 - (dist / range));
                if (damage > 0) {
                    entity.hurt(this.damageSources().explosion(this, this.getOwner()), damage);
                }
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) explode();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!this.level().isClientSide) explode();
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return ticksAlive;
    }

    private void stopChunk() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            if (currentTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, currentTicketChunk, 3, this);
                currentTicketChunk = null;
            }
            if (targetTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, targetTicketChunk, 3, this);
                targetTicketChunk = null;
            }
            if (aheadTicketChunk != null) {
                serverLevel.getChunkSource().removeRegionTicket(MISSILE_TICKET, aheadTicketChunk, 3, this);
                aheadTicketChunk = null;
            }
        }
    }
}
