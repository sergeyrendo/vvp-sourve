package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallRocketWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.entity.vehicle.weapon.Fab500Weapon;
import tech.vvp.vvp.init.ModEntities;

import java.util.List;

import static com.atsuishio.superbwarfare.event.ClientEventHandler.zoomVehicle;
import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraPitch;
import static com.atsuishio.superbwarfare.event.ClientMouseHandler.freeCameraYaw;

public class Mi8AMTSHEntity extends ContainerMobileVehicleEntity implements GeoEntity, HelicopterEntity, WeaponVehicleEntity, OBBEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDataAccessor<Float> PROPELLER_ROT = SynchedEntityData.defineId(Mi8AMTSHEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> LOADED_ROCKET = SynchedEntityData.defineId(Mi8AMTSHEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_BOMB = SynchedEntityData.defineId(Mi8AMTSHEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(Mi8AMTSHEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(Mi8AMTSHEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FIRE_TIME = SynchedEntityData.defineId(Mi8AMTSHEntity.class, EntityDataSerializers.INT);


    public boolean engineStart;
    public boolean engineStartOver;
    public int reloadCoolDownMissile;

    public double velocity;
    public int fireIndex;
    public int holdTick;
    public int reloadCoolDownBomb;
    public int holdPowerTick;
    public float destroyRot;

    public Vec3 bombLandingPosO;
    public Vec3 bombLandingPos;

    public float delta_x;
    public float delta_y;
    public OBB obbCabina;
    public OBB obbXvost;
    public OBB obbWing1;
    public OBB obbWing2;


    public Mi8AMTSHEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.MI_8_AMTSH.get(), world);
    }

    public Mi8AMTSHEntity(EntityType<Mi8AMTSHEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obbCabina = new OBB(this.position().toVector3f(), new Vector3f(50f/32f, 50f/32f, 183f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obbXvost = new OBB(this.position().toVector3f(), new Vector3f(50f/32f, 27f/32f, 170f/32f), new Quaternionf(), OBB.Part.BODY);
        this.obbWing1 = new OBB(this.position().toVector3f(), new Vector3f(32f/32f, 20f/32f, 106f/32f), new Quaternionf(), OBB.Part.ENGINE1);
        this.obbWing2 = new OBB(this.position().toVector3f(), new Vector3f(50f/32f, 37f/32f, 34f/32f), new Quaternionf(), OBB.Part.ENGINE2);
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

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                            .blockInteraction(VehicleConfig.AH_6_CANNON_DESTROY.get() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.KEEP)
                            .damage(VehicleConfig.AH_6_CANNON_DAMAGE.get().floatValue())
                            .explosionDamage(VehicleConfig.AH_6_CANNON_EXPLOSION_DAMAGE.get().floatValue())
                            .explosionRadius(VehicleConfig.AH_6_CANNON_EXPLOSION_RADIUS.get().floatValue())
                            .sound(ModSounds.INTO_CANNON.get())
                            .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_20mm.png"))
                            .sound1p(ModSounds.HELICOPTER_CANNON_FIRE_1P.get())
                            .sound3p(ModSounds.HELICOPTER_CANNON_FIRE_3P.get())
                            .sound3pFar(ModSounds.HELICOPTER_CANNON_FAR.get())
                            .sound3pVeryFar(ModSounds.HELICOPTER_CANNON_VERYFAR.get()),
                        new SmallRocketWeapon()
                                .damage(VehicleConfigVVP.BLACKHAWK_ROCKET_DAMAGE.get().floatValue())
                                .explosionDamage(VehicleConfigVVP.BLACKHAWK_ROCKET_EXPLOSION_DAMAGE.get().floatValue())
                                .explosionRadius(VehicleConfigVVP.BLACKHAWK_ROCKET_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(ModSounds.SMALL_ROCKET_FIRE_1P.get())
                                .sound3p(ModSounds.SMALL_ROCKET_FIRE_3P.get()),
                        new Fab500Weapon()
                                .sound(ModSounds.INTO_MISSILE.get()),
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
        this.entityData.define(PROPELLER_ROT, 0.0f);
        this.entityData.define(LOADED_ROCKET, 0);
        this.entityData.define(LOADED_BOMB, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(FIRE_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("LoadedRocket", this.entityData.get(LOADED_ROCKET));
        compound.putFloat("PropellerRot", this.entityData.get(PROPELLER_ROT));
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
        compound.putInt("LoadedBomb", this.entityData.get(LOADED_BOMB));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_ROCKET, compound.getInt("LoadedRocket"));
        this.entityData.set(LOADED_BOMB, compound.getInt("LoadedBomb"));
        this.entityData.set(PROPELLER_ROT, compound.getFloat("PropellerRot"));
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
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

        // Обработка загрузки ракет
        if (stack.getItem() == ModItems.SMALL_ROCKET.get() && this.entityData.get(LOADED_ROCKET) < 60) {
            this.entityData.set(LOADED_ROCKET, this.entityData.get(LOADED_ROCKET) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (stack.getItem() == tech.vvp.vvp.init.ModItems.FAB_500_ITEM.get() && this.entityData.get(LOADED_BOMB) < 1) {
            // 装载航弹
            this.entityData.set(LOADED_BOMB, this.entityData.get(LOADED_BOMB) + 1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {  // Только на сервере
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 7;  // Количество типов (default=0, desert=1, forest=2)
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

        // Вызов родительского метода для стандартного взаимодействия (открытие инвентаря)
        return super.interact(player, hand);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        updateOBB();

        bombLandingPosO = bombLandingPos;

        // if (this.tickCount % 20 == 0) {
        //     handleRadar();
        // }

        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            if (reloadCoolDownBomb > 0) {
                reloadCoolDownBomb--;
            }
            handleAmmo();
        }

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

        if (reloadCoolDownMissile > 0) {
            reloadCoolDownMissile--;
        }

        if (level().isClientSide) {
            bombLandingPos = ProjectileCalculator.calculatePreciseImpactPoint(level(), shootPos(1), shootVec(1), -0.06);
        }

        releaseDecoy();
        lowHealthWarning();
        this.terrainCompact(2.7f, 2.7f);

        this.refreshDimensions();
    }

    private void handleAmmo() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        int ammoCount = countItem(ModItems.SMALL_SHELL.get());

        if ((hasItem(ModItems.SMALL_ROCKET.get()) || InventoryTool.hasCreativeAmmoBox(player)) && reloadCoolDown == 0 && this.getEntityData().get(LOADED_ROCKET) < 60) {
            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) + 1);
            reloadCoolDown = 40;
            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_ROCKET.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.MISSILE_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if ((hasItem(tech.vvp.vvp.init.ModItems.FAB_500_ITEM.get()) || InventoryTool.hasCreativeAmmoBox(player)) && reloadCoolDownBomb == 0 && this.getEntityData().get(LOADED_BOMB) < 1) {
            this.entityData.set(LOADED_BOMB, this.getEntityData().get(LOADED_BOMB) + 1);
            reloadCoolDownBomb = 300;
            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                this.getItemStacks().stream().filter(stack -> stack.is(tech.vvp.vvp.init.ModItems.FAB_500_ITEM.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, ModSounds.BOMB_RELOAD.get(), this.getSoundSource(), 2, 1);
        }

        if (this.getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, ammoCount);
        } else if (this.getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_ROCKET));
        } else if (this.getWeaponIndex(0) == 2) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_BOMB));
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
                    this.entityData.set(DELTA_ROT,
                            this.entityData.get(DELTA_ROT) - 1.2f * Math.min(holdTick, 5) * this.entityData.get(POWER));
                } else if (this.leftInputDown) {
                    holdTick++;
                    this.entityData.set(DELTA_ROT,
                            this.entityData.get(DELTA_ROT) + 1.2f * Math.min(holdTick, 5) * this.entityData.get(POWER));
                } else {
                    holdTick = 0;
                }

                // управление (среднее между "очень тяжело" и "легко")
                delta_x = ((this.onGround()) ? 0 : 0.9f) * entityData.get(MOUSE_SPEED_Y) * this.entityData.get(PROPELLER_ROT);
                delta_y = Mth.clamp((this.onGround() ? 0.05f : 1.2f) * entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT), -7f, 7f);

                this.setYRot(this.getYRot() + delta_y);
                this.setXRot(this.getXRot() + delta_x);
                this.setZRot(this.getRoll() - this.entityData.get(DELTA_ROT)
                        + (this.onGround() ? 0 : 0.15f) * entityData.get(MOUSE_SPEED_X) * this.entityData.get(PROPELLER_ROT));
            }

            if (this.level() instanceof ServerLevel) {
                if (this.getEnergy() > 0) {
                    boolean up = upInputDown || forwardInputDown;
                    boolean down = this.downInputDown;

                    if (!engineStart && up) {
                        engineStart = true;
                        this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.MI8_START.get(), this.getSoundSource(), 3, 1);
                    }

                    if (up && engineStartOver) {
                        holdPowerTick++;
                        this.entityData.set(POWER,
                                Math.min(this.entityData.get(POWER) + 0.00004f * Math.min(holdPowerTick, 10), 0.10f));
                    }

                    if (engineStartOver) {
                        if (down) {
                            holdPowerTick++;
                            this.entityData.set(POWER,
                                    Math.max(this.entityData.get(POWER) - 0.0008f * Math.min(holdPowerTick, 5),
                                            this.onGround() ? 0 : 0.025f));
                        } else if (backInputDown) {
                            holdPowerTick++;
                            this.entityData.set(POWER,
                                    Math.max(this.entityData.get(POWER) - 0.0008f * Math.min(holdPowerTick, 5),
                                            this.onGround() ? 0 : 0.052f));
                            if (passenger != null) {
                                passenger.setXRot(0.8f * passenger.getXRot());
                            }
                        }
                    }

                    if (engineStart && !engineStartOver) {
                        this.entityData.set(POWER,
                                Math.min(this.entityData.get(POWER) + 0.0010f, 0.045f));
                    }

                    if (!(up || down || backInputDown) && engineStartOver) {
                        if (this.getDeltaMovement().y() < 0) {
                            this.entityData.set(POWER,
                                    Math.min(this.entityData.get(POWER) + 0.00015f, 0.10f));
                        } else {
                            this.entityData.set(POWER,
                                    Math.max(this.entityData.get(POWER) - (this.onGround() ? 0.00005f : 0.00015f), 0));
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
        this.entityData.set(PROPELLER_ROT,
                Mth.lerp(0.12f, this.entityData.get(PROPELLER_ROT), this.entityData.get(POWER)));
        this.setPropellerRot(this.getPropellerRot() + 30 * this.entityData.get(PROPELLER_ROT));
        this.entityData.set(PROPELLER_ROT, this.entityData.get(PROPELLER_ROT) * 0.9995f);

        if (engineStart) {
            this.consumeEnergy((int) (VehicleConfigVVP.BLACKHAWK_MIN_ENERGY_COST.get()
                    + this.entityData.get(POWER) * ((VehicleConfigVVP.BLACKHAWK_MIN_ENERGY_COST.get()
                    - VehicleConfigVVP.BLACKHAWK_MIN_ENERGY_COST.get()) / 0.12)));
        }

        if (entityData.get(ENGINE1_DAMAGED)) {
            this.entityData.set(POWER, this.entityData.get(POWER) * 0.98f);
        }

        Matrix4f transform = getVehicleTransform(1);
        Vector4f force0 = transformPosition(transform, 0, 0, 0);
        Vector4f force1 = transformPosition(transform, 0, 1, 0);

        Vec3 force = new Vec3(force0.x, force0.y, force0.z)
                .vectorTo(new Vec3(force1.x, force1.y, force1.z));

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
        return tech.vvp.vvp.init.ModSounds.MI8_IDLE.get();
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

        // Получаем базовую трансформацию техники и индекс пассажира
        Matrix4f transform = getVehicleTransform(1.0f);
        int i = this.getOrderedPassengers().indexOf(passenger);

        // Получаем стандартное смещение пассажира для корректной высоты камеры
        float riderOffset = (float) passenger.getMyRidingOffset();

        Vector4f worldPosition;
        float x, y, z;

        // Используем switch для определения координат для каждого из 10 мест
        switch (i) {
            case 0:
                x = 0.73f; y = 25.1459f/16f - 1.45f; z = 3.7f;
                break;
            case 1:
                x = -0.73f; y = 25.1459f/16f - 1.45f; z = 3.7f;
                break;
            case 2:
                x = 14f/16f;  y = 26.2306f/16f - 1.6f; z = 12.8402f/16f;
                break;
            case 3:
                x = 14f/16f;  y = 26.2306f/16f - 1.6f; z = -6.1598f/16f;
                break;
            case 4:
                x = 14f/16f;  y = 26.2306f/16f - 1.7f; z = -23.1598f/16f;
                break;
            case 5:
                x = 14f/16f;  y = 26.2306f/16f - 1.75f; z = -39.1598f/16f;
                break;
            case 6:
                x = 14f/16f;  y = 26.2306f/16f - 1.8f; z = -50.1598f/16f;
                break;

            case 7:
                x = -14f/16f;  y = 26.2306f/16f - 1.6f; z = 12.8402f/16f;
                break;
            case 8:
                x = -14/16f;  y = 26.2306f/16f - 1.6f; z = -6.1598f/16f;
                break;
            case 9:
                x = -14/16f;  y = 26.2306f/16f - 1.7f; z = -23.1598f/16f;
                break;
            case 10:
                x = -14/16f;  y = 26.2306f/16f - 1.75f; z = -39.1598f/16f;
                break;
            case 11:
                x = -14/16f;  y = 26.2306f/16f - 1.8f; z = -50.1598f/16f;
                break;
            default:
                x = 0.0f;    y = 2.0f;   z = 0.0f;
                break;
        }

        // Применяем смещение к координатам и вычисляем позицию в мире
        worldPosition = transformPosition(transform, x, y + riderOffset, z);

        // Устанавливаем позицию пассажира
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        // Копируем данные сущности (важно для правильного вращения камеры и т.д.)
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
    public void vehicleShoot(LivingEntity living, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (InventoryTool.hasCreativeAmmoBox(getNthEntity(i))) {
                hasCreativeAmmo = true;
            }
        }

        Matrix4f transform = getVehicleTransform(1);

        if (getWeaponIndex(0) == 0) {
            if (this.cannotFire) return;

            Vector4f worldPosition = transformPosition(transform, 42f/16f, -20f/16f, 14f/16f);
            Vector4f worldPosition2 = transformPosition(transform, 42f/16f + 0.01f, -20f/16f - 0.015f, 8.85210625f);

            Vec3 shootVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();


            if (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                entityData.set(FIRE_TIME, Math.min(entityData.get(FIRE_TIME) + 6, 6));

                var entityToSpawn = ((SmallCannonShellWeapon) getWeapon(0)).create(living);

                entityToSpawn.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
                entityToSpawn.shoot(shootVec.x, shootVec.y, shootVec.z, 30, 0.5f);
                level().addFreshEntity(entityToSpawn);

                if (!hasCreativeAmmo) {
                    this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                }

            }

            ShakeClientMessage.sendToNearbyPlayers(this, 5, 6, 5, 12);

            this.entityData.set(HEAT, this.entityData.get(HEAT) + 2);
        } else if (getWeaponIndex(0) == 1 && this.getEntityData().get(LOADED_ROCKET) > 0) {

            var heliRocketEntity = ((SmallRocketWeapon) getWeapon(0)).create(living);

            Vector4f worldPosition;
            Vector4f worldPosition2;

            if (fireIndex == 0) {
                worldPosition = transformPosition(transform, 54f/16f, -19f/16f, 10f/16f);
                worldPosition2 = transformPosition(transform, 54f/16f + 0.01f, -19f/16f - 0.015f, 8.85210625f);
                fireIndex = 1;
            } else {
                worldPosition = transformPosition(transform, -54f/16f, -19f/16f, 10f/16f);
                worldPosition2 = transformPosition(transform, -54f/16f + 0.01f, -19f/16f - 0.015f, 8.85210625f);
                fireIndex = 0;
            }

            Vec3 shootVec = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z).vectorTo(new Vec3(worldPosition2.x, worldPosition2.y, worldPosition2.z)).normalize();

            heliRocketEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            heliRocketEntity.shoot(shootVec.x, shootVec.y, shootVec.z, 7, 0.25f);
            living.level().addFreshEntity(heliRocketEntity);

            playShootSound3p(living, 0, 6, 6, 6, new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.entityData.set(LOADED_ROCKET, this.getEntityData().get(LOADED_ROCKET) - 1);
            reloadCoolDown = 40;

        } else if (getWeaponIndex(0) == 2 && this.getEntityData().get(LOADED_BOMB) > 0) {
            var Fab500Entity = ((Fab500Weapon) getWeapon(0)).create(living);

            Vector4f worldPosition;

            if (this.getEntityData().get(LOADED_BOMB) == 1) {
                worldPosition = transformPosition(transform, -43f/16f, 18f/16f, -11f/16f);
            } else {
                worldPosition = transformPosition(transform, -43f/16f, 18f/16f, -11f/16f);
            }

            Fab500Entity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            Fab500Entity.shoot(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z, (float) getDeltaMovement().scale(0.75).length(), 0.5f);
            living.level().addFreshEntity(Fab500Entity);

            BlockPos pos = BlockPos.containing(new Vec3(worldPosition.x, worldPosition.y, worldPosition.z));

            this.level().playSound(null, pos, ModSounds.BOMB_RELEASE.get(), SoundSource.PLAYERS, 3, 1);

            if (this.getEntityData().get(LOADED_BOMB) == 2) {
                reloadCoolDownBomb = 300;
            }
            this.entityData.set(LOADED_BOMB, this.getEntityData().get(LOADED_BOMB) - 1);
        }
    }

    @Override
    public int mainGunRpm(LivingEntity living) {
        if (getWeaponIndex(0) == 0) {
            return 400;
        } else if (getWeaponIndex(0) == 1) {
            return 300;
        }
        return 400;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        if (getWeaponIndex(0) == 0) {
            return (this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(living)) && !cannotFire;
        } else if (getWeaponIndex(0) == 1 || getWeaponIndex(0) == 2) {
            return this.entityData.get(AMMO) > 0;
        }
        return false;
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
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

    public int getMaxPassengers() {
        return 12;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/uh60_icon.png");
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
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        Vec3 p0 = bombLandingPosO;
        Vec3 p1 = bombLandingPos;
        Vec3 p2 = getViewVector(partialTicks);
        if (p0 != null && p1 != null) {
            p2 = cameraPos.vectorTo(p0.lerp(p1, partialTicks));
        }

        if (this.getSeatIndex(player) == 0) {
            if (getWeaponIndex(0) == 2 && zoomVehicle) {
                return new Vec2((float) (-getYRotFromVector(p2) - freeCameraYaw), (float) (-getXRotFromVector(p2) + freeCameraPitch));
            }
            return new Vec2((float) (getRotY(partialTicks) - freeCameraYaw), (float) (getRotX(partialTicks) + freeCameraPitch));
        }

        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (this.getSeatIndex(player) == 0) {

            if (getWeaponIndex(0) == 2 && zoomVehicle) {
                return shootPos(partialTicks);
            }

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
        return VVP.loc("textures/gui/vehicle/type/aircraft.png");
    }

    @Override
    public List<OBB> getOBBs() {
        return List.of(this.obbCabina, this.obbWing1, this.obbXvost, this.obbWing2);
    }

    @Override
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);


        Vector4f worldPosition2 = transformPosition(transform, 0, 34f/16f - 1.45f, -4.5f/16f);
        this.obbCabina.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obbCabina.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition8 = transformPosition(transform, 0, 51.5f/16f - 1.45f, -149f/16f);
        this.obbXvost.center().set(new Vector3f(worldPosition8.x, worldPosition8.y, worldPosition8.z));
        this.obbXvost.setRotation(VectorTool.combineRotations(1, this));


        Vector4f worldPosition6 = transformPosition(transform, 0, 69f/16f - 1.45f, -11f/16f);
        this.obbWing1.center().set(new Vector3f(worldPosition6.x, worldPosition6.y, worldPosition6.z));
        this.obbWing1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition7 = transformPosition(transform, 0, 56.5f/16f - 1.45f, -251f/16f);
        this.obbWing2.center().set(new Vector3f(worldPosition7.x, worldPosition7.y, worldPosition7.z));
        this.obbWing2.setRotation(VectorTool.combineRotations(1, this));


    }

    private PlayState firePredicate(AnimationState<Mi8AMTSHEntity> event) {
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
    public Matrix4f getVehicleTransform(float ticks) {
        Matrix4f transform = new Matrix4f();
        transform.translate((float) Mth.lerp(ticks, xo, getX()), (float) Mth.lerp(ticks, yo + 1.45f, getY() + 1.45f), (float) Mth.lerp(ticks, zo, getZ()));
        transform.rotate(Axis.YP.rotationDegrees(-Mth.lerp(ticks, yRotO, getYRot())));
        transform.rotate(Axis.XP.rotationDegrees(Mth.lerp(ticks, xRotO, getXRot())));
        transform.rotate(Axis.ZP.rotationDegrees(Mth.lerp(ticks, prevRoll, getRoll())));
        return transform;
    }
}
