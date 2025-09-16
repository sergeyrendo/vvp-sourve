package tech.vvp.vvp.entity.projectile;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.ExplosiveProjectile;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.projectile.GrapeshotEntity;
import com.atsuishio.superbwarfare.entity.projectile.GunGrenadeEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.network.message.receive.ClientMotionSyncMessage;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

public class CannonAtgmShellEntity extends FastThrowableProjectile implements GeoEntity, ExplosiveProjectile {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float damage = 0;
    private float radius = 0;
    private float explosionDamage = 0;
    private float fireProbability = 0;
    private int fireTime = 0;
    private float gravity = 0.1f;

    public enum Type {
        AP, HE, CM, GRAPE, ATGM // <-- добавлен новый тип (управляемый снаряд)
    }

    private Type type = Type.AP;
    private int sparedAmount = 50;
    private int sparedAngle = 15;
    private int sparedTime = 7;

    public CannonAtgmShellEntity(EntityType<? extends CannonAtgmShellEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
    }

    public CannonAtgmShellEntity(LivingEntity entity, Level world, float damage, float radius, float explosionDamage,
                                 float fireProbability, int fireTime, float gravity,
                                 Type type, int sparedAmount, int sparedTime, int sparedAngle) {
        super(ModEntities.CANNON_SHELL.get(), entity, world);
        this.noCulling = true;
        this.damage = damage;
        this.radius = radius;
        this.explosionDamage = explosionDamage;
        this.fireProbability = fireProbability;
        this.fireTime = fireTime;
        this.gravity = gravity;
        this.type = type;
        this.sparedAmount = sparedAmount;
        this.sparedTime = sparedTime;
        this.sparedAngle = sparedAngle;
    }

