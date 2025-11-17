package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.VectorTool;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.init.ModSounds;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

public class ToyotaEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Integer> SMOKE_DECOY = SynchedEntityData.defineId(ToyotaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> TURRET_Y_ROT = SynchedEntityData.defineId(ToyotaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TURRET_X_ROT = SynchedEntityData.defineId(ToyotaEntity.class, EntityDataSerializers.FLOAT);
    public float turretYRotO;
    public float turretXRotO;

    public OBB obb;
    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;

    private static final ResourceLocation GEO_MODEL = VVP.loc("geo/toyota.geo.json");
    private static boolean LOCATORS_LOADED = false;
    private static Vector3f LOCATOR_DIRT_LEFT = new Vector3f(15.5f/16f, 0.25f/16f, -32f/16f);  // fallback
    private static Vector3f LOCATOR_DIRT_RIGHT = new Vector3f(-15.5f/16f, 0.25f/16f, -32f/16f); // fallback


    public ToyotaEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.TOYOTA.get(), world);
    }

    public ToyotaEntity(EntityType<? extends ToyotaEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);

        this.obb = new OBB(this.position().toVector3f(), new Vector3f(38.5f/32f, 27.25f/32f, 30f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(38.5f/32f, 4.25f/32f, 43f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(38.5f/32f, 12.25f/32f, 25f/32f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(5.25f/32f, 16.25f/32f, 17f/32f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(5.25f/32f, 16.25f/32f, 17f/32f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(5.25f/32f, 16.25f/32f, 17f/32f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(5.25f/32f, 16.25f/32f, 17f/32f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);

    }

    @SuppressWarnings("unchecked")
    public static ToyotaEntity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<ToyotaEntity> castedEntityType = (EntityType<ToyotaEntity>) entityTypeFromPacket;
        ToyotaEntity entity = new ToyotaEntity(castedEntityType, world);
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
        return new ResourceLocation(VVP.MOD_ID, "textures/vehicle_icon/typhoon_icon.png");

    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        if (index == 2 || index == 3) {
            // Камера ниже и ближе, “эффект лёжа”
            return new ThirdPersonCameraPosition(1.8, 0.4, -0.4);
        }
        return new ThirdPersonCameraPosition(2.75, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SMOKE_DECOY, 0);
        this.entityData.define(TURRET_Y_ROT, 0.0f);
        this.entityData.define(TURRET_X_ROT, 0.0f);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(com.atsuishio.superbwarfare.init.ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.15f + 1.05f);
    }


    @Override
    public void baseTick() {
        turretYRotO = this.getTurretYRot();
        turretXRotO = this.getTurretXRot();
        super.baseTick();
        updateOBB();
        if (this.onGround()) {
            float f0 = 0.54f + 0.25f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.05 * this.getDeltaMovement().horizontalDistance())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.85, f0));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.95, 0.99));
        }

        if (this.level().isClientSide) {
            spawnWheelGroundParticles(1.0f);
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
        wheelEngine(true, 0.052, VehicleConfigVVP.WHEEL_ENERGY_COST.get(), 1.25, 1.5, 0.18f, -0.13f, 0.0060f, 0.0028f, 0.1f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.BTR_80A_ENGINE.get();
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
                worldPosition = transformPosition(transform, 9f/16f, 5.8f/16f, 2.6f/16f);
                break;
            case 1: // Пассажир рядом с водителем
                worldPosition = transformPosition(transform, -9f/16f, 5.8f/16f, 2.6f/16f);
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
        return 2; // Водитель + 3 пассажира (типичная компоновка седана)
    }



    @Override
    public void onPassengerTurned(Entity entity) {
        // Ничего не делаем здесь, чтобы предотвратить вращение турели при повороте головы пассажира
    }

    private PlayState idlePredicate(AnimationState<ToyotaEntity> event) {
        if (Mth.abs((float)this.getDeltaMovement().horizontalDistanceSqr()) > 0.001 || Mth.abs(this.entityData.get(POWER)) > 0.05) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
        }
        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
    }

    // @Override
    // public int getMaxEnergy() {
    //     return VehicleConfig.LAV_150_MAX_ENERGY.get();
    // }

    // @Override
    // public float getMaxHealth() {
    //     return VehicleConfig.LAV_150_HP.get();
    // }

    @Override
    public boolean hasDecoy() {
        return false;
    }

    @Override
    public float turretYRotO() {
        return turretYRotO;
    }

    @Override
    public float turretYRot() {
        return this.entityData.get(TURRET_Y_ROT);
    }

    @Override
    public float turretXRotO() {
        return turretXRotO;
    }

    @Override
    public float turretXRot() {
        return this.entityData.get(TURRET_X_ROT);
    }

    @Override
    public Vec3 getBarrelVec(float ticks) {
        return Vec3.ZERO;
    }

    @Override
    public Vec3 getGunVec(float ticks) {
        return Vec3.ZERO;
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return false;
    }

    @Override
    public float getStepHeight() {
        return 1.5f;
    }

    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb1, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6);
    }

    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0.0f, 23.625f/16f, 10f/16f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition1 = transformPosition(transform, 0.0f, 18.125f/16f, -26.5f/16f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.0f, 22.125f/16f, 37.5f/16f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 15.625f/16f, 8.125f/16f, 37.5f/16f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 15.625f/16f, 8.125f/16f, -25.5f/16f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, -15.625f/16f, 8.125f/16f, 37.5f/16f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition6 = transformPosition(transform, -15.625f/16f, 8.125f/16f, -25.5f/16f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));



    }

    @Override
    public boolean hasPassengerTurretWeapon() {
        return false;
    }

    @Override
    public void vehicleShoot(LivingEntity livingEntity, int i) {

    }

    @Override
    public int mainGunRpm(LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public boolean canShoot(LivingEntity livingEntity) {
        return false;
    }

    @Override
    public int getAmmoCount(LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public int zoomFov() {
        return 0;
    }

    @Override
    public int getWeaponHeat(LivingEntity livingEntity) {
        return 0;
    }


    @OnlyIn(Dist.CLIENT)
    private static void tryLoadLocatorsFromGeo() {
        if (LOCATORS_LOADED) return;
        LOCATORS_LOADED = true;

        ResourceManager rm = Minecraft.getInstance().getResourceManager();

        try {
            Optional<Resource> resOpt = rm.getResource(GEO_MODEL);
            if (resOpt.isEmpty()) {
                Mod.LOGGER.warn("BTR-4: model not found: {}", GEO_MODEL);
                return;
            }

            try (InputStreamReader reader = new InputStreamReader(resOpt.get().open())) {
                JsonObject root = new Gson().fromJson(reader, JsonObject.class);
                if (root == null || !root.has("bones")) return;

                for (JsonElement el : root.getAsJsonArray("bones")) {
                    JsonObject bone = el.getAsJsonObject();
                    if (!bone.has("locators")) continue;

                    JsonObject locs = bone.getAsJsonObject("locators");

                    if (locs.has("dirt_left")) {
                        LOCATOR_DIRT_LEFT = jsonArrToVector3fDiv16(locs.getAsJsonArray("dirt_left"));
                    }
                    if (locs.has("dirt_right")) {
                        LOCATOR_DIRT_RIGHT = jsonArrToVector3fDiv16(locs.getAsJsonArray("dirt_right"));
                    }
                }
            }
        } catch (Exception e) {
            Mod.LOGGER.warn("BTR-4: couldn't load locators from {}", GEO_MODEL, e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnGroundParticlesFromLocator(Vector3f local, float partialTicks) {
        // Мелкая оптимизация: частицы только при движении
        if (this.getDeltaMovement().horizontalDistanceSqr() < 0.0015) return;

        // Переводим локальную точку в мировые координаты через трансформ корпуса
        Matrix4f transform = getVehicleTransform(partialTicks);
        Vector4f p = transformPosition(transform, local.x, local.y, local.z);

        double x = p.x;
        double y = p.y;
        double z = p.z;

        // Берем блок прямо под точкой
        BlockPos pos = BlockPos.containing(x, y - 0.25, z);
        BlockState state = this.level().getBlockState(pos);
        if (state.isAir()) {
            state = this.level().getBlockState(pos.below());
        }
        if (state.isAir()) return; // В воздухе — ничего

        // Сколько частиц — от скорости
        Vec3 vel = this.getDeltaMovement();
        int count = Mth.clamp((int) (vel.horizontalDistance() * 24.0), 1, 6);

        for (int i = 0; i < count; i++) {
            double vx = vel.x * 0.2 + (this.random.nextDouble() - 0.5) * 0.08;
            double vy = 0.05 + this.random.nextDouble() * 0.05;
            double vz = vel.z * 0.2 + (this.random.nextDouble() - 0.5) * 0.08;

            this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                    x, y + 0.01, z, vx, vy, vz);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnWheelGroundParticles(float partialTicks) {
        tryLoadLocatorsFromGeo();

        // Можно добавить проверку, что машина “на земле”, чтобы не сыпалась в прыжке
        if (!this.onGround()) return;

        spawnGroundParticlesFromLocator(LOCATOR_DIRT_LEFT, partialTicks);
        spawnGroundParticlesFromLocator(LOCATOR_DIRT_RIGHT, partialTicks);
    }

    @OnlyIn(Dist.CLIENT)
    private static Vector3f jsonArrToVector3fDiv16(com.google.gson.JsonArray arr) {
        return new Vector3f(
                arr.get(0).getAsFloat() / 16f,
                arr.get(1).getAsFloat() / 16f,
                arr.get(2).getAsFloat() / 16f
        );
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return VVP.loc("textures/gui/vehicle/type/civilian.png");
    }

}