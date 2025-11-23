package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.LandArmorEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ThirdPersonCameraPosition;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.CannonShellWeapon;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.message.receive.ShakeClientMessage;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PlayMessages;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.config.server.VehicleConfigVVP;

import javax.annotation.ParametersAreNonnullByDefault;

public class C3MEntity extends ContainerMobileVehicleEntity implements GeoEntity, LandArmorEntity, WeaponVehicleEntity {

    public static final EntityDataAccessor<String> LOADED_SHELL = SynchedEntityData.defineId(C3MEntity.class, EntityDataSerializers.STRING);
    public static final EntityDataAccessor<Integer> SELECTED_AMMO_TYPE = SynchedEntityData.defineId(C3MEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> GUN_FIRE_TIME = SynchedEntityData.defineId(C3MEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> CAMOUFLAGE_TYPE = SynchedEntityData.defineId(C3MEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> FIRING_MODE = SynchedEntityData.defineId(C3MEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Режимы работы
    public enum OperationMode {
        DRIVING,  // Режим движения - можно ездить, башня заблокирована
        FIRING    // Режим стрельбы - техника остановлена, можно наводить башню
    }

    // Кулдаун перезарядки
    public int reloadCoolDown = 0;
    private static final int RELOAD_TIME = 200; // 10 секунд (200 тиков)

    // Отслеживание клика (не зажатия)
    private boolean wasFirePressed = false;

    // Офсеты для башни и ствола из модели 2c3m.geo.json
    // base pivot: [0.17662, 21.80764, -3.23534] относительно root
    // cannon pivot: [0.10092, 41.70299, 22.80422] относительно base
    // Turret offset = cannon pivot относительно root = base + cannon
    private static final float TURRET_OFFSET_X = (0.17662f + 0.10092f)/16f;
    private static final float TURRET_OFFSET_Y = (21.80764f + 41.70299f)/16f;
    private static final float TURRET_OFFSET_Z = (-3.23534f + 22.80422f)/16f;

    // barrel pivot: [0.17524, 42.10095, 8.50286] относительно cannon
    // Barrel offset относительно turret (cannon)
    private static final float BARREL_OFFSET_X = 0.17524f/16f;
    private static final float BARREL_OFFSET_Y = 42.10095f/16f;
    private static final float BARREL_OFFSET_Z = 8.50286f/16f;

    public C3MEntity(PlayMessages.SpawnEntity packet, Level world) {
        this(tech.vvp.vvp.init.ModEntities.C3M.get(), world);
    }

    public C3MEntity(EntityType<C3MEntity> type, Level world) {
        super(type, world);
        this.setMaxUpStep(1.5f);
        this.setTurretYRot(0);
        this.setTurretXRot(0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LOADED_SHELL, "null");
        this.entityData.define(SELECTED_AMMO_TYPE, 0);
        this.entityData.define(GUN_FIRE_TIME, 0);
        this.entityData.define(CAMOUFLAGE_TYPE, 0);
        this.entityData.define(FIRING_MODE, false); // false = DRIVING, true = FIRING
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("LoadedShell", this.entityData.get(LOADED_SHELL));
        compound.putInt("SelectedAmmoType", this.entityData.get(SELECTED_AMMO_TYPE));
        compound.putInt("CamouflageType", this.entityData.get(CAMOUFLAGE_TYPE));
        compound.putBoolean("FiringMode", this.entityData.get(FIRING_MODE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(LOADED_SHELL, compound.getString("LoadedShell"));
        this.entityData.set(SELECTED_AMMO_TYPE, compound.getInt("SelectedAmmoType"));
        this.entityData.set(CAMOUFLAGE_TYPE, compound.getInt("CamouflageType"));
        this.entityData.set(FIRING_MODE, compound.getBoolean("FiringMode"));
    }

    // Методы для работы с режимами
    public boolean isFiringMode() {
        return this.entityData.get(FIRING_MODE);
    }

    public void setFiringMode(boolean firing) {
        this.entityData.set(FIRING_MODE, firing);
    }

    public OperationMode getOperationMode() {
        return isFiringMode() ? OperationMode.FIRING : OperationMode.DRIVING;
    }

    public void toggleMode() {
        boolean currentMode = isFiringMode();
        setFiringMode(!currentMode);

        // При переключении в режим FIRING - останавливаем технику
        if (!currentMode) { // Переключаемся в FIRING
            this.setDeltaMovement(Vec3.ZERO);
        }

        // Отправляем сообщение игроку
        if (!this.level().isClientSide && this.getFirstPassenger() instanceof Player player) {
            String mode = isFiringMode() ? "§aFIRING" : "§6DRIVING";
            player.sendSystemMessage(Component.literal("Mode: " + mode));
        }
    }

    @Override
    public VehicleWeapon[][] initWeapons() {
        return new VehicleWeapon[][]{
                new VehicleWeapon[]{
                        // HE снаряд для артиллерии
                        new CannonShellWeapon()
                                .hitDamage(50)
                                .explosionRadius(8.0f)
                                .explosionDamage(80)
                                .fireProbability(0.3F)
                                .fireTime(3)
                                .durability(1)
                                .velocity(35)
                                .gravity(0.15f)
                                .sound(ModSounds.INTO_CANNON.get())
                                .ammo(ModItems.HE_5_INCHES.get())
                                .icon(Mod.loc("textures/screens/vehicle_weapon/he_shell.png"))
                                .sound1p(tech.vvp.vvp.init.ModSounds.ABRAMS_1P.get())
                                .sound3p(tech.vvp.vvp.init.ModSounds.ABRAMS_3P.get())
                                .sound3pFar(tech.vvp.vvp.init.ModSounds.ABRAMS_FAR.get())
                                .sound3pVeryFar(tech.vvp.vvp.init.ModSounds.ABRAMS_VERYFAR.get())
                                .mainGun(true),
                }
        };
    }

    @Override
    public ThirdPersonCameraPosition getThirdPersonCameraPosition(int index) {
        return new ThirdPersonCameraPosition(4.0F + ClientMouseHandler.custom3pDistanceLerp, 1.5F, 0.0F);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(ModSounds.WHEEL_STEP.get(), (float)(this.getDeltaMovement().length() * 0.3), this.random.nextFloat() * 0.15F + 1.05F);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> this.getSourceAngle(source, 0.25F) * damage);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        // Обработка перезарядки
        if (!this.level().isClientSide) {
            handleAmmo();
        }

        // Управление башней в режиме FIRING
        if (isFiringMode() && this.getFirstPassenger() instanceof Player player) {
            // В режиме FIRING башня следует за камерой игрока
            float playerYaw = player.getYHeadRot();
            float playerPitch = player.getXRot();

            // Поворот башни - используем относительный угол к машине
            float relativeYaw = Mth.wrapDegrees(playerYaw - this.getYRot());
            this.setTurretYRot(relativeYaw);

            // Подъем башни с ограничениями для артиллерии
            // playerPitch: отрицательный = вверх, положительный = вниз
            // Ограничиваем от -60 (вверх) до 0 (горизонт) для артиллерии
            this.setTurretXRot(Mth.clamp(playerPitch, -60F, 0F));

            // Сбрасываем MOUSE_SPEED чтобы не накапливалось
            this.entityData.set(MOUSE_SPEED_X, 0f);
            this.entityData.set(MOUSE_SPEED_Y, 0f);
        } else if (!isFiringMode()) {
            // В режиме DRIVING башня заблокирована
            this.setTurretYRot(0F);
            this.setTurretXRot(0F);

            // Сбрасываем MOUSE_SPEED
            this.entityData.set(MOUSE_SPEED_X, 0f);
            this.entityData.set(MOUSE_SPEED_Y, 0f);
        }

        // Сохраняем значения башни
        this.turretYRotO = this.getTurretYRot();
        this.turretXRotO = this.getTurretXRot();

        // Физика движения
        double fluidFloat = 0.052 * this.getSubmergedHeight(this);
        this.setDeltaMovement(this.getDeltaMovement().add(0.0F, fluidFloat, 0.0F));

        if (this.onGround()) {
            float f0 = 0.54F + 0.25F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.05 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f0, 0.99, f0));
        } else if (this.isInWater()) {
            float f1 = 0.74F + 0.09F * Mth.abs(90.0F - (float)calculateAngle(this.getDeltaMovement(), this.getViewVector(1.0F))) / 90.0F;
            this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).normalize().scale(0.04 * this.getDeltaMovement().dot(this.getViewVector(1.0F)))));
            this.setDeltaMovement(this.getDeltaMovement().multiply(f1, 0.85, f1));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.99, 0.99, 0.99));
        }

        // Обработка стрельбы ЛКМ (только по клику, не при зажатии)
        if (this.getFirstPassenger() instanceof Player player) {
            if (fireInputDown && !wasFirePressed && reloadCoolDown == 0) {
                vehicleShoot(player, 0);
            }
            wasFirePressed = fireInputDown;
        }

        if (this.entityData.get(GUN_FIRE_TIME) > 0) {
            this.entityData.set(GUN_FIRE_TIME, this.entityData.get(GUN_FIRE_TIME) - 1);
        }

        this.lowHealthWarning();
        this.terrainCompact(2.7F, 3.61F);
        this.releaseSmokeDecoy(this.getTurretVector(1.0F));
        this.inertiaRotate(1.2F);
        this.refreshDimensions();
    }

    private void handleAmmo() {
        // Уменьшаем кулдаун перезарядки
        if (reloadCoolDown > 0) {
            reloadCoolDown--;
        }

        // Проверяем креативный ящик с патронами
        boolean hasCreativeAmmo = false;
        if (this.getFirstPassenger() instanceof Player player && com.atsuishio.superbwarfare.tools.InventoryTool.hasCreativeAmmoBox(player)) {
            hasCreativeAmmo = true;
        }

        if (hasCreativeAmmo) {
            // Креативный режим - всегда заряжено
            if (this.entityData.get(LOADED_SHELL).equals("null")) {
                this.entityData.set(LOADED_SHELL, String.valueOf(ForgeRegistries.ITEMS.getKey(getWeapon(0).ammo)));
            }
        } else {
            // Считаем патроны в инвентаре контейнера
            int ammoInContainer = countItem(getWeapon(0).ammo);
            String currentShell = this.entityData.get(LOADED_SHELL);

            // Если не заряжено И есть в контейнере И кулдаун закончился - заряжаем
            if (currentShell.equals("null") && ammoInContainer > 0 && reloadCoolDown <= 0) {
                consumeItem(getWeapon(0).ammo, 1);
                this.entityData.set(LOADED_SHELL, String.valueOf(ForgeRegistries.ITEMS.getKey(getWeapon(0).ammo)));

                // Устанавливаем задержку перезарядки 10 секунд
                reloadCoolDown = RELOAD_TIME;

                // Звук и сообщение игроку
                if (this.getFirstPassenger() instanceof Player player) {
                    com.atsuishio.superbwarfare.tools.SoundTool.playLocalSound(player, tech.vvp.vvp.init.ModSounds.M1128_RELOAD.get());
                    player.sendSystemMessage(Component.literal("§aReloading..."));
                }
            }
        }
    }

    @Override
    public float turretYSpeed() {
        return 0f; // Блокируем управление мышью - управляется через камеру
    }

    @Override
    public float turretXSpeed() {
        return 0f; // Блокируем управление мышью - управляется через камеру
    }

    @Override
    public float turretMinPitch() {
        return -60f; // Максимальный угол подъема для артиллерии
    }

    @Override
    public float turretMaxPitch() {
        return 0f; // Горизонт
    }

    @Override
    public boolean canCollideHardBlock() {
        return this.getDeltaMovement().horizontalDistance() > 0.09 || Mth.abs(this.entityData.get(POWER)) > 0.15;
    }

    @Override
    public void vehicleShoot(LivingEntity living, int type) {
        if (!(living instanceof Player player)) return;

        // Проверяем режим
        if (!isFiringMode()) {
            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal("§cCannot fire in DRIVING mode! Switch to FIRING mode first."));
            }
            return;
        }

        // Проверяем наличие заряженного снаряда
        String loadedShell = this.entityData.get(LOADED_SHELL);
        if (loadedShell.equals("null")) {
            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal("§cNo shell loaded! Reloading..."));
            }
            return;
        }

        // Проверяем кулдаун
        if (reloadCoolDown > 0) {
            if (!this.level().isClientSide) {
                player.sendSystemMessage(Component.literal("§cReloading... " + (reloadCoolDown / 20) + "s"));
            }
            return;
        }

        // Стреляем
        var cannonShell = (CannonShellWeapon) getWeapon(0);
        var entityToSpawn = cannonShell.create(living);

        Vec3 shootPos = getTurretShootPos(living, 1.0F);
        entityToSpawn.setPos(shootPos.x, shootPos.y, shootPos.z);
        entityToSpawn.shoot(getBarrelVector(1).x, getBarrelVector(1).y, getBarrelVector(1).z, cannonShell.velocity, 0.02f);
        level().addFreshEntity(entityToSpawn);

        playShootSound3p(living, 0, 8, 16, 32, shootPos);

        this.entityData.set(CANNON_RECOIL_TIME, 60);
        this.entityData.set(LOADED_SHELL, "null");
        this.entityData.set(YAW, getTurretYRot());
        this.entityData.set(FIRE_ANIM, 3);

        reloadCoolDown = RELOAD_TIME;

        // Эффекты выстрела
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.getX() + 5 * getBarrelVector(1).x,
                    this.getY() + 0.1,
                    this.getZ() + 5 * getBarrelVector(1).z,
                    300, 6, 0.02, 6, 0.005);

            double x = shootPos.x + 9 * getBarrelVector(1).x;
            double y = shootPos.y + 9 * getBarrelVector(1).y;
            double z = shootPos.z + 9 * getBarrelVector(1).z;

            server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);
            server.sendParticles(ParticleTypes.CLOUD, x, y, z, 10, 0.4, 0.4, 0.4, 0.0075);

            for (float i = 9.5f; i < 23; i += .5f) {
                server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        shootPos.x + i * getBarrelVector(1).x,
                        shootPos.y + i * getBarrelVector(1).y,
                        shootPos.z + i * getBarrelVector(1).z,
                        Mth.clamp(6 - (int)(i / 2), 1, 5), 0.15, 0.15, 0.15, 0.0025);
            }
        }

        ShakeClientMessage.sendToNearbyPlayers(this, 8, 10, 8, 60);

        if (!this.level().isClientSide) {
            player.sendSystemMessage(Component.literal("§aFired! Reloading..."));
        }
    }

    @Override
    public boolean hasPassengerTurretWeapon() {
        return true;
    }

    @Override
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4f transform = this.getBarrelTransform(pPartialTicks);
        // Стандартное направление ствола - по оси Z вперед
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        Vec3 direction = new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
        return direction.normalize();
    }

    @Override
    public void travel() {
        Entity passenger0 = this.getFirstPassenger();
        if (this.getEnergy() > 0) {
            if (passenger0 == null) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.sprintInputDown = false;
                this.entityData.set(POWER, 0.0F);
            }

            // В режиме FIRING блокируем движение
            if (isFiringMode()) {
                this.leftInputDown = false;
                this.rightInputDown = false;
                this.forwardInputDown = false;
                this.backInputDown = false;
                this.sprintInputDown = false;
                this.entityData.set(POWER, 0.0F);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 1.0, 0.8)); // Плавная остановка
            }

            if (this.forwardInputDown) {
                this.entityData.set(POWER, org.joml.Math.min(this.entityData.get(POWER) + (this.entityData.get(POWER) < 0.0F ? 0.012F : 0.0024F), 0.18F));
            }

            if (this.backInputDown) {
                this.entityData.set(POWER, org.joml.Math.max(this.entityData.get(POWER) - (this.entityData.get(POWER) > 0.0F ? 0.012F : 0.0024F), -0.13F));
            }

            if (this.rightInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) + 0.1F);
            } else if (this.leftInputDown) {
                this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) - 0.1F);
            }

            this.entityData.set(POWER, this.entityData.get(POWER) * (this.upInputDown ? 0.5F : (!this.rightInputDown && !this.leftInputDown ? 0.99F : 0.977F)));
            this.entityData.set(DELTA_ROT, this.entityData.get(DELTA_ROT) * (float) org.joml.Math.max(0.76F - 0.1F * this.getDeltaMovement().horizontalDistance(), 0.3));

            double s0 = this.getDeltaMovement().dot(this.getViewVector(1.0F));
            this.setLeftWheelRot((float)(this.getLeftWheelRot() - 1.25F * s0 - this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5F * this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRightWheelRot((float)(this.getRightWheelRot() - 1.25F * s0 + this.getDeltaMovement().horizontalDistance() * Mth.clamp(1.5F * this.entityData.get(DELTA_ROT), -5.0F, 5.0F)));
            this.setRudderRot(Mth.clamp(this.getRudderRot() - this.entityData.get(DELTA_ROT), -0.8F, 0.8F) * 0.75F);

            this.setYRot((float)(this.getYRot() - Math.max((this.isInWater() && !this.onGround() ? 5 : 10) * this.getDeltaMovement().horizontalDistance(), 0.0F) * this.getRudderRot() * (this.entityData.get(POWER) > 0.0F ? 1 : -1)));

            if (this.isInWater() || this.onGround()) {
                float power = this.entityData.get(POWER) * Mth.clamp(1.0F + (float)(s0 > 0.0F ? 1 : -1) * this.getXRot() / 35.0F, 0.0F, 2.0F);
                this.setDeltaMovement(this.getDeltaMovement().add(this.getViewVector(1.0F).scale((!this.isInWater() && !this.onGround() ? 0.05F : (this.isInWater() && !this.onGround() ? 0.3F : 1.0F)) * power)));
            }
        }
    }

    @Override
    public SoundEvent getEngineSound() {
        return ModSounds.LAV_ENGINE.get();
    }

    @Override
    public float getEngineSoundVolume() {
        return Mth.abs(this.entityData.get(POWER)) * 2.0F;
    }

    @Override
    public void positionRider(@NotNull Entity passenger, @NotNull MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Matrix4f transform = this.getVehicleTransform(1.0F);
            int i = this.getSeatIndex(passenger);
            Vector4f worldPosition;
            if (i == 0) {
                // Позиция водителя/наводчика
                float x = 0.0F;
                float y = 1.5F;
                float z = -1.0F;
                worldPosition = this.transformPosition(transform, x, y, z);
            } else {
                worldPosition = this.transformPosition(transform, 0.0F, 1.0F, 0.0F);
            }
            passenger.setPos(worldPosition.x, worldPosition.y, worldPosition.z);
            callback.accept(passenger, worldPosition.x, worldPosition.y, worldPosition.z);
            this.copyEntityData(passenger);
        }
    }

    public void copyEntityData(Entity entity) {
        if (entity == this.getNthEntity(0)) {
            entity.setYBodyRot(this.getBarrelYRot(1.0F));
        }
    }

    @Override
    public int getMaxPassengers() {
        return 2;
    }

    @Override
    public Vec3 driverZoomPos(float ticks) {
        Matrix4f transform = this.getTurretTransform(ticks);
        Vector4f worldPosition = this.transformPosition(transform, 0.0F, 0.5F, 0.5F);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Vec3 getTurretShootPos(Entity entity, float ticks) {
        Matrix4f transform = getBarrelTransform(ticks);
        // Ствол длинный, нужно сместить вперед по оси Z (конец ствола)
        // Примерно 60-70 блоков вперед от pivot barrel (длина ствола ~60 блоков)
        Vector4f worldPosition = transformPosition(transform, 0.0f, 0.0f, 60.0f/16f);
        return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
    }

    public Matrix4f getBarrelTransform(float ticks) {
        Matrix4f transformT = this.getTurretTransform(ticks);
        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = this.transformPosition(transform, BARREL_OFFSET_X, BARREL_OFFSET_Y, BARREL_OFFSET_Z);
        transformT.translate(worldPosition.x, worldPosition.y, worldPosition.z);

        float a = this.getTurretYaw(ticks);
        float r = (Mth.abs(a) - 90.0F) / 90.0F;
        float r2;
        if (Mth.abs(a) <= 90.0F) {
            r2 = a / 90.0F;
        } else if (a < 0.0F) {
            r2 = -(180.0F + a) / 90.0F;
        } else {
            r2 = (180.0F - a) / 90.0F;
        }

        float x = Mth.lerp(ticks, this.turretXRotO, this.getTurretXRot());
        float xV = Mth.lerp(ticks, this.xRotO, this.getXRot());
        float z = Mth.lerp(ticks, this.prevRoll, this.getRoll());
        transformT.rotate(Axis.XP.rotationDegrees(x + r * xV + r2 * z));
        return transformT;
    }

    public Vec3 getTurretVector(float pPartialTicks) {
        Matrix4f transform = this.getTurretTransform(pPartialTicks);
        Vector4f rootPosition = this.transformPosition(transform, 0.0F, 0.0F, 0.0F);
        Vector4f targetPosition = this.transformPosition(transform, 0.0F, 0.0F, 1.0F);
        return new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).vectorTo(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
    }

    public Matrix4f getTurretTransform(float ticks) {
        Matrix4f transformV = getVehicleTransform(ticks);

        Matrix4f transform = new Matrix4f();
        Vector4f worldPosition = transformPosition(transform, TURRET_OFFSET_X, TURRET_OFFSET_Y, TURRET_OFFSET_Z);

        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate(Axis.YP.rotationDegrees(Mth.lerp(ticks, turretYRotO, getTurretYRot())));
        return transformV;
    }

    protected void clampRotation(Entity entity) {
        // Не делаем ничего - башня управляется только через режим FIRING
    }

    @Override
    public void onPassengerTurned(@NotNull Entity entity) {
        // Не делаем ничего - башня управляется только через режим FIRING
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public int mainGunRpm(LivingEntity living) {
        return 6; // 6 выстрелов в минуту для артиллерии
    }

    @Override
    public int getAmmoCount(LivingEntity living) {
        return this.entityData.get(LOADED_SHELL).equals("null") ? 0 : 1;
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        return !this.entityData.get(LOADED_SHELL).equals("null") && isFiringMode() && !this.cannotFire && reloadCoolDown == 0;
    }

    @Override
    public boolean banHand(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean hidePassenger(Entity entity) {
        return false;
    }

    @Override
    public int zoomFov() {
        return 3;
    }

    @Override
    public int getWeaponHeat(LivingEntity living) {
        return 0; // Артиллерия не перегревается
    }

    @Override
    public net.minecraft.resources.ResourceLocation getVehicleIcon() {
        return VVP.loc("textures/vehicle_icon/2c3m_icon.png");
    }

    @Override
    public boolean hasDecoy() {
        return true;
    }

    @Override
    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        return zoom ? 0.23 : (net.minecraft.client.Minecraft.getInstance().options.getCameraType().isFirstPerson() ? 0.3 : 0.4);
    }

    @Override
    public boolean isEnclosed(int index) {
        return true;
    }

    @Override
    public net.minecraft.resources.ResourceLocation getVehicleItemIcon() {
        return VVP.loc("textures/gui/vehicle/type/land_ru.png");
    }

    // Отключаем звук поворота башни
    @Override
    public void turretTurnSound(float diffX, float diffY, float pitch) {
        // Пустой метод - не воспроизводим звук поворота башни
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(tech.vvp.vvp.init.ModItems.SPRAY.get())) {
            if (!this.level().isClientSide) {  // Только на сервере
                int currentType = this.entityData.get(CAMOUFLAGE_TYPE);
                int maxTypes = 4;  // Количество типов (0, 1, 2, 3)
                int newType = (currentType + 1) % maxTypes;  // Цикл: 0→1→2→3→0
                this.entityData.set(CAMOUFLAGE_TYPE, newType);  // Сохраняем новый тип

                // Звук и эффект
                this.level().playSound(null, this, tech.vvp.vvp.init.ModSounds.SPRAY.get(), this.getSoundSource(), 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1, this.getZ(), 10, 1.0, 1.0, 1.0, 0.1);
                }

                return InteractionResult.CONSUME;  // Consume — прерываем, не даём войти
            } else {
                return InteractionResult.SUCCESS;  // Success на клиенте для отклика
            }
        }

        return super.interact(player, hand);
    }
}

