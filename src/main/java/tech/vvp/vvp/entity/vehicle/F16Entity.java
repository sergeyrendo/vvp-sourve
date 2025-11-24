package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.*;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.*;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PlayMessages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.weapon.Aim120Weapon;
import tech.vvp.vvp.radar.IRadarVehicle;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

import static com.atsuishio.superbwarfare.event.ClientEventHandler.zoomVehicle;
import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraPitch;
import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraYaw;
import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class F16Entity extends ContainerMobileVehicleEntity implements GeoEntity, WeaponVehicleEntity, AircraftEntity, OBBEntity, IRadarVehicle {

    public static Consumer<MobileVehicleEntity> fireSound = vehicle -> {
    };

    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(F16Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(F16Entity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FIRE_TIME = SynchedEntityData.defineId(F16Entity.class, EntityDataSerializers.INT);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public float destroyRot;
    private boolean wasFiring = false;
    public float delta_x;
    public float delta_y;
    public int fireIndex;
    public int reloadCoolDownMissile;
    public String lockingTargetO = "none";
    public String lockingTarget = "none";
    public int lockTime;
    public boolean locked;

    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obb5;
    public OBB obb6;

    public F16Entity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.F_16.get(), world);
    }

    public F16Entity(EntityType<F16Entity> type, Level world) {
        super(type, world);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(22f/32f, 37f/32f, 198f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(98f/32f, 11f/32f, 49f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(98f/32f, 11f/32f, 49f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(13f/32f, 37f/32f, 114f/32f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obb5 = new OBB(this.position().toVector3f(), new Vector3f(13f/32f, 37f/32f, 114f/32f), new Quaternionf(), OBB.Part.ENGINE2);
        this.obb6 = new OBB(this.position().toVector3f(), new Vector3f(22f/32f, 65f/32f, 50f/32f), new Quaternionf(), OBB.Part.ENGINE2);
    }

    @Override
    public int getRadarRange() {
        return 300;
    }

    @Override
    public boolean consumeRadarEnergy() {
        int cost = java.lang.Math.max(1, getRadarEnergyCostPerScan());
        if (this.getEnergy() >= cost) {
            this.consumeEnergy(cost);
            return true;
        }
        return false;
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .damage(8)
                                .explosionDamage(4)
                                .explosionRadius(0.5f)
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png")),
                        new Aim120Weapon()
                                .sound(ModSounds.INTO_MISSILE.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(17, 3, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(FIRE_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
        compound.putInt("FireTime", this.entityData.get(FIRE_TIME));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
        this.entityData.set(FIRE_TIME, compound.getInt("FireTime"));
    }

    @Override
    public boolean shouldSendHitParticles() {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.2), random.nextFloat() * 0.1f + 1f);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.25f) * damage * (getHealth() > 0.1f ? 0.4f : 0.05f));
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getMainHandItem();

        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 6;
                int newType = (currentType + 1) % maxTypes;
                this.entityData.set(CAMOUFLAGE_TYPE, newType);

                this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.SPRAY.get(), this.getSoundSource(), 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1, this.getZ(), 10, 1.0, 1.0, 1.0, 0.1);
                }

                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @Override
    public void baseTick() {
        if (!this.wasFiring && this.isFiring() && this.level().isClientSide()) {
            fireSound.accept(this);
        }
        this.wasFiring = this.isFiring();

        this.lockingTargetO = getTargetUuid();

        super.baseTick();
        
        if (!this.level().isClientSide) {
            // Перезарядка ракет
            if (reloadCoolDownMissile > 0) {
                reloadCoolDownMissile--;
            }
            // Задержка между выстрелами
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            handleAmmo();
        }
        
        // Обработка стрельбы
        if (this.getFirstPassenger() instanceof Player player && fireInputDown) {
            if (this.getWeaponIndex(0) == 0) {
                if ((this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFire) {
                    vehicleShoot(player, 0);
                }
            } else if (this.getWeaponIndex(0) == 1) {
                // Ракеты X-25 можно стрелять только когда цель залочена
                if (this.entityData.get(AMMO) > 0 && locked && reloadCoolDown == 0) {
                    vehicleShoot(player, 0);
                }
            }
        }
        
        // Зменшення FIRE_TIME
        if (entityData.get(FIRE_TIME) > 0) {
            entityData.set(FIRE_TIME, entityData.get(FIRE_TIME) - 1);
        }
        
        // Пошук цілей для ракет
        if (this.getWeaponIndex(0) == 1) {
            seekTarget();
        }
        
        this.updateOBB();
        float f = (float) Mth.clamp(java.lang.Math.max((onGround() ? 0.819f : 0.82f) - 0.005 * getDeltaMovement().length(), 0.5) + 0.001f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90, 0.01, 0.99);

        boolean forward = getDeltaMovement().dot(getViewVector(1)) > 0;
        this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).scale((forward ? 0.227 : 0.1) * getDeltaMovement().dot(getViewVector(1)))));
        this.setDeltaMovement(this.getDeltaMovement().multiply(f, f, f));

        if (this.isInWater() && this.tickCount % 4 == 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 0.6, 0.6));
            if (lastTickSpeed > 0.4) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, this.getFirstPassenger() == null ? this : this.getFirstPassenger()), (float) (20 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
            }
        }

        if (onGround()) {
            terrainCompactF16();
        }

        lowHealthWarning();

        releaseDecoy();

        this.refreshDimensions();
    }

    @Override
    public void onEngine1Damaged(Vec3 pos, ServerLevel serverLevel) {
        sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0, true);
        sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0, true);
        sendParticle(serverLevel, ParticleTypes.FLAME, pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0, true);
        sendParticle(serverLevel, ModParticleTypes.FIRE_STAR.get(), pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0.25, true);
    }

    @Override
    public void onEngine2Damaged(Vec3 pos, ServerLevel serverLevel) {
        sendParticle(serverLevel, ParticleTypes.LARGE_SMOKE, pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0, true);
        sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0, true);
        sendParticle(serverLevel, ParticleTypes.FLAME, pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0, true);
        sendParticle(serverLevel, ModParticleTypes.FIRE_STAR.get(), pos.x, pos.y, pos.z, 5, 0.25, 0.25, 0.25, 0.25, true);
    }

    public void terrainCompactF16() {
        if (onGround()) {
            Matrix4f transform = this.getWheelsTransform(1);

            Vector4f positionF = transformPosition(transform, 0.141675f, 0, 4.6315125f);
            Vector4f positionLB = transformPosition(transform, 2.5752f, 0, -0.7516125f);
            Vector4f positionRB = transformPosition(transform, -2.5752f, 0, -0.7516125f);

            Vec3 p1 = new Vec3(positionF.x, positionF.y, positionF.z);
            Vec3 p2 = new Vec3(positionLB.x, positionLB.y, positionLB.z);
            Vec3 p3 = new Vec3(positionRB.x, positionRB.y, positionRB.z);

            float p1y = (float) this.traceBlockY(p1, 3);
            float p2y = (float) this.traceBlockY(p2, 3);
            float p3y = (float) this.traceBlockY(p3, 3);

            p1 = new Vec3(positionF.x, p1y, positionF.z);
            p2 = new Vec3(positionLB.x, p2y, positionLB.z);
            p3 = new Vec3(positionRB.x, p3y, positionRB.z);
            Vec3 p4 = p2.add(p3).scale(0.5);

            Vec3 v1 = p2.vectorTo(p3);
            Vec3 v2 = p4.vectorTo(p1);

            double x = getXRotFromVector(v2);
            double z = getXRotFromVector(v1);

            float diffX = org.joml.Math.clamp(-5f, 5f, Mth.wrapDegrees((float) (-2 * x) - getXRot()));
            setXRot(Mth.clamp(getXRot() + 0.05f * diffX, -45f, 45f));

            float diffZ = org.joml.Math.clamp(-5f, 5f, Mth.wrapDegrees((float) (-2 * z) - getRoll()));
            setZRot(Mth.clamp(getRoll() + 0.05f * diffZ, -45f, 45f));
        } else if (isInWater()) {
            setXRot(getXRot() * 0.9f);
            setZRot(getRoll() * 0.9f);
        }
    }

    private void handleAmmo() {
        boolean hasCreativeAmmoBox = this.getFirstPassenger() instanceof Player player && InventoryTool.hasCreativeAmmoBox(player);

        int ammoCount = countItem(ModItems.SMALL_SHELL.get());

        if ((hasItem(tech.vvp.vvp.init.ModItems.AIM_120_ITEM.get()) || hasCreativeAmmoBox) && reloadCoolDownMissile == 0 && this.getEntityData().get(LOADED_MISSILE) < 4) {
            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) + 1);
            reloadCoolDownMissile = 300;
            if (!hasCreativeAmmoBox) {
                this.getItemStacks().stream().filter(stack -> stack.is(tech.vvp.vvp.init.ModItems.AIM_120_ITEM.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_MISSILE));
        }
    }

    private boolean isAirborneTarget(Entity target) {
        // Проверка 1: Это летающая техника?
        if (target instanceof AircraftEntity || target instanceof HelicopterEntity) {
            return true;
        }
        
        // Проверка 2: Не на земле И достаточно высоко?
        if (!target.onGround()) {
            BlockPos groundPos = target.level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, target.blockPosition());
            double heightAboveGround = target.getY() - groundPos.getY();
            return heightAboveGround > 3.0; // Минимум 3 блока в воздухе
        }
        
        return false;
    }

    public void seekTarget() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        if (getTargetUuid().equals(lockingTargetO) && !getTargetUuid().equals("none")) {
            lockTime++;
        } else {
            resetSeek(player);
        }

        Entity entity = SeekTool.seekCustomSizeEntity(this, this.level(), 384, 18, 0.9, false);
        if (entity != null && isAirborneTarget(entity)) {
            if (lockTime == 0) {
                setTargetUuid(String.valueOf(entity.getUUID()));
            }
            if (!String.valueOf(entity.getUUID()).equals(getTargetUuid())) {
                resetSeek(player);
                setTargetUuid(String.valueOf(entity.getUUID()));
            }
        } else {
            setTargetUuid("none");
        }

        if (lockTime == 1) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCK.get(), 2, 1);
            }
        }

        if (lockTime > 10) {
            if (player instanceof ServerPlayer serverPlayer) {
                SoundTool.playLocalSound(serverPlayer, ModSounds.JET_LOCKON.get(), 2, 1);
            }
            locked = true;
        }
    }

    public void resetSeek(Player player) {
        lockTime = 0;
        locked = false;
        if (player instanceof ServerPlayer serverPlayer) {
            var clientboundstopsoundpacket = new ClientboundStopSoundPacket(new ResourceLocation(Mod.MODID, "jet_lock"), SoundSource.PLAYERS);
            serverPlayer.connection.send(clientboundstopsoundpacket);
        }
    }

    public void setTargetUuid(String uuid) {
        this.lockingTarget = uuid;
    }

    public String getTargetUuid() {
        return this.lockingTarget;
    }

    @Override
    public void travel() {
        Entity passenger = this.getFirstPassenger();

        if (getHealth() > 0.1f * getMaxHealth()) {
            if (passenger == null || isInWater()) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.entityData.set(POWER, this.entityData.get(POWER) * 0.95f);
                if (onGround()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.94, 1, 0.94));
                } else {
                    this.setXRot(Mth.clamp(this.getXRot() + 0.1f, -89, 89));
                }
            } else if (passenger instanceof Player) {
                if (getEnergy() > 0) {
                    if (forwardInputDown) {
                        this.entityData.set(POWER, java.lang.Math.min(this.entityData.get(POWER) + 0.004f, sprintInputDown ? 1f : 0.0575f));
                    }

                    if (backInputDown) {
                        this.entityData.set(POWER, java.lang.Math.max(this.entityData.get(POWER) - 0.002f, onGround() ? -0.05f : 0.01f));
                    }
                }

                if (!onGround()) {
                    if (rightInputDown) {
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 1.2f);
                    } else if (this.leftInputDown) {
                        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 1.2f);
                    }
                }

                if (downInputDown) {
                    if (onGround()) {
                        this.entityData.set(POWER, this.entityData.get(POWER) * 0.8f);
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.97, 1, 0.97));
                    } else {
                        this.entityData.set(POWER, this.entityData.get(POWER) * 0.97f);
                        this.setDeltaMovement(this.getDeltaMovement().multiply(0.994, 1, 0.994));
                    }
                    this.entityData.set(PLANE_BREAK, java.lang.Math.min(this.entityData.get(PLANE_BREAK) + 10, 60f));
                }
            }

            if (getEnergy() > 0 && !this.level().isClientSide) {
                this.consumeEnergy((int) (Mth.abs(this.entityData.get(POWER)) * 5 * VehicleConfig.A_10_MAX_ENERGY_COST.get()));
            }

            float rotSpeed = 1.5f + 2 * Mth.abs(VectorTool.calculateY(getRoll()));

            float addY = Mth.clamp(java.lang.Math.max((this.onGround() ? 0.1f : 0.2f) * (float) getDeltaMovement().length(), 0f) * entityData.get(MOUSE_SPEED_X), -rotSpeed, rotSpeed);
            float addX = Mth.clamp(java.lang.Math.min((float) java.lang.Math.max(getDeltaMovement().dot(getViewVector(1)) - 0.24, 0.15), 0.4f) * entityData.get(MOUSE_SPEED_Y), -3.5f, 3.5f);
            float addZ = this.entityData.get(DELTA_ROT) - (this.onGround() ? 0 : 0.004f) * entityData.get(MOUSE_SPEED_X) * (float) getDeltaMovement().dot(getViewVector(1));

            delta_x = addX;
            delta_y = addY;

            this.setYRot(this.getYRot() + delta_y);
            if (!onGround()) {
                this.setXRot(this.getXRot() + delta_x);
                this.setZRot(this.getRoll() - addZ);
            }

            if (!onGround()) {
                float xSpeed = 1 + 20 * Mth.abs(getXRot() / 180);
                float speed = Mth.clamp(Mth.abs(roll) / (90 / xSpeed), 0, 1);

                if (this.roll > 0) {
                    setZRot(roll - java.lang.Math.min(speed, roll));
                } else if (this.roll < 0) {
                    setZRot(roll + java.lang.Math.min(speed, -roll));
                }
            }

            this.setPropellerRot(this.getPropellerRot() + 30 * this.entityData.get(POWER));

            if (upInputDown) {
                upInputDown = false;
                if (entityData.get(GEAR_ROT) == 0 && !onGround()) {
                    entityData.set(GEAR_UP, true);
                } else if (entityData.get(GEAR_ROT) == 85) {
                    entityData.set(GEAR_UP, false);
                }
            }

            if (onGround()) {
                entityData.set(GEAR_UP, false);
            }

            if (entityData.get(GEAR_UP)) {
                entityData.set(GEAR_ROT, java.lang.Math.min(entityData.get(GEAR_ROT) + 5, 85));
            } else {
                entityData.set(GEAR_ROT, java.lang.Math.max(entityData.get(GEAR_ROT) - 5, 0));
            }

            float flapX = (1 - (Mth.abs(getRoll())) / 90) * Mth.clamp(entityData.get(MOUSE_SPEED_Y), -22.5f, 22.5f) - VectorTool.calculateY(getRoll()) * Mth.clamp(entityData.get(MOUSE_SPEED_X), -22.5f, 22.5f);

            setFlap1LRot(Mth.clamp(-flapX - 4 * addZ - this.entityData.get(PLANE_BREAK), -22.5f, 22.5f));
            setFlap1RRot(Mth.clamp(-flapX + 4 * addZ - this.entityData.get(PLANE_BREAK), -22.5f, 22.5f));
            setFlap1L2Rot(Mth.clamp(-flapX - 4 * addZ + this.entityData.get(PLANE_BREAK), -22.5f, 22.5f));
            setFlap1R2Rot(Mth.clamp(-flapX + 4 * addZ + this.entityData.get(PLANE_BREAK), -22.5f, 22.5f));

            setFlap2LRot(Mth.clamp(flapX - 4 * addZ, -22.5f, 22.5f));
            setFlap2RRot(Mth.clamp(flapX + 4 * addZ, -22.5f, 22.5f));

            float flapY = (1 - (Mth.abs(getRoll())) / 90) * Mth.clamp(entityData.get(MOUSE_SPEED_X), -22.5f, 22.5f) + VectorTool.calculateY(getRoll()) * Mth.clamp(entityData.get(MOUSE_SPEED_Y), -22.5f, 22.5f);

            setFlap3Rot(flapY * 5);
        } else if (!onGround()) {
            float diffX;
            this.entityData.set(POWER, java.lang.Math.max(this.entityData.get(POWER) - 0.0003f, 0.02f));
            destroyRot += 0.1f;
            diffX = 90 - this.getXRot();
            this.setXRot(this.getXRot() + diffX * 0.001f * destroyRot);
            this.setZRot(this.getRoll() - destroyRot);
            setDeltaMovement(getDeltaMovement().add(0, -0.03, 0));
            setDeltaMovement(getDeltaMovement().add(0, -destroyRot * 0.005, 0));
        }

        this.entityData.set(POWER, this.entityData.get(POWER) * 0.99f);
        this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * 0.85f);
        this.entityData.set(PLANE_BREAK, this.entityData.get(PLANE_BREAK) * 0.8f);

        if (entityData.get(ENGINE1_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.96f);
        }

        if (entityData.get(ENGINE2_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.96f);
        }

        Matrix4f transform = getVehicleTransform(1);
        double flapAngle = (getFlap1LRot() + getFlap1RRot() + getFlap1L2Rot() + getFlap1R2Rot()) / 4;

        Vector4f force0 = transformPosition(transform, 0, 0, 0);
        Vector4f force1 = transformPosition(transform, 0, 1, 0);

        Vec3 force = new Vec3(force0.x, force0.y, force0.z).vectorTo(new Vec3(force1.x, force1.y, force1.z));

        setDeltaMovement(getDeltaMovement().add(force.scale(getDeltaMovement().dot(getViewVector(1)) * 0.022 * (1 + java.lang.Math.sin((onGround() ? 25 : flapAngle + 25) * Mth.DEG_TO_RAD)))));

        this.setDeltaMovement(this.getDeltaMovement().add(getViewVector(1).scale(0.4 * this.entityData.get(POWER))));
    }

    @Override
    public void move(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        if (!this.level().isClientSide()) {
            MobileVehicleEntity.IGNORE_ENTITY_GROUND_CHECK_STEPPING = true;
        }
        if (level() instanceof ServerLevel && canCollideBlockBeastly()) {
            collideBlockBeastly();
        }

        super.move(movementType, movement);
        if (level() instanceof ServerLevel) {
            if (this.horizontalCollision) {
                collideNormalBlock();
                if (canCollideHardBlock()) {
                    collideHardBlock();
                }
            }

            if (lastTickSpeed < 0.3 || collisionCoolDown > 0) return;
            Entity driver = EntityFindUtil.findEntity(this.level(), this.entityData.get(LAST_DRIVER_UUID));

            if ((verticalCollision)) {
                if (entityData.get(GEAR_ROT) > 10 || Mth.abs(getRoll()) > 20 || Mth.abs(getXRot()) > 30) {
                    this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, driver == null ? this : driver), (float) ((8 + Mth.abs(getRoll() * 0.2f)) * (lastTickSpeed - 0.3) * (lastTickSpeed - 0.3)));
                    if (!this.level().isClientSide) {
                        this.level().playSound(null, this, ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1, 1);
                    }
                    this.bounceVertical(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                } else {
                    if (Mth.abs((float) lastTickVerticalSpeed) > 0.4) {
                        this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, driver == null ? this : driver), (float) (96 * ((Mth.abs((float) lastTickVerticalSpeed) - 0.4) * (lastTickSpeed - 0.3) * (lastTickSpeed - 0.3))));
                        if (!this.level().isClientSide) {
                            this.level().playSound(null, this, ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1, 1);
                        }
                        this.bounceVertical(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                    }
                }

            }

            if (this.horizontalCollision) {
                this.hurt(ModDamageTypes.causeVehicleStrikeDamage(this.level().registryAccess(), this, driver == null ? this : driver), (float) (126 * ((lastTickSpeed - 0.4) * (lastTickSpeed - 0.4))));
                this.bounceHorizontal(Direction.getNearest(this.getDeltaMovement().x(), this.getDeltaMovement().y(), this.getDeltaMovement().z()).getOpposite());
                if (!this.level().isClientSide) {
                    this.level().playSound(null, this, ModSounds.VEHICLE_STRIKE.get(), this.getSoundSource(), 1, 1);
                }
                collisionCoolDown = 4;
                crash = true;
            }
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return tech.vvp.vvp.init.ModSounds.F16_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return entityData.get(POWER) * (sprintInputDown ? 5.5f : 3f);
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getVehicleTransform(1);

        // Позиція пілота: (0, 2.16, 6.04)
        float x = 0f;
        float y = 2.2f + (float) passenger.getMyRidingOffset();
        float z = 6.24f;

        Vector4f worldPosition = transformPosition(transform, x, y, z);
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        copyEntityData(passenger);
    }

    @Override
    public @NotNull Vec3 getDismountLocationForIndex(LivingEntity passenger, int index) {
        Matrix4f transform = getVehicleTransform(1);
        if ((!onGround() || getDeltaMovement().length() >= 0.1)) {
            Vector4f worldPosition = transformPosition(transform, 0, 2f + (float) passenger.getMyRidingOffset(), 3.95f);
            return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        } else {
            return super.getDismountLocationForIndex(passenger, index);
        }
    }

    @Override
    public @NotNull Vec3 getDismountMovement(LivingEntity passenger, int index) {
        return getDeltaMovement().add(new Vec3(0, 4, 0));
    }

    @Override
    public boolean allowEjection() {
        return true;
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getVehicleTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0, 1.35f, 4.15f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public void copyEntityData(Entity entity) {
        entity.setYHeadRot(entity.getYHeadRot() + delta_y);
        entity.setYRot(entity.getYRot() + delta_y);
        entity.setYBodyRot(this.getYRot());
    }

    @Override
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo, getY()), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, yRotO, getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/f16_icon.png");
    }

    @Override
    public Vec3 shootPos(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition = transformPosition(transform, 0, 0, 0);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    @Override
    public Vec3 shootVec(float tickDelta) {
        Matrix4f transform = getVehicleTransform(tickDelta);
        Vector4f worldPosition = transformPosition(transform, 0, 0, 0);
        Vector4f worldPosition2 = transformPosition(transform, 0, -0.03f, 1);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();
    }

    @Override
    public Float gearRot(float tickDelta) {
        return Mth.lerp(tickDelta, gearRotO, entityData.get(GEAR_ROT));
    }

    @Override
    public void vehicleShoot(LivingEntity living, int type) {
        Matrix4f transform = getVehicleTransform(1);

        if (getWeaponIndex(0) == 0) {
            if (this.cannotFire) return;

            boolean hasCreativeAmmo = InventoryTool.hasCreativeAmmoBox(getFirstPassenger());

            Vector4f worldPosition = transformPosition(transform, 0.5f, -0.3f, 7.5f);
            Vector4f worldPosition2 = transformPosition(transform, 0.51f, -0.315f, 8.5f);

            Vec3 shootVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();

            if (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                entityData.set(FIRE_TIME, java.lang.Math.min(entityData.get(FIRE_TIME) + 6, 6));

                var entityToSpawn = ((SmallCannonShellWeapon) getWeapon(0)).create(living);

                entityToSpawn.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                entityToSpawn.shoot(shootVec.x, shootVec.y, shootVec.z, 30, 0.3f);
                level().addFreshEntity(entityToSpawn);

                sendParticle((ServerLevel) this.level(), ParticleTypes.LARGE_SMOKE, worldPosition.x, worldPosition.y, worldPosition.z, 1, 0.2, 0.2, 0.2, 0.001, true);
                sendParticle((ServerLevel) this.level(), ParticleTypes.CLOUD, worldPosition.x, worldPosition.y, worldPosition.z, 2, 0.5, 0.5, 0.5, 0.005, true);

                if (!hasCreativeAmmo) {
                    this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                }
            }

            ShakeClientMessage.sendToNearbyPlayers(this, 5, 6, 5, 12);
        } else if (getWeaponIndex(0) == 1 && this.getEntityData().get(LOADED_MISSILE) > 0 && locked) {
            var Aim120Entity = ((Aim120Weapon) getWeapon(0)).create(living);

            Vector4f worldPosition;

            // Позиции для 4 ракет
            int missileCount = this.getEntityData().get(LOADED_MISSILE);
            if (missileCount == 4) {
                worldPosition = transformPosition(transform, 96f/16f, 34f/16f, -33f/16f);
            } else if (missileCount == 3) {
                worldPosition = transformPosition(transform, -96f/16f, 34f/16f, -33f/16f);
            } else if (missileCount == 2) {
                worldPosition = transformPosition(transform, 72f/16f, 34f/16f, -33f/16f);
            } else {
                worldPosition = transformPosition(transform, -72f/16f, 34f/16f, -33f/16f);
            }

            Aim120Entity.setTargetUuid(getTargetUuid());
            Aim120Entity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            Aim120Entity.shoot(shootVec(1).x, shootVec(1).y, shootVec(1).z, (float) getDeltaMovement().length() + 1, 1);
            living.level().addFreshEntity(Aim120Entity);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3, 1);

            if (this.getEntityData().get(LOADED_MISSILE) == 4) {
                reloadCoolDownMissile = 300;
            }

            this.entityData.set(LOADED_MISSILE, this.getEntityData().get(LOADED_MISSILE) - 1);
            
            // Добавляем задержку между выстрелами (1 секунда)
            reloadCoolDown = 20;
        }
    }

    public float shootingVolume() {
        return entityData.get(FIRE_TIME) * 0.3f;
    }

    public float shootingPitch() {
        return 0.7f + entityData.get(FIRE_TIME) * 0.05f;
    }

    public boolean isFiring() {
        return this.entityData.get(FIRE_TIME) > 0;
    }

    @Override
    public int mainGunRpm(LivingEntity living) {
        if (getWeaponIndex(0) == 0) {
            return sprintInputDown ? 6000 : 4000;
        }
        if (getWeaponIndex(0) == 1) {
            return 120;
        }
        return 0;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        if (getWeaponIndex(0) == 1) {
            return this.entityData.get(AMMO) > 0;
        }
        return this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(living);
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean banHand(LivingEntity entity) {
        return true;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(LivingEntity living) {
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

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return 0;
    }

    @Override
    public double getMouseSensitivity() {
        return zoomVehicle ? 0.03 : 0.07;
    }

    @Override
    public double getMouseSpeedX() {
        return 0.3;
    }

    @Override
    public double getMouseSpeedY() {
        return 0.3;
    }

    @Override
    public boolean isEnclosed(int index) {
        return true;
    }

    @Override
    public int passengerSeatLocation(Entity entity) {
        return 2;
    }

    @Override
    public int getHudColor() {
        return super.getHudColor();
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public Pair<Quaternionf, Quaternionf> getPassengerRotation(Entity entity, float tickDelta) {
        return Pair.of(Axis.XP.rotationDegrees(-this.getViewXRot(tickDelta)), Axis.ZP.rotationDegrees(-this.getRoll(tickDelta)));
    }

    public Matrix4f getClientVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo, getY()), (float) Mth.lerp(ticks, zo, getZ()));
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
            Vector4f maxCameraPosition = transformPosition(transform, 0, 4, -14 - (float) ClientMouseHandler.custom3pDistanceLerp);
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
        return VVP.loc("textures/gui/vehicle/type/aircraft_us.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obb1, this.obb2, this.obb3, this.obb4, this.obb5, this.obb6);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition1 = transformPosition(transform, 0, 37.5f/16f, 19f/16f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 73f/16f, 42.5f/16f, -29.5f/16f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, -73f/16f, 42.5f/16f, -29.5f/16f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, 17.5f/16f, 37.5f/16f, -28f/16f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition5 = transformPosition(transform, -17.5f/16f, 37.5f/16f, -28f/16f);
        this.obb5.center().set(new Vector3f(worldPosition5.x, worldPosition5.y, worldPosition5.z));
        this.obb5.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition6 = transformPosition(transform, 0f, 60.5f/16f, -107f/16f);
        this.obb6.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obb6.setRotation(VectorTool.combineRotations(1, this));
    }
}
