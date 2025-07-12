package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.VehicleConfigVVP;

import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ArmedVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.VectorTool;

// import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.atsuishio.superbwarfare.entity.OBBEntity;

// Импортируем необходимые классы для атрибутов
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;

public class M997Entity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, ArmedVehicleEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public OBB obb;
    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;


    public M997Entity(EntityType<? extends M997Entity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(1.3125f, 1.0625f, 1.65625f), new Quaternionf(), OBB.Part.BODY);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(1.3125f, 1.0f, 0.46875f), new Quaternionf(), OBB.Part.BODY); 
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(1.3125f, 0.3125f, 0.8125f), new Quaternionf(), OBB.Part.BODY); 
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.234375f, 0.3125f, 0.375f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.234375f, 0.3125f, 0.375f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(0.234375f, 0.3125f, 0.375f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(0.234375f, 0.3125f, 0.375f), new Quaternionf(), OBB.Part.ENGINE1);
    }

    // Добавляем статический метод для создания атрибутов
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)  // Тигр легче Абрамса
                .add(Attributes.MOVEMENT_SPEED, 1.0D) // Тигр быстрее
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0D);
    }

    @SuppressWarnings("unchecked")
    public static M997Entity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null; 
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
             Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
             return null;
        }

        EntityType<M997Entity> castedEntityType = (EntityType<M997Entity>) entityTypeFromPacket;
        M997Entity entity = new M997Entity(castedEntityType, world);
        return entity;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/m997_icon.png");
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(2.75, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        // Добавьте здесь любые дополнительные данные для сохранения, если они есть
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        // Восстановите здесь любые дополнительные данные, если они есть
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.15f + 1.05f);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .multiply(0.2f)
                .multiply(1.5f, DamageTypes.ARROW)
                .multiply(1.5f, DamageTypes.TRIDENT)
                .multiply(2.5f, DamageTypes.MOB_ATTACK)
                .multiply(2f, DamageTypes.MOB_ATTACK_NO_AGGRO)
                .multiply(1.5f, DamageTypes.MOB_PROJECTILE)
                .multiply(12.5f, DamageTypes.LAVA)
                .multiply(6f, DamageTypes.EXPLOSION)
                .multiply(6f, DamageTypes.PLAYER_EXPLOSION)
                .multiply(2.4f, ModDamageTypes.CUSTOM_EXPLOSION)
                .multiply(2f, ModDamageTypes.PROJECTILE_BOOM)
                .multiply(0.75f, ModDamageTypes.MINE)
                .multiply(1.5f, ModDamageTypes.CANNON_FIRE)
                .multiply(0.25f, ModTags.DamageTypes.PROJECTILE)
                .multiply(0.85f, ModTags.DamageTypes.PROJECTILE_ABSOLUTE)
                .multiply(10f, ModDamageTypes.VEHICLE_STRIKE)
                .reduce(7);
    }

    public double getSubmergedHeight(Entity entity) {
        for (FluidType fluidType : ForgeRegistries.FLUID_TYPES.get().getValues()) {
            if (entity.level().getFluidState(entity.blockPosition()).getFluidType() == fluidType)
                return entity.getFluidTypeHeight(fluidType);
        }
        return 0;
    }

    @Override
    public void baseTick() {
        turretYRotO = this.getTurretYRot();
        turretXRotO = this.getTurretXRot();
        rudderRotO = this.getRudderRot();
        leftWheelRotO = this.getLeftWheelRot();
        rightWheelRotO = this.getRightWheelRot();

        super.baseTick();
        updateOBB();

        if (this.onGround()) {
            float f0 = 0.54f + 0.25f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.05 * this.getDeltaMovement().horizontalDistance())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.85, f0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.95, 0.99));
        }

        lowHealthWarning();
        this.terrainCompact(2.7f, 3.61f);
        inertiaRotate(1.25f);


        this.refreshDimensions();
    }

    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.09 || Mth.abs(this.entityData.get(POWER)) > 0.15;
    }

    @Override
    public void travel() {
        Entity passenger0 = this.getFirstPassenger();

        if (this.getEnergy() <= 0) return;

        if (passenger0 == null) {
            this.leftInputDown = false;
            this.rightInputDown = false;
            this.forwardInputDown = false;
            this.backInputDown = false;
            this.entityData.set(POWER, 0f);
        }

        if (forwardInputDown) {
            this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + (this.entityData.get(POWER) < 0 ? 0.014f : 0.0036f), 0.26f));
        }

        if (backInputDown) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.entityData.get(POWER) > 0 ? 0.014f : 0.0036f), -0.15f));
        }

        if (rightInputDown) {
            this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.11f);
        } else if (this.leftInputDown) {
            this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.11f);
        }

        if (this.forwardInputDown || this.backInputDown) {
            this.consumeEnergy(VehicleConfigVVP.TYPHOON_ENERGY_COST.get());
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * (upInputDown ? 0.5f : (rightInputDown || leftInputDown) ? 0.977f : 0.99f));
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * (float) Math.max(0.76f - 0.1f * this.getDeltaMovement().horizontalDistance(), 0.3));

        float angle = (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1));
        double s0;

        if (Mth.abs(angle) < 90) {
            s0 = this.getDeltaMovement().horizontalDistance();
        } else {
            s0 = -this.getDeltaMovement().horizontalDistance();
        }

        this.setLeftWheelRot((float) ((this.getLeftWheelRot() - 1.25 * s0) - this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5f * this.entityData.get(DELTA_ROT), -5f, 5f)));
        this.setRightWheelRot((float) ((this.getRightWheelRot() - 1.25 * s0) + this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5f * this.entityData.get(DELTA_ROT), -5f, 5f)));

        this.setRudderRot(Mth.clamp(this.getRudderRot() - this.entityData.get(DELTA_ROT), -0.8f, 0.8f) * 0.75f);

        this.setYRot((float) (this.getYRot() - Math.max(10 * this.getDeltaMovement().horizontalDistance(), 0) * this.getRudderRot() * (this.entityData.get(POWER) > 0 ? 1 : -1)));
        if (onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale(this.entityData.get(POWER))));
        }
    }


    @Override
    public SoundEvent getEngineSound() {
        return tech.vvp.vvp.init.ModSounds.HUMVEE_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Mth.abs(entityData.get(POWER)) * 2f;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1);

        int i = this.getSeatIndex(passenger);
        Vector4f worldPosition;

        switch(i) {
            case 0: // Водитель (слева спереди)
                worldPosition = transformPosition(transform, 0.7f, 0.30f, 0.2f); 
                break;
            case 1: // Пассажир рядом с водителем
                worldPosition = transformPosition(transform, -0.5f, 0.30f, 0.3f); 
                break;
            case 2: // Пассажир сзади слева
                worldPosition = transformPosition(transform, 0.7f, 0.70f, -1.3f);
                break;
            case 3: // Пассажир сзади справа
                worldPosition = transformPosition(transform, -0.7f, 0.70f, -1.3f);
                break;
            default:
                worldPosition = transformPosition(transform, 0, 1, 0);
                break;
        }

        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public int getMaxPassengers() {
        return 4; // Водитель + 3 пассажира (типичная компоновка седана)
    }

    @Override
    public void destroy() {
        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), getAttacker(), getAttacker()), 80f,
                    this.getX(), this.getY(), this.getZ(), 5f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            net.minecraftforge.event.ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnMediumExplosionParticles(this.level(), this.position());
        }

        explodePassengers();
        super.destroy();
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        // Ничего не делаем здесь, чтобы предотвратить вращение турели при повороте головы пассажира
    }

    private PlayState idlePredicate(AnimationState<M997Entity> event) {
        if (Mth.abs((float)this.getDeltaMovement().horizontalDistanceSqr()) > 0.001 || Mth.abs(this.entityData.get(POWER)) > 0.05) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
    }
    
    // Реализация методов ArmedVehicleEntity - заглушки, так как оружия у нас больше нет
    
    @Override
    public int mainGunRpm(Player player) {
        return 0; // Нет оружия
    }

    @Override
    public boolean canShoot(Player player) {
        return false; // Нет оружия
    }

    @Override
    public int getAmmoCount(Player player) {
        return 0; // Нет боеприпасов
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        // Ничего не делаем, т.к. стрелять невозможно
    }
    
    @Override
    public int zoomFov() {
        return 0; // Нет оптического прицела
    }
    
    @Override
    public int getWeaponHeat(Player player) {
        return 0; // Нет нагрева оружия
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        // Пассажиры внутри автомобиля видны
        return false;
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return 0.3; // Нормальная чувствительность для всех пассажиров
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (isFirstPerson) {
            return new Vec2(Mth.lerp(partialTicks, player.yHeadRotO, player.getYHeadRot()), 
                           Mth.lerp(partialTicks, player.xRotO, player.getXRot()));
        }
        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (isFirstPerson) {
            // В режиме от первого лица камера находится примерно на уровне глаз
            return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), 
                           Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), 
                           Mth.lerp(partialTicks, player.zo, player.getZ()));
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }
    
    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6);
    }

    // @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0f, 2.0625f, -1.59375f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition1 = transformPosition(transform, 0f, 2.0f, 0.53125f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0f, 1.3125f, 1.8125f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.078125f, 0.5f, 1.9375f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.078125f, 0.5f, 1.9375f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, 1.078125f, 0.5f, -1.875f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition6 = transformPosition(transform, -1.078125f, 0.5f, -1.875f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));


    }    
}