    public CannonAtgmShellEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(tech.vvp.vvp.init.ModEntities.CANNON_ATGM_SHELL.get(), level);
    }

    public CannonAtgmShellEntity durability(int durability) {
        this.durability = durability;
        return this;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isColliding(BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);

        pCompound.putFloat("Damage", this.damage);
        pCompound.putFloat("ExplosionDamage", this.explosionDamage);
        pCompound.putFloat("Radius", this.radius);
        pCompound.putFloat("FireProbability", this.fireProbability);
        pCompound.putInt("FireTime", this.fireTime);
        pCompound.putInt("Durability", this.durability);
        pCompound.putString("ShellType", this.type.name());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);

        if (pCompound.contains("Damage")) this.damage = pCompound.getFloat("Damage");
        if (pCompound.contains("ExplosionDamage")) this.explosionDamage = pCompound.getFloat("ExplosionDamage");
        if (pCompound.contains("Radius")) this.radius = pCompound.getFloat("Radius");
        if (pCompound.contains("FireProbability")) this.fireProbability = pCompound.getFloat("FireProbability");
        if (pCompound.contains("FireTime")) this.fireTime = pCompound.getInt("FireTime");
        if (pCompound.contains("Durability")) this.durability = pCompound.getInt("Durability");
        if (pCompound.contains("ShellType")) {
            try {
                this.type = Type.valueOf(pCompound.getString("ShellType"));
            } catch (Exception e) {
                this.type = Type.AP;
            }
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.HE_5_INCHES.get();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            if (type != Type.AP) {
                causeExplode(blockHitResult.getLocation());
                this.discard();
                return;
            }
            BlockPos resultPos = blockHitResult.getBlockPos();
            float hardness = this.level().getBlockState(resultPos).getBlock().defaultDestroyTime();
            if (hardness != -1) {
                if (ExplosionConfig.EXPLOSION_DESTROY.get()) {
                    if (firstHit) {
                        causeExplode(blockHitResult.getLocation());
                        firstHit = false;
                        Mod.queueServerWork(3, this::discard);
                    }
                    this.level().destroyBlock(resultPos, true);
                }
            } else {
                causeExplode(blockHitResult.getLocation());
                this.discard();
            }
            if (!ExplosionConfig.EXPLOSION_DESTROY.get()) {
                causeExplode(blockHitResult.getLocation());
                this.discard();
            }
        }
    }

    @Override
    public void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (this.level() instanceof ServerLevel) {
            Entity entity = entityHitResult.getEntity();
            if (this.getOwner() != null && entity == this.getOwner().getVehicle())
                return;

            if (type == Type.GRAPE) {
                DamageHandler.doDamage(entity, ModDamageTypes.causeGrapeShotHitDamage(this.level().registryAccess(), this, this.getOwner()), 0.5f * this.damage);
            } else {
                DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(this.level().registryAccess(), this, this.getOwner()), this.damage);
            }

            if (entity instanceof LivingEntity) entity.invulnerableTime = 0;

            if (this.getOwner() instanceof LivingEntity living) {
                if (!living.level().isClientSide() && living instanceof ServerPlayer player) {
                    living.level().playSound(null, living.blockPosition(), ModSounds.INDICATION.get(), SoundSource.VOICE, 1, 1);
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> player), new ClientIndicatorMessage(0, 5));
                }
            }

            ParticleTool.cannonHitParticles(this.level(), this.position(), this);
            causeExplode(entityHitResult.getLocation());
            if (entity instanceof VehicleEntity) this.discard();
        }
    }

    @Override
    public void tick() {
        if (type == Type.GRAPE) {
            releaseGrapeShot((LivingEntity) getOwner());
        }
        super.tick();

        if (this.level() instanceof ServerLevel serverLevel && tickCount > 1) {
            double l = getDeltaMovement().length();
            for (double i = 0; i < l; i++) {
                Vec3 startPos = new Vec3(this.xo, this.yo, this.zo);
                Vec3 pos = startPos.add(getDeltaMovement().normalize().scale(-i));
                ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y, pos.z,
                        1, 0, 0, 0, 0.001, true);
            }
        }

        destroyBlock();

        // === Управляемый ПТУР ===
        if (type == Type.ATGM && tickCount > 5 && this.getOwner() != null && getOwner().getVehicle() instanceof VehicleEntity vehicle) {
            Entity shooter = this.getOwner();

            Vec3 lookVec = vehicle.getBarrelVec(1).normalize();
            Vec3 vec3 = TraceTool.vehicleFindLookingPos(this, vehicle, vehicle.getNewEyePos(1), 512);
            Vec3 toVec;

            if (vec3 != null) {
                toVec = this.position().vectorTo(vec3).normalize();
            } else {
                BlockHitResult result = level().clip(new ClipContext(
                        vehicle.getNewEyePos(1),
                        vehicle.getNewEyePos(1).add(lookVec.scale(512)),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE,
                        shooter
                ));
                Vec3 hitPos = result.getLocation();
                toVec = this.position().vectorTo(hitPos).normalize();
            }

            setDeltaMovement(getDeltaMovement().add(toVec.scale(0.8)));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.8, 0.8));
        }

        if (this.tickCount > 600) {
            if (this.level() instanceof ServerLevel) causeExplode(position());
            this.discard();
        }

        if (type == Type.CM && tickCount > 3) {
            BlockHitResult hitResult = level().clip(new ClipContext(
                    position(),
                    position().add(getDeltaMovement().scale(sparedTime)),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.ANY,
                    this
            ));
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                releaseClusterMunitions((LivingEntity) getOwner());
            }

            Entity target = TraceTool.findLookingEntity(this, getDeltaMovement().scale(sparedTime).length());
            if (target != null && target != this) {
                releaseClusterMunitions((LivingEntity) getOwner());
            }
        }
    }

    public void releaseClusterMunitions(LivingEntity shooter) {
        if (level() instanceof ServerLevel serverLevel) {
            ParticleTool.spawnMediumExplosionParticles(serverLevel, position());
            for (int index0 = 0; index0 < sparedAmount; index0++) {
                GunGrenadeEntity gunGrenadeEntity = new GunGrenadeEntity(shooter, serverLevel,
                        6 * damage / sparedAmount,
                        5 * explosionDamage / sparedAmount,
                        radius / 2
                );
                gunGrenadeEntity.setPos(position().x, position().y, position().z);
                gunGrenadeEntity.shoot(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z,
                        (float) (random.nextFloat() * 0.2f + 0.4f * getDeltaMovement().length()),
                        sparedAngle);
                serverLevel.addFreshEntity(gunGrenadeEntity);
            }
            discard();
        }
    }

    public void releaseGrapeShot(LivingEntity shooter) {
        if (level() instanceof ServerLevel serverLevel) {
            for (int index0 = 0; index0 < sparedAmount; index0++) {
                GrapeshotEntity grapeProjectileEntity = new GrapeshotEntity(shooter, serverLevel, damage / sparedAmount);
                grapeProjectileEntity.setPos(this.xo, this.yo, this.zo);
                grapeProjectileEntity.shoot(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z,
                        (float) (random.nextFloat() * 0.2f + 0.9f * getDeltaMovement().length()),
                        sparedAngle);
                serverLevel.addFreshEntity(grapeProjectileEntity);
            }
            discard();
        }
    }

    @Override
    public void syncMotion() {
        if (!this.level().isClientSide) {
            Mod.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new ClientMotionSyncMessage(this));
        }
    }

    @Override
    public void causeExplode(Vec3 vec3) {
        new CustomExplosion.Builder(this)
                .attacker(this.getOwner())
                .damage(explosionDamage)
                .radius(radius)
                .position(vec3)
                .withParticleType(radius > 9 ? ParticleTool.ParticleType.HUGE : ParticleTool.ParticleType.MEDIUM)
                .explode();

        discard();
    }

    private PlayState movementPredicate(AnimationState<CannonAtgmShellEntity> event) {
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.cannon_shell.idle"));
    }

    @Override
    public float getGravity() {
        if (this.type == Type.ATGM) return 0f; // управляемый снаряд летит ровно
        return this.gravity;
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
        return ModSounds.SHELL_FLY.get();
    }

    @Override
    public float getVolume() {
        return 0.07f;
    }

    @Override
    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public void setExplosionDamage(float damage) {
        this.explosionDamage = damage;
    }

    @Override
    public void setExplosionRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public boolean forceLoadChunk() {
        return true;
    }

    @Override
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
}