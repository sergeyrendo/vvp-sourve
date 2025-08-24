package tech.vvp.vvp.entity.projectile;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.ExplosiveProjectile;
import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.Optional;

/**
 * 40x53мм граната для гранатомёта MK19:
 * - баллистика (увеличенная гравитация)
 * - «арминг» после времени/дистанции
 * - отскок от блоков до армирования, после — взрыв при соприкосновении
 * - мягкий дымовой трейл
 */
public class Mk19GrenadeEntity extends FastThrowableProjectile implements GeoEntity, ExplosiveProjectile {

    private float damage = 18.0f;             // прямой урон по цели до взрыва/при касании
    private float explosionDamage = 28f;      // урон от взрыва
    private float explosionRadius = 3.2f;     // радиус взрыва
    private float gravity = 0.06f;            // баллистика 40мм
    private Explosion.BlockInteraction blockInteraction;

    // Армирование (граната не взорвётся раньше)
    private static final int   ARM_TICKS_MIN   = 10;   // ~0.5 сек
    private static final float ARM_TRAVEL_MIN  = 4.0f; // либо пролетит >= 4 блока
    private boolean armed = false;
    private float travelled = 0f;

    // Самоликвидация
    private static final int MAX_LIFETIME_TICKS = 120; // ~6 сек

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Mk19GrenadeEntity(EntityType<? extends Mk19GrenadeEntity> type, Level world) {
        super(type, world);
        this.noCulling = true;
    }

    public Mk19GrenadeEntity(LivingEntity shooter, Level level,
                             float damage, float explosionDamage, float explosionRadius) {
        super(tech.vvp.vvp.init.ModEntities.MK_19.get(), shooter, level); // <-- замените на свой EntityType при регистрации MK19
        this.noCulling = true;
        this.damage = damage;
        this.explosionDamage = explosionDamage;
        this.explosionRadius = explosionRadius;
    }

