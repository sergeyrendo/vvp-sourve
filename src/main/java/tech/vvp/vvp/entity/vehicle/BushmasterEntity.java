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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.joml.Math;
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

public class BushmasterEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Integer> SMOKE_DECOY = SynchedEntityData.defineId(BushmasterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> TURRET_Y_ROT = SynchedEntityData.defineId(BushmasterEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TURRET_X_ROT = SynchedEntityData.defineId(BushmasterEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(BushmasterEntity.class, EntityDataSerializers.INT);
    public float turretYRotO;
    public float turretXRotO;

    public OBB obb;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;


    private static final ResourceLocation GEO_MODEL = VVP.loc("geo/bushmaster.geo.json");
    private static boolean LOCATORS_LOADED = false;
    private static Vector3f LOCATOR_DIRT_LEFT = new Vector3f(18.75f/16f, 0.7f/16f, -39/16f);  // fallback
    private static Vector3f LOCATOR_DIRT_RIGHT = new Vector3f(-18.75f/16f, 0.7f/16f, -39/16f); // fallback

    public BushmasterEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.BUSHMASTER.get(), world);
    }


    public BushmasterEntity(EntityType<BushmasterEntity> type, Level world) {
        super(type, world);
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(44f/32f, 33f/32f, 94f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(44f/32f, 15f/32f, 26f/32f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(7f/32f, 22f/32f, 23f/32f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(7f/32f, 22f/32f, 23f/32f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(7f/32f, 22f/32f, 23f/32f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(7f/32f, 22f/32f, 23f/32f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
    }


    private PlayState firePredicate(AnimationState<BushmasterEntity> event) {
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "movement", 0, this::firePredicate));
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
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        this.playSound(com.atsuishio.superbwarfare.init.ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.15), random.nextFloat() * 0.15f + 1.05f);
    }


    @Override
    public void baseTick() {
        turretYRotO = this.getTurretYRot();
        turretXRotO = this.getTurretXRot();
        super.baseTick();
        this.updateOBB();


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
        wheelEngine(true, 0.052, VehicleConfigVVP.WHEEL_ENERGY_COST.get(), 1.25, 1.5, 0.18f, -0.13f, 0.0065f, 0.0030f, 0.1f);
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
        // From Immersive_Aircraft
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1);

        int i = this.getOrderedPassengers().indexOf(passenger);

        var worldPosition = switch (i) {
            case 0 -> transformPosition(transform, -10f/16f, 21f/16f, 24f/16f);
            case 1 -> transformPosition(transform, 10f/16f, 21f/16f, 24f/16f);
            case 2 -> transformPosition(transform, 8f/16f, 21f/16f, -12f/16f);
            case 3 -> transformPosition(transform, 8f/16f, 21f/16f, -27f/16f);
            case 4 -> transformPosition(transform, 8f/16f, 21f/16f, -39f/16f);
            case 5 -> transformPosition(transform, -8f/16f, 21f/16f, -12f/16f);
            case 6 -> transformPosition(transform, -8f/16f, 21f/16f, -27f/16f);
            case 7 -> transformPosition(transform, -8f/16f, 21f/16f, -39f/16f);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };

        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public int getMaxPassengers() {
        return 8;
    }

    @Override
    public void onPassengerTurned(Entity entity) {
    }

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
    public float getWheelMaxHealth() {
        return 75;
    }

    @Override
    public float getEngineMaxHealth() {
        return 50;
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, 0, 39f/16f, -7f/16f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0, 30.5f/16f, 53f/16f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 18.5f/16f, 11f/16f, 38.5f/16f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 18.5f/16f, 11f/16f, -32.5f/16f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, -18.5f/16f, 11f/16f, 38.5f/16f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition6 = transformPosition(transform, -18.5f/16f, 11f/16f, -32.5f/16f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));

    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {  // Только на сервере
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 4;  // Количество типов (default=0, desert=1, forest=2)
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

        return super.interact(player, hand);
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

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return VVP.loc("textures/gui/vehicle/type/land.png");
    }

}