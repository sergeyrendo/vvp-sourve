package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.ProjectileWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.SmallCannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.WgMissileWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.ExplosionConfigVVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.Ammo;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
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
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
// import net.minecraftforge.api.distmarker.Dist;
// import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
// import tech.vvp.vvp.config.server.VehicleConfigVVP;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.atsuishio.superbwarfare.tools.ParticleTool.sendParticle;

public class BradleyUkrEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity, OBBEntity {
// это там оно? неа, я сам хуй знает, ща буду проверять все, кароче надо будет найти где ивент сажания на технику, и просто добавить условие
    // в пизду этих китайцев с нуля уже написать было бы не так мучительно мы ещё же новички
    // тут или у китайцев спрашивать или я хуй знает
    // ща еще одну хуйню чекну // та это пиздец
    // чек дс
    public static final EntityDataAccessor<Integer> LOADED_AP = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LOADED_MISSILE = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_COUNT = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CURRENT_MISSILE = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> HAS_MANGAL = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_FOLIAGE = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> HAS_FOLIAGE_BODY = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> KOROBKI = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> MISSILE_FIRE_COOLDOWN = SynchedEntityData.defineId(BradleyUkrEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public OBB obb1;
    public OBB obb2;
    public OBB obb3;
    public OBB obb4;
    public OBB obbTurret;
    public OBB obbMangal;

    public BradleyUkrEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(ModEntities.BRADLEY_UKR.get(), world);
    }

    public BradleyUkrEntity(EntityType<BradleyUkrEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.obb1 = new OBB(this.position().toVector3f(), new Vector3f(2.3125f, 0.625f, 3.09375f), new Quaternionf(), OBB.Part.BODY);
        this.obb2 = new OBB(this.position().toVector3f(), new Vector3f(2.3125f, 0.625f, 0.6875f), new Quaternionf(), OBB.Part.BODY);
        this.obb3 = new OBB(this.position().toVector3f(), new Vector3f(0.4375f, 0.625f, 3.21875f), new Quaternionf(), OBB.Part.WHEEL_LEFT);
        this.obb4 = new OBB(this.position().toVector3f(), new Vector3f(0.4375f, 0.625f, 3.21875f), new Quaternionf(), OBB.Part.WHEEL_RIGHT);
        this.obbTurret = new OBB(this.position().toVector3f(), new Vector3f(1.406f, 0.469f, 1.688f), new Quaternionf(), OBB.Part.TURRET);
        this.obbMangal = new OBB(this.position().toVector3f(), new Vector3f(1.906f, 0.094f, 1.594f), new Quaternionf(), OBB.Part.EMPTY);

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
    public static BradleyUkrEntity clientSpawn(PlayMessages.SpawnEntity packet, Level world) {
        EntityType<?> entityTypeFromPacket = BuiltInRegistries.ENTITY_TYPE.byId(packet.getTypeId());
        if (entityTypeFromPacket == null) {
            Mod.LOGGER.error("Failed to create entity from packet: Unknown entity type id: " + packet.getTypeId());
            return null;
        }
        if (!(entityTypeFromPacket instanceof EntityType<?>)) {
            Mod.LOGGER.error("Retrieved EntityType is not an instance of EntityType<?> for id: " + packet.getTypeId());
            return null;
        }

        EntityType<BradleyUkrEntity> castedEntityType = (EntityType<BradleyUkrEntity>) entityTypeFromPacket;
        BradleyUkrEntity entity = new BradleyUkrEntity(castedEntityType, world);
        return entity;
    }


    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        new SmallCannonShellWeapon()
                                .damage(VehicleConfigVVP.BRADLEY_CANNON_DAMAGE.get())
                                .explosionDamage(VehicleConfigVVP.BRADLEY_CANNON_EXPLOSION_DAMAGE.get())
                                .explosionRadius(VehicleConfigVVP.BRADLEY_CANNON_EXPLOSION_RADIUS.get().floatValue())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/cannon_30mm.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.BUSHMASTER_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.BUSHMASTER_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.BUSHMASTER_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.BUSHMASTER_VERYFAR.get()),
                        new ProjectileWeapon()
                                .damage(6.5f)
                                .headShot(2)
                                .zoom(false)
                                .sound(ModSounds.INTO_CANNON.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/gun_7_62mm.png"))
                                .sound1p(ModSounds.COAX_FIRE_1P.get())
                                .sound3p(ModSounds.M_60_FIRE_3P.get())
                                .sound3pFar(ModSounds.M_60_FAR.get())
                                .sound3pVeryFar(ModSounds.M_60_VERYFAR.get()),
                        new WgMissileWeapon()
                                .damage(ExplosionConfigVVP.TOW_MISSILE_DAMAGE.get())
                                .explosionDamage(ExplosionConfigVVP.TOW_MISSILE_EXPLOSION_DAMAGE.get())
                                .explosionRadius(ExplosionConfigVVP.TOW_MISSILE_EXPLOSION_RADIUS.get())
                                .sound(ModSounds.INTO_MISSILE.get())
                                .sound1p(tech.vvp.vvp.init.ModSounds.TOW_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.TOW_1P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.TOW_FAR.get()),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(2.75 + ClientMouseHandler.custom3pDistanceLerp, 1, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_AP, 0);
        this.entityData.define(LOADED_MISSILE, 0);
        this.entityData.define(MISSILE_COUNT, 0);
        this.entityData.define(CURRENT_MISSILE, 0);
        this.entityData.define(HAS_MANGAL, false);
        this.entityData.define(HAS_FOLIAGE, false);
        this.entityData.define(HAS_FOLIAGE_BODY, false);
        this.entityData.define(KOROBKI, false);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(MISSILE_FIRE_COOLDOWN, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("loaded_ap", this.entityData.get(LOADED_AP));
        compound.putInt("LoadedMissile", this.entityData.get(LOADED_MISSILE));
        compound.putBoolean("HasMangal", this.entityData.get(HAS_MANGAL));
        compound.putBoolean("HasFoliage", this.entityData.get(HAS_FOLIAGE));
        compound.putBoolean("HasFoliageBody", this.entityData.get(HAS_FOLIAGE_BODY));
        compound.putBoolean("Korobki", this.entityData.get(KOROBKI));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putInt("MissileFireCooldown", this.entityData.get(MISSILE_FIRE_COOLDOWN));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_AP, compound.getInt("loaded_ap"));
        this.entityData.set(LOADED_MISSILE, compound.getInt("LoadedMissile"));
        this.entityData.set(HAS_MANGAL, compound.getBoolean("HasMangal"));
        this.entityData.set(HAS_FOLIAGE_BODY, compound.getBoolean("HasFoliageBody"));
        this.entityData.set(KOROBKI, compound.getBoolean("Korobki"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(MISSILE_FIRE_COOLDOWN, compound.getInt("MissileFireCooldown"));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float) (getDeltaMovement().length() * 0.3), random.nextFloat() * 0.15f + 1.05f);
    }


    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.25f) * damage);
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

        // так хитбоксы тож изи сделал, следи за мной