    public Mk19GrenadeEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(tech.vvp.vvp.init.ModEntities.MK_19.get(), level); // <-- замените на свой EntityType при регистрации MK19
    }

    public Mk19GrenadeEntity setBlockInteraction(Explosion.BlockInteraction blockInteraction) {
        this.blockInteraction = blockInteraction;
        return this;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Damage", this.damage);
        tag.putFloat("ExplosionDamage", this.explosionDamage);
        tag.putFloat("Radius", this.explosionRadius);
        tag.putBoolean("Armed", this.armed);
        tag.putFloat("Travelled", this.travelled);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Damage")) this.damage = tag.getFloat("Damage");
        if (tag.contains("ExplosionDamage")) this.explosionDamage = tag.getFloat("ExplosionDamage");
        if (tag.contains("Radius")) this.explosionRadius = tag.getFloat("Radius");
        if (tag.contains("Armed")) this.armed = tag.getBoolean("Armed");
        if (tag.contains("Travelled")) this.travelled = tag.getFloat("Travelled");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.AP_5_INCHES.get(); // <-- замените на свой предмет 40мм гранаты, если есть
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        return true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (this.getOwner() != null && this.getOwner().getVehicle() != null && entity == this.getOwner().getVehicle())
            return;

        if (!(this.level() instanceof ServerLevel server)) return;

        // лёгкий контактный урон (даже до арминга)
        DamageHandler.doDamage(entity, ModDamageTypes.causeProjectileHitDamage(server.registryAccess(), this, this.getOwner()), damage);
        if (entity instanceof LivingEntity) {
            entity.invulnerableTime = 0;
        }

        if (this.tickCount <= 0) return;

        if (this.armed) {
            causeExplode(result.getLocation(), true);
            this.discard();
        } else {
            // не взрываемся — «гасим» скорость и продолжаем полёт
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6));
        }
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = this.level().getBlockState(pos);

        if (this.level() instanceof ServerLevel server) {
            if (state.getBlock() instanceof BellBlock bell) {
                bell.attemptToRing(server, pos, hit.getDirection());
            }

            if (this.armed) {
                causeExplode(hit.getLocation(), false);
                this.discard();
                return;
            } else {
                // до арминга — упруго гасим скорость и отскакиваем
                bounce(hit.getDirection());
                if (!server.isClientSide()) {
                    ParticleTool.sendParticle(server, ParticleTypes.CLOUD,
                            this.getX(), this.getY() + 0.1, this.getZ(),
                            1, 0.04, 0.02, 0.04, 0.005, true);
                }
            }
        }
    }

    private void bounce(Direction normal) {
        Vec3 v = this.getDeltaMovement();
        switch (normal.getAxis()) {
            case X -> v = new Vec3(-v.x * 0.35, v.y * 0.5, v.z * 0.35);
            case Y -> v = new Vec3(v.x * 0.6, -v.y * 0.2, v.z * 0.6);
            case Z -> v = new Vec3(v.x * 0.35, v.y * 0.5, -v.z * 0.35);
        }
        this.setDeltaMovement(v);
    }

    private void causeExplode(Vec3 pos, boolean hitEntity) {
        new CustomExplosion.Builder(this)
                .attacker(this.getOwner())
                .damage(explosionDamage)
                .radius(explosionRadius)
                .position(pos)
                .causeVanillaExplosion()
                .withParticleType(ParticleTool.ParticleType.SMALL)
                .destroyBlock(() -> hitEntity
                        ? Explosion.BlockInteraction.KEEP
                        : (ExplosionConfig.EXPLOSION_DESTROY.get()
                        ? (this.blockInteraction != null ? this.blockInteraction : Explosion.BlockInteraction.DESTROY)
                        : Explosion.BlockInteraction.KEEP))
                .damageMultiplier(1.0F)
                .explode();
    }

    @Override
    public void tick() {
        super.tick();

        // арминга: по времени или пройденной дистанции
        if (!this.armed) {
            float dx = (float) (this.getX() - this.xo);
            float dy = (float) (this.getY() - this.yo);
            float dz = (float) (this.getZ() - this.zo);
            this.travelled += Mth.sqrt(dx * dx + dy * dy + dz * dz);
            if (this.tickCount >= ARM_TICKS_MIN || this.travelled >= ARM_TRAVEL_MIN) {
                this.armed = true;
            }
        }

        // дымовой след
        if (this.level() instanceof ServerLevel server && this.tickCount % 2 == 0) {
            ParticleTool.sendParticle(server, ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(),
                    1, 0.02, 0.01, 0.02, 0.01, true);
        }

        // остановка на земле без взрыва (неармированная может «уснуть»)
        if (onGround()) {
            this.setDeltaMovement(0, 0, 0);
        }

        // самоликвидация или подрыв в воде
        if (this.tickCount > MAX_LIFETIME_TICKS || this.isInWater()) {
            if (this.level() instanceof ServerLevel server) {
                if (this.armed) {
                    causeExplode(position(), false);
                } else {
                    // не армирована — просто «гасим» без взрыва
                    ParticleTool.sendParticle(server, ParticleTypes.CLOUD,
                            this.getX(), this.getY() + 0.1, this.getZ(),
                            2, 0.05, 0.02, 0.05, 0.003, true);
                }
            }
            this.discard();
        }
    }

    // (опционально) перехват гранат и ракет — оставлен как унаследованный пример,
    // можно удалить при ненадобности
    public void crushProjectile(Vec3 velocity) {
        if (this.level() instanceof ServerLevel) {
            var frontBox = getBoundingBox().inflate(0.4).expandTowards(velocity);
            Optional<Projectile> target = level().getEntities(
                            EntityTypeTest.forClass(Projectile.class), frontBox, e -> e != this).stream()
                    .filter(e -> (e.getBbWidth() >= 0.25 || e.getBbHeight() >= 0.25))
                    .min(Comparator.comparingDouble(e -> e.position().distanceTo(position())));
            if (target.isPresent() && this.armed) {
                causeExplode(target.get().position(), false);
                target.get().discard();
                this.discard();
            }
        }
    }

    @Override public void registerControllers(AnimatableManager.ControllerRegistrar data) { }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }

    @Override public void setDamage(float damage) { this.damage = damage; }
    @Override public void setExplosionDamage(float explosionDamage) { this.explosionDamage = explosionDamage; }
    @Override public void setExplosionRadius(float radius) { this.explosionRadius = radius; }

    @Override public float getGravity() { return this.gravity; }
    @Override public void setGravity(float gravity) { this.gravity = gravity; }
}

