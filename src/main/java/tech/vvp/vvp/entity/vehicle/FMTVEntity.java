package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;

import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tech.vvp.vvp.VVP;

import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.VectorTool;

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

// Импортируем необходимые классы для атрибутов
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Mob;
import tech.vvp.vvp.config.server.VehicleConfigVVP;

public class FMTVEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, ArmedVehicleEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(FMTVEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> HAS_TENT = SynchedEntityData.defineId(FMTVEntity.class, EntityDataSerializers.BOOLEAN);

    public OBB obb;
    public OBB obb1;
    public OBB obbTent;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;
    public OBB obb7;
    public OBB obb8;

    public FMTVEntity(EntityType<? extends FMTVEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(1.406f, 0.938f, 1.156f), new Quaternionf(), OBB.Part.BODY);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(1.469f, 0.250f, 3.938f), new Quaternionf(), OBB.Part.BODY);
        this.obbTent = new OBB(this.position().toVector3f(), new Vector3f(1.469f, 0.688f, 2.563f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.352f, 0.398f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.352f, 0.398f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(0.352f, 0.398f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(0.352f, 0.398f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb7 = new OBB(this.position().toVector3f(), new Vector3f(0.352f, 0.398f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb8 = new OBB(this.position().toVector3f(), new Vector3f(0.352f, 0.398f, 0.375f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
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
    public static FMTVEntity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<FMTVEntity> castedEntityType = (EntityType<FMTVEntity>) entityTypeFromPacket;
        FMTVEntity entity = new FMTVEntity(castedEntityType, world);
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
        return VVP.loc("textures/vehicle_icon/fmtv_icon.png");
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(2.75, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(HAS_TENT, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putBoolean("has_tent", this.entityData.get(HAS_TENT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(HAS_TENT, compound.getBoolean("has_tent"));
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.15f + 1.05f);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    public void baseTick() {
        turretYRotO = this.getTurretYRot();
        turretXRotO = this.getTurretXRot();
        rudderRotO = this.getRudderRot();
        leftWheelRotO = this.getLeftWheelRot();
        rightWheelRotO = this.getRightWheelRot();

        super.baseTick();
        this.updateOBB();

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
        wheelEngine(true, 0.052, VehicleConfigVVP.HUMVEE_ENERGY_COST.get(), 1.25, 1.5, 0.25f, -0.13f, 0.0024f, 0.0024f, 0.1f);
    }


    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.BMP_ENGINE.get();
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
            case 0:
                worldPosition = transformPosition(transform, 0.625f, 1.27f, 2.828f);
                break;
            case 1:
                worldPosition = transformPosition(transform, -0.625f, 1.27f, 2.828f);
                break;
            case 2:
                worldPosition = transformPosition(transform, -1.063f, 1.8f, 0.203f);
                break;
            case 3:
                worldPosition = transformPosition(transform, -1.063f, 1.8f, -0.984f);
                break;
            case 4:
                worldPosition = transformPosition(transform, -1.063f, 1.8f, -2.047f);
                break;
            case 5:
                worldPosition = transformPosition(transform, 1.063f, 1.8f, 0.203f);
                break;
            case 6:
                worldPosition = transformPosition(transform, 1.063f, 1.8f, -0.984f);
                break;
            case 7:
                worldPosition = transformPosition(transform, 1.063f, 1.8f, -2.047f);
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
        return 8; // Водитель + 3 пассажира (типичная компоновка седана)
    }


    @Override
    public void onPassengerTurned(Entity entity) {
        // Ничего не делаем здесь, чтобы предотвратить вращение турели при повороте головы пассажира
    }

    private PlayState idlePredicate(AnimationState<FMTVEntity> event) {
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
        if (this.entityData.get(HAS_TENT)) {
            return List.of(this.obb, this.obb1, this.obbTent, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7, this.obb8);
        } else {
            return List.of(this.obb, this.obbTent, this.obb3, this.obb4, this.obb5, this.obb6, this.obb7, this.obb8);
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(tech.vvp.vvp.init.ModItems.TENT.get()) && !this.entityData.get(HAS_TENT)) {
            return loadModule(player, stack, HAS_TENT, tech.vvp.vvp.init.ModItems.TENT.get());
        }

        // Универсальное удаление с ключом (один if для всех флагов)
        if (stack.is(tech.vvp.vvp.init.ModItems.WRENCH.get())) {
            // Проверяем флаги по порядку (можно сделать цикл для всех)
            if (this.entityData.get(HAS_TENT)) {
                return removeModule(player, HAS_TENT, tech.vvp.vvp.init.ModItems.TENT.get());
            }
        }

        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {  // Только на сервере
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 2;  // Количество типов (default=0, desert=1, forest=2)
                int newType = (currentType + 1) % maxTypes;  // Цикл: 0→1→2→0
                this.entityData.set(CAMOUFLAGE_TYPE, newType);  // Сохраняем новый тип

                // Опционально: Звук и эффект (например, частицы)
                this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.SPRAY.get(), this.getSoundSource(), 1.0F, 1.0F);  // Пример звука (замени на свой)
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1, this.getZ(), 10, 1.0, 1.0, 1.0, 0.1);  // Частицы успеха
                }

                return InteractionResult.CONSUME;  // Consume — прерываем, не даём войти
            } else {
                return InteractionResult.SUCCESS;  // Success на клиенте для отклика
            }
        }

        // Если ничего не подошло — базовая логика (вход/инвентарь)
        return super.interact(player, hand);
    }

    // Новая функция для загрузки (чтобы избежать дубликатов)
    private InteractionResult loadModule(Player player, ItemStack stack, EntityDataAccessor<Boolean> flag, Item returnItem) {
        if (!this.level().isClientSide) {
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.entityData.set(flag, true);
            this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.REMONT.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    // Новая функция для удаления (чтобы избежать дубликатов)
    private InteractionResult removeModule(Player player, EntityDataAccessor<Boolean> flag, Item returnItem) {
        if (!this.level().isClientSide) {
            this.entityData.set(flag, false);
            ItemStack returnedItem = new ItemStack(returnItem, 1);
            boolean addedToInventory = player.getInventory().add(returnedItem);
            if (!addedToInventory) {
                ItemEntity droppedItem = new ItemEntity(this.level(), this.getX(), this.getY() + 1, this.getZ(), returnedItem);
                this.level().addFreshEntity(droppedItem);
            }
            this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.REMONT.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    // @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0.031f, 2.500f, 2.781f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        if (this.entityData.get(HAS_TENT)) {
            Vector4f worldPosition1 = transformPosition(transform, -0.031f, 1.625f, 0.000f);
            this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
            this.obb1.setRotation(VectorTool.combineRotations(1, this));
        }

        Vector4f worldPosition2 = transformPosition(transform, 0.000f, 3.125f, -1.625f);
        this.obbTent.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obbTent.setRotation(VectorTool.combineRotations(1, this));






        Vector4f worldPosition3 = transformPosition(transform, 1.164f, 0.719f, 2.313f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 1.164f, 0.719f, -1.438f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, 1.164f, 0.719f, -2.938f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));



        Vector4f worldPosition6 = transformPosition(transform, -1.164f, 0.719f, 2.313f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition7 = transformPosition(transform, -1.164f, 0.719f, -1.438f);
        this.obb7.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obb7.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition8 = transformPosition(transform, -1.164f, 0.719f, -2.938f);
        this.obb8.center().set(new Vector3f(worldPosition8.x, worldPosition8.y, worldPosition8.z));
        this.obb8.setRotation(VectorTool.combineRotations(1, this));
    }


}