// йооо а как ты этот хитбокс реализовал
        // ну по такой логике можно действительно и с камуфляжами шота придумать
        // слеж слежу

        if (this.level() instanceof ServerLevel) {
            if (reloadCoolDown > 0) {
                reloadCoolDown--;
            }
            int missileCooldown = this.entityData.get(MISSILE_FIRE_COOLDOWN);
            if (missileCooldown > 0) {
                this.entityData.set(MISSILE_FIRE_COOLDOWN, missileCooldown - 1);
            }
            this.handleAmmo();
        }

        double fluidFloat;
        fluidFloat = 0.052 * getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, fluidFloat, 0.0));

        if (this.onGround()) {
            float f0 = 0.54f + 0.25f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.05 * getDeltaMovement().dot(getViewVector(1)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.99, f0));

        } else if (this.isInWater()) {
            float f1 = 0.74f + 0.09f * Mth.abs(90 - (float) calculateAngle(this.getDeltaMovement(), this.getViewVector(1))) / 90;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1).normalize().scale(0.04 * getDeltaMovement().dot(getViewVector(1)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.85, f1));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        if (this.level() instanceof ServerLevel serverLevel && this.isInWater() && this.getDeltaMovement().length() > 0.1) {
            sendParticle(serverLevel, ParticleTypes.CLOUD, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 4 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
            sendParticle(serverLevel, ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + 0.5 * this.getDeltaMovement().x, this.getY() + getSubmergedHeight(this) - 0.2, this.getZ() + 0.5 * this.getDeltaMovement().z, (int) (2 + 10 * this.getDeltaMovement().length()), 0.65, 0, 0.65, 0, true);
        }

        turretAngle(3f, 4.7f);
        lowHealthWarning();
        this.terrainCompact(2.7f, 3.61f);
        inertiaRotate(1.25f);

        releaseSmokeDecoy(getTurretVector(1));

        this.refreshDimensions();
    }


    @Override
    public boolean canCollideHardBlock() {
        return getDeltaMovement().horizontalDistance() > 0.09 || Mth.abs(this.entityData.get(POWER)) > 0.15;
    }

    private void handleAmmo() {
        if (!(this.getFirstPassenger() instanceof Player player)) return;

        int ammoCount = this.getItemStacks().stream().filter(stack -> {
            if (stack.is(ModItems.AMMO_BOX.get())) {
                return Ammo.RIFLE.get(stack) > 0;
            }
            return false;
        }).mapToInt(Ammo.RIFLE::get).sum() + countItem(ModItems.RIFLE_AMMO.get());

        if ((hasItem(ModItems.WIRE_GUIDE_MISSILE.get())
                || InventoryTool.hasCreativeAmmoBox(player))
                && this.reloadCoolDown <= 0 && this.getEntityData().get(LOADED_MISSILE) < 2) {
            this.entityData.set(LOADED_MISSILE, this.entityData.get(LOADED_MISSILE) + 1);
            this.reloadCoolDown = 160;
            if (!InventoryTool.hasCreativeAmmoBox(player)) {
                this.getItemStacks().stream().filter(stack -> stack.is(ModItems.WIRE_GUIDE_MISSILE.get())).findFirst().ifPresent(stack -> stack.shrink(1));
            }
            this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.TOW_RELOAD.get(), this.getSoundSource(), 1, 1);
        }

        if (getWeaponIndex(0) == 0) {
            this.entityData.set(AMMO, countItem(com.atsuishio.superbwarfare.init.ModItems.SMALL_SHELL.get()));
        } else if (getWeaponIndex(0) == 1) {
            this.entityData.set(AMMO, ammoCount);
        } else if (getWeaponIndex(0) == 2) {
            this.entityData.set(AMMO, this.getEntityData().get(LOADED_MISSILE));
        }

        this.entityData.set(MISSILE_COUNT, countItem(ModItems.WIRE_GUIDE_MISSILE.get()));

    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = getBarrelTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    @Override
    public void vehicleShoot(Player player, int type) {
        boolean hasCreativeAmmo = false;
        for (int i = 0; i < getMaxPassengers() - 1; i++) {
            if (getNthEntity(i) instanceof Player pPlayer && InventoryTool.hasCreativeAmmoBox(pPlayer)) {
                hasCreativeAmmo = true;
            }
        }

        Matrix4f transform = getBarrelTransform(1);

        if (this.getWeaponIndex(0) == 0) {
            if (this.cannotFire) {
                return;
            }
            // Убираем переключение и всегда используем левую сторону
            float x = -0.1f; // Фиксированная левая позиция
            float y = -0.5f;
            float z = 2.5f;

            Vector4f worldPosition = this.transformPosition(transform, x, y, z);
            SmallCannonShellEntity smallCannonShell = ((SmallCannonShellWeapon)this.getWeapon(0)).create(player);
            smallCannonShell.setPos(
                    (double)worldPosition.x - 1.1 * this.getDeltaMovement().x,
                    (double)worldPosition.y,
                    (double)worldPosition.z - 1.1 * this.getDeltaMovement().z
            );
            Vec3 barrelVec = this.getBarrelVector(1.0F);
            smallCannonShell.shoot(
                    barrelVec.x,
                    barrelVec.y,
                    barrelVec.z,
                    35.0F,
                    0.2F
            );
            this.level().addFreshEntity(smallCannonShell);

            ParticleTool.sendParticle(
                    (ServerLevel)this.level(),
                    ParticleTypes.LARGE_SMOKE,
                    (double)worldPosition.x - 1.1 * this.getDeltaMovement().x,
                    (double)worldPosition.y,
                    (double)worldPosition.z - 1.1 * this.getDeltaMovement().z,
                    1, 0.02, 0.02, 0.02, 0.0F, false
            );

            if (!player.level().isClientSide) {
                this.playShootSound3p(player, 0, 4, 12, 24);
            }

            Level level = player.level();
            Vec3 center = new Vec3(this.getX(), this.getEyeY(), this.getZ());

            for (Entity target : level.getEntitiesOfClass(Entity.class, (new AABB(center, center)).inflate(4.0F)).stream()
                    .sorted(Comparator.comparingDouble((e) -> e.distanceToSqr(center))).toList()) {
                if (target instanceof ServerPlayer serverPlayer) {
                    Mod.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new ShakeClientMessage(6.0, 5.0, 9.0, this.getX(), this.getEyeY(), this.getZ()));
                }
            }

            this.entityData.set(CANNON_RECOIL_TIME, 40);
            this.entityData.set(YAW, this.getTurretYRot());
            this.entityData.set(HEAT, this.entityData.get(HEAT) + 2);
            this.entityData.set(FIRE_ANIM, 3);

            if (hasCreativeAmmo) return;
            this.getItemStacks().stream().filter(stack -> stack.is(ModItems.SMALL_SHELL.get())).findFirst().ifPresent(stack -> stack.shrink(1));
        }

        else if (getWeaponIndex(0) == 1) {
            if (this.cannotFireCoax) return;
            float x = -0.65f;
            float y = -0.4f;
            float z = 1.3f;

            Vector4f worldPosition = transformPosition(transform, x, y, z);

            if (this.entityData.get(AMMO) > 0 || hasCreativeAmmo) {
                var projectileRight = ((ProjectileWeapon) getWeapon(0)).create(player).setGunItemId(this.getType().getDescriptionId());

                projectileRight.bypassArmorRate(0.2f);
                projectileRight.setPos(worldPosition.x - 1.1 * this.getDeltaMovement().x, worldPosition.y, worldPosition.z - 1.1 * this.getDeltaMovement().z);
                projectileRight.shoot(player, getBarrelVector(1).x, getBarrelVector(1).y + 0.002f, getBarrelVector(1).z, 36,
                        0.25f);
                this.level().addFreshEntity(projectileRight);

                if (!hasCreativeAmmo) {
                    ItemStack ammoBox = this.getItemStacks().stream().filter(stack -> {
                        if (stack.is(ModItems.AMMO_BOX.get())) {
                            return Ammo.RIFLE.get(stack) > 0;
                        }
                        return false;
                    }).findFirst().orElse(ItemStack.EMPTY);

                    if (!ammoBox.isEmpty()) {
                        Ammo.RIFLE.add(ammoBox, -1);
                    } else {
                        this.getItemStacks().stream().filter(stack -> stack.is(ModItems.RIFLE_AMMO.get())).findFirst().ifPresent(stack -> stack.shrink(1));
                    }
                }
            }

            this.entityData.set(COAX_HEAT, this.entityData.get(COAX_HEAT) + 3);
            this.entityData.set(FIRE_ANIM, 2);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 0, 3, 6, 12);
            }
        }  else if (getWeaponIndex(0) == 2 && this.getEntityData().get(LOADED_MISSILE) > 0) {
            if (this.entityData.get(MISSILE_FIRE_COOLDOWN) > 0) {
                return; // Если кулдаун активен, прерываем выстрел
            }
            int currentMissile = this.entityData.get(CURRENT_MISSILE);
            // Определяем, верхняя или нижняя ракета. Четные (0, 2) - сверху, нечетные (1, 3) - снизу.
            boolean isTopSide = currentMissile % 2 == 0;

            // Координаты для верхней и нижней стороны.
            // X теперь постоянный (ракеты по центру), а Y изменяется.
            float x = 1.183f; // Горизонтальное смещение, 0.0f если ракеты по центру.
            float y = isTopSide ? 0.2f : 0.0f; // Вертикальное смещение. Подберите значения под вашу модель!
            float z = 0.448f; // Смещение вперед/назад, можно оставить. -1.183f, 0.000f, -0.448f

            Matrix4f transformT = getBarrelTransform(1);
            Vector4f worldPosition = transformPosition(transformT, x, y, z);

            var wgMissileEntity = ((WgMissileWeapon) getWeapon(0)).create(player);

            wgMissileEntity.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            wgMissileEntity.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, 2f, 0f);
            player.level().addFreshEntity(wgMissileEntity);

            if (!player.level().isClientSide) {
                playShootSound3p(player, 0, 6, 0, 0);
            }

            // Увеличиваем счетчик текущего ПТУРа
            this.entityData.set(CURRENT_MISSILE, (currentMissile + 1) % 4);
            this.entityData.set(LOADED_MISSILE, this.entityData.get(LOADED_MISSILE) - 1);
            this.entityData.set(MISSILE_FIRE_COOLDOWN, 120); // Устанавливаем кулдаун 4 секунды (4 * 20 тиков)
            reloadCoolDown = 160;
        }
    }

    @Override
    public void travel() {
        trackEngine(true, 0.052, VehicleConfigVVP.BRADLEY_ENERGY_COST.get(), 0.55, 0.5, 1.9, 0.8, 0.21f, -0.16f, 0.0024f, 0.0024f, 0.1f);
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.BMP_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Mth.abs(entityData.get(POWER)) * 8f;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        // From Immersive_Aircraft
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Matrix4f transform = getTurretTransform(1);
        Matrix4f transformV = getVehicleTransform(1);

        int i = this.getSeatIndex(passenger);

        Vector4f worldPosition;
        if (i == 0) {
            worldPosition = transformPosition(transform, 0.0f, 0.0f, 0.0f);
        } else {
            worldPosition = transformPosition(transformV, 0, 1, 0);
        }
        passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
        callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);

        copyEntityData(passenger);
    }

    public void copyEntityData(Entity entity) {
        if (entity == getNthEntity(0)) {
            entity.setYBodyRot(getBarrelYRot(1));
        }
    }

    public int getMaxPassengers() {
        return 7;
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = getTurretTransform(ticks);
        Vector4f worldPosition = transformPosition(transform, 0.3f, 0.75f, 0.56f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = getTurretTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, 0.0234375f, 0.33795f, 0.825f);

        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);

        float a = getTurretYaw(ticks);

        float r = (Mth.abs(a) - 90f) / 90f;

        float r2;

        if (Mth.abs(a) <= 90f) {
            r2 = a / 90f;
        } else {
            if (a < 0) {
                r2 = -(180f + a) / 90f;
            } else {
                r2 = (180f - a) / 90f;
            }
        }

        float x = Mth.lerp(ticks, turretXRotO, getTurretXRot());
        float xV = Mth.lerp(ticks, xRotO, getXRot());
        float z = Mth.lerp(ticks, prevRoll, getRoll());

        transformT.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformT;
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = getTurretTransform(pPartialTicks);
        Vector4f rootPosition = transformPosition(transform, 0, 0, 0);
        Vector4f targetPosition = transformPosition(transform, 0, 0, 1);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, -0.219f, 3.094f, -0.625f);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }

    protected void clampRotation(Entity entity) {
        float a = getTurretYaw(1);
        float r = (Mth.abs(a) - 90f) / 90f;

        float r2;

        if (Mth.abs(a) <= 90f) {
            r2 = a / 90f;
        } else {
            if (a < 0) {
                r2 = -(180f + a) / 90f;
            } else {
                r2 = (180f - a) / 90f;
            }
        }

        float min = -32.5f - r * getXRot() - r2 * getRoll();
        float max = 15f - r * getXRot() - r2 * getRoll();

        float f = Mth.wrapDegrees(entity.getXRot());
        float f1 = Mth.clamp(f, min, max);
        entity.xRotO += f1 - f;
        entity.setXRot(entity.getXRot() + f1 - f);

        entity.setYBodyRot(getBarrelYRot(1));
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        this.clampRotation(entity);
    }

    private PlayState firePredicate(AnimationState<BradleyUkrEntity> event) {
        if (this.entityData.get(FIRE_ANIM) > 1 && getWeaponIndex(0) == 0) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav.fire"));
        }

        if (this.entityData.get(FIRE_ANIM) > 0 && getWeaponIndex(0) == 1) {
            return event.setAndContinue(RawAnimation.begin().thenPlay("animation.lav.fire2"));
        }

        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.lav.idle"));
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
    public int mainGunRpm(Player player) {
        if (getWeaponIndex(0) == 0) {
            return 235;
        } else if (getWeaponIndex(0) == 1) {
            return 700;
        }
        return 235;
    }

    @Override
    public boolean canShoot(Player player) {
        if (getWeaponIndex(0) == 0) {
            return (this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFire;
        } else if (getWeaponIndex(0) == 1) {
            return (this.entityData.get(AMMO) > 0 || InventoryTool.hasCreativeAmmoBox(player)) && !cannotFireCoax;
        } else if (getWeaponIndex(0) == 2) {
            return (this.entityData.get(LOADED_MISSILE) > 0) && this.entityData.get(MISSILE_FIRE_COOLDOWN) <= 0;
        }
        return false;
    }

    @Override
    public int getAmmoCount(Player player) {
        return this.entityData.get(AMMO);
    }

    @Override
    public boolean banHand(Player player) {
        return true;
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return true;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(Player player) {
        if (getWeaponIndex(0) == 0) {
            return entityData.get(HEAT);
        } else if (getWeaponIndex(0) == 1) {
            return entityData.get(COAX_HEAT);
        }
        return 0;
    }

    @Override
    public ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/bradley__icon.png");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFirstPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderFirstPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);

        if (this.getWeaponIndex(0) == 0) {
            double heat = 1 - this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("M242 25MM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat = 1 - this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM M240C " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat = 1 - this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("BGM-71 TOW " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), screenWidth / 2 - 33, screenHeight - 65, Mth.hsvToRgb((float) heat / 3.745318352059925F, 1.0F, 1.0F), false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderThirdPersonOverlay(GuiGraphics guiGraphics, Font font, Player player, int screenWidth, int screenHeight, float scale) {
        super.renderThirdPersonOverlay(guiGraphics, font, player, screenWidth, screenHeight, scale);

        if (this.getWeaponIndex(0) == 0) {
            double heat = this.getEntityData().get(HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("M242 25MM " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 1) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("7.62MM M240C " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        } else if (this.getWeaponIndex(0) == 2) {
            double heat2 = this.getEntityData().get(COAX_HEAT) / 100.0F;
            guiGraphics.drawString(font, Component.literal("BGM-71 TOW " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : this.getAmmoCount(player))), 30, -9, Mth.hsvToRgb(0F, (float) heat2, 1.0F), false);
        }
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.23 : 0.3;
    }

    @Override
    public boolean isEnclosed(int index) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @Nullable Vec2 getCameraRotation(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                return new Vec2((float) -getYRotFromVector(this.getBarrelVec(partialTicks)), (float) -getXRotFromVector(this.getBarrelVec(partialTicks)));
            } else {
                return new Vec2(Mth.lerp(partialTicks, player.yHeadRotO, player.getYHeadRot()), Mth.lerp(partialTicks, player.xRotO, player.getXRot()));
            }
        }
        return super.getCameraRotation(partialTicks, player, false, false);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public Vec3 getCameraPosition(float partialTicks, Player player, boolean zoom, boolean isFirstPerson) {
        if (zoom || isFirstPerson) {
            if (this.getSeatIndex(player) == 0) {
                if (zoom) {
                    return new Vec3(this.driverZoomPos(partialTicks).x, Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), this.driverZoomPos(partialTicks).z);
                } else {
                    return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()), Mth.lerp(partialTicks, player.yo + player.getEyeHeight(), player.getEyeY()), Mth.lerp(partialTicks, player.zo, player.getZ()));
                }
            } else {
                return new Vec3(Mth.lerp(partialTicks, player.xo, player.getX()) - 6 * player.getViewVector(partialTicks).x,
                        Mth.lerp(partialTicks, player.yo + player.getEyeHeight() + 1, player.getEyeY() + 1) - 6 * player.getViewVector(partialTicks).y,
                        Mth.lerp(partialTicks, player.zo, player.getZ()) - 6 * player.getViewVector(partialTicks).z);
            }
        }
        return super.getCameraPosition(partialTicks, player, false, false);
    }

    @Override
    public @Nullable ResourceLocation getVehicleItemIcon() {
        return Mod.loc("textures/gui/vehicle/type/land.png");
    }

    public List<OBB> getOBBs() {
        if (this.entityData.get(HAS_MANGAL)) {  // Если мангал "появился", добавляем obbMangal в список
            return List.of(this.obb1, this.obb2, this.obb3, this.obb4, this.obbTurret, this.obbMangal);
        } else {
            return List.of(this.obb1, this.obb2, this.obb3, this.obb4, this.obbTurret);  // Оригинальный список без изменений
        }
    }

    // сейчас в дс скину где это возможно есть
    public void updateOBB() {
        Matrix4f transform = getVehicleTransform(1);

        Vector4f worldPosition1 = transformPosition(transform, 0.0f, 1.9375f, -0.40625f);
        this.obb1.center().set(new Vector3f(worldPosition1.x, worldPosition1.y, worldPosition1.z));
        this.obb1.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition2 = transformPosition(transform, 0.0f, 1.9375f, 3.375f);
        this.obb2.center().set(new Vector3f(worldPosition2.x, worldPosition2.y, worldPosition2.z));
        this.obb2.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition3 = transformPosition(transform, 1.625f, 0.6875f, -0.21875f);
        this.obb3.center().set(new Vector3f(worldPosition3.x, worldPosition3.y, worldPosition3.z));
        this.obb3.setRotation(VectorTool.combineRotations(1, this));

        Vector4f worldPosition4 = transformPosition(transform, -1.625f, 0.6875f, -0.21875f);
        this.obb4.center().set(new Vector3f(worldPosition4.x, worldPosition4.y, worldPosition4.z));
        this.obb4.setRotation(VectorTool.combineRotations(1, this));

        Matrix4f transformT = getTurretTransform(1);
        Vector4f worldPositionT = transformPosition(transformT, 0.0f, 0.0f, 0.0f);
        this.obbTurret.center().set(new Vector3f(worldPositionT.x, worldPositionT.y, worldPositionT.z));
        this.obbTurret.setRotation(VectorTool.combineRotationsTurret(1, this));

        // а тут чисто тоже самое ток это уже позиция тип
        if (this.entityData.get(HAS_MANGAL)) {
            Vector4f worldPositionMangal = transformPosition(transformT, 0.2f, 1.2f, -0.2f);  // Примерная позиция мангала (относительно турели; подкорректируй x/y/z)
            this.obbMangal.center().set(new Vector3f(worldPositionMangal.x, worldPositionMangal.y, worldPositionMangal.z));
            this.obbMangal.setRotation(VectorTool.combineRotationsTurret(1, this));  // Ротация как у турели
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Загрузка модулей (отдельные if для каждого, как раньше)
        if (stack.is(tech.vvp.vvp.init.ModItems.MANGAL_TURRET.get()) && !this.entityData.get(HAS_MANGAL)) {
            return loadModule(player, stack, HAS_MANGAL, tech.vvp.vvp.init.ModItems.MANGAL_TURRET.get());  // Универсальная функция (см. ниже)
        }
        if (stack.is(tech.vvp.vvp.init.ModItems.SETKA_TURRET.get()) && !this.entityData.get(HAS_FOLIAGE)) {
            return loadModule(player, stack, HAS_FOLIAGE, tech.vvp.vvp.init.ModItems.SETKA_TURRET.get());
        }
        if (stack.is(tech.vvp.vvp.init.ModItems.SETKA_BODY.get()) && !this.entityData.get(HAS_FOLIAGE_BODY)) {
            return loadModule(player, stack, HAS_FOLIAGE_BODY, tech.vvp.vvp.init.ModItems.SETKA_BODY.get());
        }
        if (stack.is(tech.vvp.vvp.init.ModItems.KOROBKI.get()) && !this.entityData.get(KOROBKI)) {
            return loadModule(player, stack, KOROBKI, tech.vvp.vvp.init.ModItems.KOROBKI.get());
        }

        // Универсальное удаление с ключом (один if для всех флагов)
        if (stack.is(tech.vvp.vvp.init.ModItems.WRENCH.get())) {
            // Проверяем флаги по порядку (можно сделать цикл для всех)
            if (this.entityData.get(HAS_MANGAL)) {
                return removeModule(player, HAS_MANGAL, tech.vvp.vvp.init.ModItems.MANGAL_TURRET.get());
            } else if (this.entityData.get(HAS_FOLIAGE)) {
                return removeModule(player, HAS_FOLIAGE, tech.vvp.vvp.init.ModItems.SETKA_TURRET.get());
            } else if (this.entityData.get(HAS_FOLIAGE_BODY)) {
                return removeModule(player, HAS_FOLIAGE_BODY, tech.vvp.vvp.init.ModItems.SETKA_BODY.get());
            } else if (this.entityData.get(KOROBKI)) {
                return removeModule(player, KOROBKI, tech.vvp.vvp.init.ModItems.KOROBKI.get());
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

    public float rotateYOffset() {
        return 3.5f;
    }
}
