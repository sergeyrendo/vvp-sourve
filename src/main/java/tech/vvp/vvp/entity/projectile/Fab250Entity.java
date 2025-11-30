package tech.vvp.vvp.entity.projectile;

import com.atsuishio.superbwarfare.entity.projectile.FastThrowableProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.ProjectileTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
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
import tech.vvp.vvp.config.server.ExplosionConfigVVP;
import tech.vvp.vvp.init.ModEntities;

public class Fab250Entity extends FastThrowableProjectile implements GeoEntity {

    public static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(Fab250Entity.class, EntityDataSerializers.FLOAT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final DamageModifier DAMAGE_MODIFIER = DamageModifier.createDefaultModifier()
            .immuneTo(ModEntities.FAB_250.get());

    public Fab250Entity(EntityType<? extends Fab250Entity> type, Level world) {
        super(type, world);
        this.noCulling = true;
        this.explosionDamage = ExplosionConfigVVP.FAB_250_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ExplosionConfigVVP.FAB_250_EXPLOSION_RADIUS.get().floatValue();
        this.gravity = 0.06f;
    }

    public Fab250Entity(LivingEntity entity, Level level) {
        super(ModEntities.FAB_250.get(), entity, level);
        this.noCulling = true;
        this.explosionDamage = ExplosionConfigVVP.FAB_250_EXPLOSION_DAMAGE.get();
        this.explosionRadius = ExplosionConfigVVP.FAB_250_EXPLOSION_RADIUS.get().floatValue();
        this.gravity = 0.06f;
    }

    public Fab250Entity(PlayMessages.SpawnEntity spawnEntity, Level level) {
        this(ModEntities.FAB_250.get(), level);
    }

    public Fab250Entity(EntityType<? extends ThrowableItemProjectile> pEntityType, double pX, double pY, double pZ, Level pLevel) {
        super(pEntityType, pX, pY, pZ, pLevel);
        this.noCulling = true;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.MEDIUM_AERIAL_BOMB.get();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        var newAmount = DAMAGE_MODIFIER.compute(source, amount);
        this.entityData.set(HEALTH, this.entityData.get(HEALTH) - newAmount);
        return super.hurt(source, amount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HEALTH, 50f);
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Health")) {
            this.entityData.set(HEALTH, compound.getFloat("Health"));
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Health", this.entityData.get(HEALTH));
    }

    @Override
    public void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level() instanceof ServerLevel) {
            ProjectileTool.causeCustomExplode(this, this.explosionDamage, this.explosionRadius, 1.2f);
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 600 || this.entityData.get(HEALTH) <= 0) {
            if (!this.level().isClientSide) {
                ProjectileTool.causeCustomExplode(this, this.explosionDamage, this.explosionRadius, 1.2f);
            }
            this.discard();
        }
    }

    private PlayState movementPredicate(AnimationState<Fab250Entity> event) {
        return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.mk_82.start"));
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
    @NotNull
    public SoundEvent getSound() {
        return ModSounds.SHELL_FLY.get();
    }

    @Override
    public float getVolume() {
        return 0.7f;
    }

    @Override
    public boolean shouldSyncMotion() {
        return true;
    }
}
