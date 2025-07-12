package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.AirEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.HeliRocketWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
// import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PlayMessages;
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
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.VehicleConfigVVP;
import tech.vvp.vvp.init.ModEntities;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.S2CRadarSyncPacket;

import java.util.ArrayList;
import java.util.List;

import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraPitch;
import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraYaw;
import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class Mi24polEntity extends ContainerMobileVehicleEntity implements GeoEntity, HelicopterEntity, WeaponVehicleEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Float> PROPELLER_ROT = SynchedEntityData.defineId(Mi24polEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> LOADED_ROCKET = SynchedEntityData.defineId(Mi24polEntity.class, EntityDataSerializers.INT);

    public static final int RADAR_RANGE = 150;

    public boolean engineStart;
    public boolean engineStartOver;
    public double velocity;
    public int fireIndex;
    public int holdTick;
    public int holdPowerTick;
    public float destroyRot;
    public float delta_x;
    public float delta_y;
    public OBB obb;
    public OBB obb1;
    public OBB obb2;
    public OBB obb3;

    public Mi24polEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.MI24POL.get(), world);
    }

    public Mi24polEntity(EntityType<Mi24polEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        // КАБИНА
        this.obb = new OBB(this.position().toVector3f(), new Vector3f(0.85f, 1.177f, 1.398f), new Quaternionf(), OBB.Part.BODY);
        // КОРПУС
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(0.85f, 1.45f, 4.289f), new Quaternionf(), OBB.Part.BODY); 
        // ВИНТ
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(0.85f, 0.239f, 2.742f), new Quaternionf(), OBB.Part.BODY); 
        // ХВОСТ
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.85f, 1.0515625f, 2.7421875f), new Quaternionf(), OBB.Part.ENGINE1); 
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
    public static Mi24polEntity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null; 
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
             Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
             return null;
        }

        EntityType<Mi24polEntity> castedEntityType = (EntityType<Mi24polEntity>) entityTypeFromPacket;
        Mi24polEntity entity = new Mi24polEntity(castedEntityType, world);
        return entity;
    }

    private void handleRadar() {
        // Эта часть остается без изменений
        if (this.level().isClientSide() || !(this.getFirstPassenger() instanceof ServerPlayer player)) {
            return;
        }

        List<Vec3> targetPositions = new ArrayList<>();

        // Здесь мы изменяем условие поиска сущностей
        List<Entity> potentialTargets = this.level().getEntities(this, this.getBoundingBox().inflate(RADAR_RANGE),
            entity -> (entity instanceof HelicopterEntity || entity instanceof AirEntity) && entity != this);

        // Эта часть тоже остается без изменений
        if (!potentialTargets.isEmpty()) {
            for (Entity target : potentialTargets) {
                targetPositions.add(target.position());
            }
            VVPNetwork.VVP_HANDLER.sendTo(new S2CRadarSyncPacket(targetPositions), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        updateOBB();

        if (this.tickCount % 20 == 0) {
            handleRadar();
        }

        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            handleAmmo();
        }

        // if (level().isClientSide()) {
        //     handleEngineSound();
        // }


        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1, 0.8));
        } else {
            setZRot(getRoll() * (backInputDown ? 0.9f : 0.99f));
            float f = (float) Mth.clamp(0.95f - 0.015 * getDeltaMovement().length() + 0.02f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).scale((this.getXRot() < 0 ? -0.035 : (this.getXRot() > 0 ? 0.035 : 0)) * this.getDeltaMovement().length())));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f, 0.95, f));
        }

        if (this.isInWater() && this.tickCount % 4 == 0 && getSubmergedHeight(this) > 0.5 * getBbHeight()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, this.getFirstPassenger() == null ? this : this.getFirstPassenger()), 6 + (float) (20 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
        }

        releaseDecoy();
        lowHealthWarning();
        this.terrainCompact(2.7f, 2.7f);

        this.refreshDimensions();
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .blockInteraction(VehicleConfigVVP.MI_24_CANNON_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP)
                                .damage(VehicleConfigVVP.MI_24_CANNON_DAMAGE.get())
                                .explosionDamage(VehicleConfigVVP.MI_24_CANNON_EXPLOSION_DAMAGE.get().floatValue())
                                .explosionRadius(VehicleConfigVVP.MI_24_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png"))
                                .sound1p(ModSounds.HELICOPTER_CANNON_FIRE_1P.get())
                                .sound3p(ModSounds.HELICOPTER_CANNON_FIRE_3P.get())
                                .sound3pFar(ModSounds.HELICOPTER_CANNON_FAR.get())
                                .sound3pVeryFar(ModSounds.HELICOPTER_CANNON_VERYFAR.get()),
                        new HeliRocketWeapon()
                                .damage(VehicleConfigVVP.MI_24_ROCKET_DAMAGE.get())
                                .explosionDamage(VehicleConfigVVP.MI_24_ROCKET_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.MI_24_ROCKET_EXPLOSION_RADIUS.get())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(ModSounds.HELICOPTER_ROCKET_FIRE_1P.get())
                                .sound3p(ModSounds.HELICOPTER_ROCKET_FIRE_3P.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(16.0, 1.5, 0.0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(PROPELLER_ROT, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedRocket", this.entityData.get(LOADED_ROCKET));
        compound.putFloat("PropellerRot", this.entityData.get(PROPELLER_ROT));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(PROPELLER_ROT, compound.getFloat("PropellerRot"));
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> {
                    var entity = source.getDirectEntity();
                    if (entity != null && entity.getType().is(tech.vvp.vvp.init.ModTags.EntityTypes.AERIAL_BOMB)) {
                        damage *= 2;
                    }
                    damage *= getHealth() > 0.1f ? 0.7f : 0.05f;
                    return damage;
                });
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == ModItems.ROCKET_70.get() && this.entityData.get(LOADED_ROCKET) < 25) {
            this.entityData.set(LOADED_ROCKET, this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.interact(player, hand);
    }

    private void handleAmmo() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        int ammoCount = this.getItemStacks().stream().filter(stack -> {
            if (stack.is(ModItems.AMMO_BOX.get())) {
                return Ammo.HEAVY.get(stack) > 0;
            }
            return false;
        }).mapToInt(Ammo.HEAVY::get).sum() + countItem(ModItems.SMALL_SHELL.get());

        if ((hasItem(ModItems.ROCKET_70.get()) || InventoryTool.hasCreativeAmmoBox(player)) && reloadCoolDown == 0 && this.getEntityData().get(LOADED_ROCKET) < 25) {
            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) + 1);
            reloadCoolDown = 25;
            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.ROCKET_70.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_ROCKET));
        }
    }

    @Override
    public void travel() {
        Entity passenger = getFirstPassenger();
        Entity passenger2 = getNthEntity(1);

        if (getHealth() > 0.1f * getMaxHealth()) {
            if (passenger == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.upInputDown = false;
                this.downInputDown = false;
                this.setZRot(this.roll * 0.98f);
                this.setXRot(this.getXRot() * 0.98f);
                if (passenger2 == null) {
                    this.entityData.set(POWER, this.entityData.get(POWER) * 0.99f);
                }
            } else if (passenger instanceof Player) {

                if (rightInputDown) {
                    holdTick++;
                    this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 2f * Math.min(holdTick, 7) * this.entityData.get(POWER));
                } else if (this.leftInputDown) {
                    holdTick++;
                    this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 2f * Math.min(holdTick, 7) * this.entityData.get(POWER));
                } else {
                    holdTick = 0;
                }

                delta_x = ((this.onGround()) ? 0 : 1.5f) * entityData.get(MOUSE_SPEED_Y) * this.entityData.get(PROPELLER_ROT);
                delta_y = Mth.clamp((this.onGround() ? 0.1f : 2f) * entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT), -10f, 10f);

                this.setYRot(this.getYRot() + delta_y);
                this.setXRot(this.getXRot() + delta_x);
                this.setZRot(this.getRoll() - this.entityData.get(DELTA_ROT) + (this.onGround() ? 0 : 0.25f) * entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT));
            }

            if (this.level() instanceof ServerLevel) {
                if (this.getEnergy() > 0) {
                    boolean up = upInputDown || forwardInputDown;
                    boolean down = this.downInputDown;

                    if (!engineStart && up) {
                        engineStart = true;
                        this.level().playSound(null, this, ModSounds.HELICOPTER_ENGINE_START.get(), this.getSoundSource(), 3, 1);
                    }

                    if (up && engineStartOver) {
                        holdPowerTick++;
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0002f * Math.min(holdPowerTick, 10), 0.12f));
                    }

                    if (engineStartOver) {
                        if (down) {
                            holdPowerTick++;
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.001f * Math.min(holdPowerTick, 5), this.onGround() ? 0 : 0.025f));
                        } else if (backInputDown) {
                            holdPowerTick++;
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.001f * Math.min(holdPowerTick, 5), this.onGround() ? 0 : 0.052f));
                            if (passenger != null) {
                                passenger.setXRot(0.8f * passenger.getXRot());
                            }
                        }
                    }

                    if (engineStart && !engineStartOver) {
                        this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0012f, 0.045f));
                    }

                    if (!(up || down || backInputDown) && engineStartOver) {
                        if (this.getDeltaMovement().y() < 0) {
                            this.entityData.set(POWER, Math.min(this.entityData.get(POWER) + 0.0002f, 0.12f));
                        } else {
                            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - (this.onGround() ? 0.00005f : 0.0002f), 0));
                        }
                        holdPowerTick = 0;
                    }
                } else {
                    this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.0001f, 0));
                    this.forwardInputDown = false;
                    this.backInputDown = false;
                    engineStart = false;
                    engineStartOver = false;
                }
            }
        } else if (!onGround() && engineStartOver) {
            this.entityData.set(POWER, Math.max(this.entityData.get(POWER) - 0.0003f, 0.01f));
            destroyRot += 0.08f;

            float diffX = 45 - this.getXRot();
            float diffZ = -20 - this.getRoll();

            this.setXRot(this.getXRot() + diffX * 0.05f * this.entityData.get(PROPELLER_ROT));
            this.setYRot(this.getYRot() + destroyRot);
            this.setZRot(this.getRoll() + diffZ * 0.1f * this.entityData.get(PROPELLER_ROT));
            setDeltaMovement(getDeltaMovement().add(0, -destroyRot * 0.004, 0));
        }

        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.9f);
        this.entityData.set(PROPELLER_ROT, Mth.lerp(0.18f, this.entityData.get(PROPELLER_ROT), this.entityData.get(POWER)));
        this.setPropellerRot(this.getPropellerRot() + 30 * this.entityData.get(PROPELLER_ROT));
        this.entityData.set(PROPELLER_ROT, this.entityData.get(PROPELLER_ROT) * 0.9995f);

        if (engineStart) {
            this.consumeEnergy((int) (VehicleConfigVVP.MI_24_MIN_ENERGY_COST.get() + this.entityData.get(POWER) * ((VehicleConfigVVP.MI_24_MAX_ENERGY_COST.get() - VehicleConfigVVP.MI_24_MIN_ENERGY_COST.get()) / 0.12)));
        }

        Matrix4f transform = getVehicleTransform(1);

        Vector4f force0 = transformPosition(transform, 0, 0, 0);
        Vector4f force1 = transformPosition(transform, 0, 1, 0);

        Vec3 force = new Vec3(force0.x, force0.y, force0.z).vectorTo(new Vec3(force1.x, force1.y, force1.z));

        setDeltaMovement(getDeltaMovement().add(force.scale(this.entityData.get(POWER))));

        if (this.entityData.get(POWER) > 0.04f) {
            engineStartOver = true;
        }

        if (this.entityData.get(POWER) < 0.0004f) {
            engineStart = false;
            engineStartOver = false;
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return tech.vvp.vvp.init.ModSounds.MI24_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return entityData.get(PROPELLER_ROT) * 2f;
    }

    protected void clampRotation(Entity entity) {
        if (entity == getNthEntity(0)) {
            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getXRot());
            float f1 = Mth.clamp(f, -80.0F, 80F);
            entity.xRotO += f1 - f;
            entity.setXRot(entity.getXRot() + f1 - f);

            float f2 = Mth.wrapDegrees(entity.getYRot() - this.getYRot());
            float f3 = Mth.clamp(f2, -80.0F, 80.0F);
            entity.yRotO += f3 - f2;
            entity.setYRot(entity.getYRot() + f3 - f2);
            entity.setYBodyRot(this.getYRot());
        }
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1.0f);
        int i = this.getOrderedPassengers().indexOf(passenger);
        float riderOffset = (float) passenger.getMyRidingOffset();
        Vector4f worldPosition;

        if (i == 0) { // Пилот (переднее сиденье)
            float pilotX = 0.0f;
            float pilotY = 0.43f + riderOffset;
            float pilotZ = 0.35f;
            worldPosition = transformPosition(transform, pilotX, pilotY, pilotZ);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        } else if (i == 1) { // Стрелок/Второй пассажир (заднее сиденье)
            float gunnerX = 0.0f;
            float gunnerY = -0.25f + riderOffset;
            float gunnerZ = 1.5f;
            worldPosition = transformPosition(transform, gunnerX, gunnerY, gunnerZ);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
        } else if (i == 2) {
            worldPosition = transformPosition(transform, -0.61f, -0.3f, -1.4f);
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);       
        } else if (i == 3) {
            worldPosition = transformPosition(transform, 0.55f, -0.3f, -1.4f);      
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);        
        } else if (i == 4) {
            worldPosition = transformPosition(transform, -0.61f, -0.3f, -4.0f);   
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);           
        } else if (i == 5) {
            worldPosition = transformPosition(transform, 0.55f, -0.3f, -4.0f);  
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);            
        }

        if (passenger != this.getFirstPassenger()) {
            passenger.setXRot(passenger.getXRot() + (getXRot() - xRotO));
        }

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYHeadRot(entity.getYHeadRot() + delta_y);
            entity.setYRot(entity.getYRot() + delta_y);
            entity.setYBodyRot(this.getYRot());
        } else if (entity == getNthEntity(1)) {
            float f = Mth.wrapDegrees(entity.getYRot() - getYRot());
            float g = Mth.clamp(f, -105.0f, 105.0f);
            entity.yRotO += g - f;
            entity.setYRot(entity.getYRot() + g - f + 0.9f * destroyRot);
            entity.setYHeadRot(entity.getYRot());
            entity.setYBodyRot(getYRot());
        }
    }

    @Override
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 1.45f, getY() + 1.45f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, yRotO, getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    // @Override
    // public EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
    //     return EntityDimensions.scalable(2.063f, 3.625f);
    // }

    @Override
    public void destroy() {
        if (this.crash) {
            crashPassengers();
        } else {
            explodePassengers();
        }

        if (level() instanceof ServerLevel) {
            CustomExplosion explosion = new CustomExplosion(this.level(), this,
                    ModDamageTypes.causeCustomExplosionDamage(this.level().registryAccess(), this, getAttacker()), 300.0f,
                    this.getX(), this.getY(), this.getZ(), 8f, ExplosionConfig.EXPLOSION_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP, true).setDamageMultiplier(1);
            explosion.explode();
            ForgeEventFactory.onExplosionStart(this.level(), explosion);
            explosion.finalizeExplosion(false);
            ParticleTool.spawnHugeExplosionParticles(this.level(), this.position());
        }
        super.destroy();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition = transformPosition(transform, 0f, -0.8f, 3.0f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition = transformPosition(transform, 0, 0, 0);
        Vector4f worldPosition2 = transformPosition(transform, 0, 0.01f, 1);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        Matrix4f transform = getVehicleTransform(1);
        float x;
        float y;
        float z;

        if (getWeaponIndex(0) == 0) {
            if (this.cannotFire) return;

            x = 0.0f;
            y = -0.8f;
            z = 3.0f;

            Vector4f worldPosition;
            Vector4f worldPosition2;

            if (fireIndex == 0) {
                worldPosition = transformPosition(transform, -x, y, z);
                worldPosition2 = transformPosition(transform, -x + 0.009f - 0.002f, y + 0.012f, z + 1.0f);
                fireIndex = 1;
            } else {
                worldPosition = transformPosition(transform, x, y, z);
                worldPosition2 = transformPosition(transform, x + 0.009f + 0.002f, y + 0.012f, z + 1.0f);
                fireIndex = 0;
            }

            Vec3 shootVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();

            if (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                var entityToSpawn = ((SmallCannonShellWeapon) getWeapon(0)).create(player);

                entityToSpawn.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                entityToSpawn.shoot(shootVec.x, shootVec.y, shootVec.z, 20, 0.15f);
                level().addFreshEntity(entityToSpawn);

                sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, worldPosition.x, worldPosition.y, worldPosition.z, 1, 0, 0, 0, 0, false);

                if (!hasCreativeAmmo) {
                    ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                        if (stack.is(ModItems.AMMO_BOX.get())) {
                            return Ammo.HEAVY.get(stack) > 0;
                        }
                        return false;
                    }).findFirst().orElse(ItemStack.EMPTY);

                    if (!ammoBox.isEmpty()) {
                        Ammo.HEAVY.add(ammoBox, -1);
                    } else {
                        this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                    }
                }
            }

            this.entityData.set(HEAT, this.entityData.get(HEAT) + 4);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 0, 4, 12, 24);
            }

        } else if (getWeaponIndex(0) == 1 && this.getEntityData().get(LOADED_ROCKET) > 0) {
            x = 1.7f;
            y = 0.62f - 1.45f;
            z = 0.8f;

            var heliRocketEntity = ((HeliRocketWeapon) getWeapon(0)).create(player);

            Vector4f worldPosition;
            Vector4f worldPosition2;

            if (fireIndex == 0) {
                worldPosition = transformPosition(transform, -x, y, z);
                worldPosition2 = transformPosition(transform, -x + 0.009f - 0.0025f, y + 0.012f, z + 1.0f);
                fireIndex = 1;
            } else {
                worldPosition = transformPosition(transform, x, y, z);
                worldPosition2 = transformPosition(transform, x + 0.009f + 0.0025f, y + 0.012f, z + 1.0f);
                fireIndex = 0;
            }

            Vec3 shootVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();

            heliRocketEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 7, 0.25f);
            player.level().addFreshEntity(heliRocketEntity);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 0, 6, 6, 6);
            }

            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) - 1);
            reloadCoolDown = 30;
        }
    }

    @Override
    public int mainGunRpm(Player player) {
        return 600;
    }

    @Override
    public boolean canShoot(Player player) {
        if (getWeaponIndex(0) == 0) {
            return (this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFire;
        } else if (getWeaponIndex(0) == 1) {
            return this.entityData.get(AMMO) > 0;
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return false;
    }

    @Override
    public int zoomFov() {
        return 5;
    }

    @Override
    public int getWeaponHeat(Player player) {
        return entityData.get(HEAT);
    }

    @Override
    public float getRotX(float tickDelta) {
        return this.getPitch(tickDelta);
    }

    @Override
    public float getRotY(float tickDelta) {
        return this.getYaw(tickDelta);
    }

    @Override
    public float getRotZ(float tickDelta) {
        return this.getRoll(tickDelta);
    }

    @Override
    public float getPower() {
        return this.entityData.get(POWER);
    }

    @Override
    public int getDecoy() {
        return this.entityData.get(DECOY_COUNT);
    }

    public int getMaxPassengers() {
        return 6;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/mi24pl_icon.png");
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return seatIndex == 0 ? 0 : original;
    }

    @Override
    public double getMouseSensitivity() {
        return 0.15;
    }

    @Override
    public double getMouseSpeedX() {
        return 0.35;
    }

    @Override
    public double getMouseSpeedY() {
        return 0.2;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        return Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 1.45f, getY() + 1.45f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees((float) (-Mth.lerp(ticks, yRotO, getYRot()) + freeCameraYaw)));
        transform.rotate(Axis.XP.rotationDegrees((float) (Mth.lerp(ticks, xRotO, getXRot()) + freeCameraPitch)));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 0) {
            return new Vec2((float) (getRotY(partialTicks) - freeCameraYaw), (float) (getRotX(partialTicks) + freeCameraPitch));
        }

        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 0) {
            Matrix4f transform = getClientVehicleTransform(partialTicks);
            Vector4f maxCameraPosition = transformPosition(transform, -2.1f, 1, -10 - (float) ClientMouseHandler.custom3pDistanceLerp);
            Vec3 finalPos = CameraTool.getMaxZoom(transform, maxCameraPosition);

            if (isFirstPerson) {
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
            } else {
                return finalPos;
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/aircraft.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb, this.obb1, this.obb2, this.obb3); 
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition = transformPosition(transform, -0.020f, 1.190f, 0.746f);
        this.obb.center().set(new Vector3f(worldPosition.x, worldPosition.y, worldPosition.z));
        this.obb.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition1 = transformPosition(transform, 0.0f, 1.327f, -4.1953125f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.0f, 3.249f, -3.96f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 0.0f, 1.26796875f, -11.2109375f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));
    }
}