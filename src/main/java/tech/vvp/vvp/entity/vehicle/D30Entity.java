package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.List;

public class D30Entity extends GeoVehicleEntity {

    public static final EntityDataAccessor<Float> TARGET_PITCH = SynchedEntityData.defineId(D30Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> TARGET_YAW = SynchedEntityData.defineId(D30Entity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> LOADED = SynchedEntityData.defineId(D30Entity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> SHOOT_ANIM_TIME = SynchedEntityData.defineId(D30Entity.class, EntityDataSerializers.INT);

    // OBB для интерактивных частей
    public OBB pitchController; // вентиль вверх-вниз
    public OBB yawController;   // вентиль влево-вправо
    public OBB body;            // корпус

    private static final float PROJECTILE_VELOCITY = 15.0f;
    private static final float GRAVITY = 0.05f;
    private static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenPlay("animation.d30.shoot");

    public double interactionTick = 1;
    public int cooldown;
    private int lastShootAnimTime = 0;

    public D30Entity(EntityType<D30Entity> type, Level world) {
        super(type, world);
        // OBB для вентилей
        this.pitchController = new OBB(OBB.vec3ToVector3d(this.position()), new Vector3d(0.1, 0.15, 0.1), new Quaterniond(), OBB.Part.INTERACTIVE);
        this.yawController = new OBB(OBB.vec3ToVector3d(this.position()), new Vector3d(0.145, 0.145, 0.15), new Quaterniond(), OBB.Part.INTERACTIVE);
        this.body = new OBB(OBB.vec3ToVector3d(this.position()), new Vector3d(0.8, 0.6, 1.5), new Quaterniond(), OBB.Part.BODY);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "shoot", 0, this::shootPredicate));
    }

    private PlayState shootPredicate(AnimationState<D30Entity> state) {
        int currentTime = entityData.get(SHOOT_ANIM_TIME);
        if (currentTime != lastShootAnimTime && currentTime > 0) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(SHOOT_ANIM);
            lastShootAnimTime = currentTime;
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_PITCH, 0f);
        this.entityData.define(TARGET_YAW, 0f);
        this.entityData.define(LOADED, false);
        this.entityData.define(SHOOT_ANIM_TIME, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("TargetPitch", this.entityData.get(TARGET_PITCH));
        compound.putFloat("TargetYaw", this.entityData.get(TARGET_YAW));
        compound.putBoolean("Loaded", this.entityData.get(LOADED));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(TARGET_PITCH, compound.getFloat("TargetPitch"));
        this.entityData.set(TARGET_YAW, compound.getFloat("TargetYaw"));
        this.entityData.set(LOADED, compound.getBoolean("Loaded"));
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(double x, double y, double z) {
        // Гаубица неподвижна
    }

    @Override
    public void move(net.minecraft.world.entity.MoverType type, Vec3 pos) {
        // Блокируем любое движение
    }

    @Override
    public void setDeltaMovement(Vec3 motion) {
        // Игнорируем изменение скорости
        super.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, @NotNull InteractionHand hand) {
        var stack = player.getMainHandItem();
        var lookingObb = OBB.getLookingObb(player, player.getEntityReach());

        // Пустая рука - работа с вентилями и стрельба
        if (stack.isEmpty()) {
            // Вентиль yaw (влево-вправо)
            if (lookingObb == yawController) {
                interactEvent(OBB.vector3dToVec3(yawController.center()));
                // Shift крутит в обратную сторону
                float delta = (player.isShiftKeyDown() ? -0.04f : 0.04f) * (float) interactionTick;
                entityData.set(TARGET_YAW, Mth.clamp(entityData.get(TARGET_YAW) + delta, -180f, 180f));
                player.swing(InteractionHand.MAIN_HAND);
                showRangeInfo(player);
                return InteractionResult.SUCCESS;
            }

            // Вентиль pitch (вверх-вниз)
            if (lookingObb == pitchController) {
                interactEvent(OBB.vector3dToVec3(pitchController.center()));
                // Shift крутит в обратную сторону
                float delta = (player.isShiftKeyDown() ? 0.04f : -0.04f) * (float) interactionTick;
                entityData.set(TARGET_PITCH, Mth.clamp(entityData.get(TARGET_PITCH) + delta, -70f, 7f));
                player.swing(InteractionHand.MAIN_HAND);
                showRangeInfo(player);
                return InteractionResult.SUCCESS;
            }

            // Shift+ПКМ НЕ на вентилях - стрельба
            if (player.isShiftKeyDown() && lookingObb != yawController && lookingObb != pitchController) {
                if (cooldown == 0 && entityData.get(LOADED)) {
                    shoot(player);
                    player.swing(InteractionHand.MAIN_HAND);
                    return InteractionResult.SUCCESS;
                } else if (!entityData.get(LOADED)) {
                    if (!level().isClientSide) {
                        player.displayClientMessage(
                            Component.literal("Not loaded! Use 122mm shell").withStyle(ChatFormatting.RED), true);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }

        // Загрузка снаряда
        if (stack.is(tech.vvp.vvp.init.ModItems.SHELL_122MM.get())) {
            if (!level().isClientSide) {
                if (entityData.get(LOADED)) {
                    player.displayClientMessage(
                        Component.literal("Already loaded!").withStyle(ChatFormatting.YELLOW), true);
                } else {
                    entityData.set(LOADED, true);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    level().playSound(null, getOnPos(), ModSounds.TYPE_63_RELOAD.get(), SoundSource.PLAYERS, 1.5f, 0.8f);
                    player.displayClientMessage(
                        Component.literal("Shell loaded!").withStyle(ChatFormatting.GREEN), true);
                    cooldown = 10;
                }
            }
            player.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }

        // Shift + кровбар - забрать технику
        if (player.isShiftKeyDown() && stack.is(ModTags.Items.TOOLS_CROWBAR)) {
            retrieve(player);
            player.swing(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void showRangeInfo(Player player) {
        if (!level().isClientSide) {
            float pitch = -entityData.get(TARGET_PITCH);
            float yaw = entityData.get(TARGET_YAW);
            
            double velocityPerSec = PROJECTILE_VELOCITY * 20.0;
            double gravityPerSec = GRAVITY * 400.0;
            double angleRad = Math.toRadians(pitch);
            double range = (velocityPerSec * velocityPerSec * Math.sin(2 * angleRad)) / gravityPerSec;
            
            if (range < 0 || pitch < 0) range = 0;
            
            String loaded = entityData.get(LOADED) ? "§a[LOADED]" : "§c[EMPTY]";
            String rangeText = String.format("%s Range: %.0f m | Pitch: %.1f° | Yaw: %.1f°", loaded, range, pitch, yaw);
            player.displayClientMessage(Component.literal(rangeText).withStyle(ChatFormatting.AQUA), true);
        }
    }

    public void interactEvent(Vec3 vec3) {
        if (level() instanceof ServerLevel serverLevel) {
            interactionTick += 1.5;
            if (cooldown <= 0) {
                cooldown = 4;
                serverLevel.playSound(null, vec3.x, vec3.y, vec3.z, ModSounds.HAND_WHEEL_ROT.get(), SoundSource.PLAYERS, 0.5f, random.nextFloat() * 0.05f + 0.975f);
            }
        }
    }

    public void shoot(Player player) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        
        entityData.set(LOADED, false);
        
        Vec3 shootPos = getShootPos();
        Vec3 barrelDir = getBarrelVector(1.0f);
        
        CannonShellEntity shell = new CannonShellEntity(ModEntities.CANNON_SHELL.get(), serverLevel);
        shell.setPos(shootPos.x, shootPos.y, shootPos.z);
        shell.setOwner(player);
        shell.setDamage(400);
        shell.setExplosionDamage(350);
        shell.setExplosionRadius(14);
        shell.setType(CannonShellEntity.Type.HE);
        
        shell.shoot(barrelDir.x, barrelDir.y, barrelDir.z, PROJECTILE_VELOCITY, 0.5f);
        serverLevel.addFreshEntity(shell);
        
        // Звук выстрела
        serverLevel.playSound(null, shootPos.x, shootPos.y, shootPos.z, 
            ModSounds.RPG_FIRE_3P.get(), SoundSource.PLAYERS, 7.0f, 0.7f + random.nextFloat() * 0.2f);
        
        // Стандартные частицы дульного пламени
        ParticleTool.spawnMediumCannonMuzzleParticles(barrelDir, shootPos, serverLevel, this);
        
        // === НОВЫЕ ЭФФЕКТЫ ===
        
        // 1. Тряска камеры для игроков рядом (радиус 15 блоков)
        Vec3 artilleryPos = this.position();
        serverLevel.players().forEach(p -> {
            if (p.distanceToSqr(artilleryPos) < 225) { // 15^2 = 225
                p.hurt(serverLevel.damageSources().explosion(this, player), 0.0f); // 0 урона, но тряска
            }
        });
        
        // 2. Пыль от земли вокруг гаубицы (отдача)
        for (int i = 0; i < 20; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2.5;
            double offsetZ = (random.nextDouble() - 0.5) * 2.5;
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.POOF,
                artilleryPos.x + offsetX, artilleryPos.y + 0.1, artilleryPos.z + offsetZ,
                1, 0, 0.05, 0, 0.02);
        }
        
        // 3. Дым из ствола (долгий эффект)
        for (int i = 0; i < 15; i++) {
            Vec3 smokeOffset = barrelDir.scale(0.5 + i * 0.3);
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.LARGE_SMOKE,
                shootPos.x + smokeOffset.x, shootPos.y + smokeOffset.y, shootPos.z + smokeOffset.z,
                2, 0.1, 0.1, 0.1, 0.01);
        }
        
        // 4. Кольцо ударной волны (расширяющийся дым)
        for (int angle = 0; angle < 360; angle += 15) {
            double rad = Math.toRadians(angle);
            double ringX = Math.cos(rad) * 1.5;
            double ringZ = Math.sin(rad) * 1.5;
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE,
                shootPos.x + ringX, shootPos.y, shootPos.z + ringZ,
                1, ringX * 0.3, 0, ringZ * 0.3, 0.15);
        }
        
        // 5. Гильза вылетает сзади из казённика
        Matrix4d turretTransform = getTurretTransform(1.0f);
        Vector4d casingPos = transformPosition(turretTransform, -0.8, 0.3, -0.8);
        
        net.minecraft.world.entity.item.ItemEntity casingEntity = new net.minecraft.world.entity.item.ItemEntity(
            serverLevel, casingPos.x, casingPos.y, casingPos.z,
            new ItemStack(tech.vvp.vvp.init.ModItems.SHELL_122MM_CASING.get())
        );
        
        // Вылетает вбок с вращением
        Vec3 ejectDir = getLeftVector(1.0f).add(0, 0.3, 0).normalize();
        casingEntity.setDeltaMovement(ejectDir.scale(0.4 + random.nextDouble() * 0.2));
        casingEntity.setPickUpDelay(20);
        serverLevel.addFreshEntity(casingEntity);
        
        cooldown = 20;
        entityData.set(SHOOT_ANIM_TIME, entityData.get(SHOOT_ANIM_TIME) + 1); // Триггер анимации
        
        player.displayClientMessage(
            Component.literal("FIRE!").withStyle(ChatFormatting.RED, ChatFormatting.BOLD), true);
    }

    public Vec3 getShootPos() {
        Matrix4d barrelTransform = getBarrelTransform(1.0f);
        Vector4d pos = transformPosition(barrelTransform, 0, 0, 2.0);
        return new Vec3(pos.x, pos.y, pos.z);
    }

    public Vec3 getLeftVector(float partialTicks) {
        Matrix4d turretTransform = getTurretTransform(partialTicks);
        Vector4d left = transformPosition(turretTransform, 1, 0, 0);
        Vec3 center = this.position();
        return new Vec3(left.x - center.x, left.y - center.y, left.z - center.z).normalize();
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (cooldown > 0) cooldown--;

        interactionTick *= 0.9;
        if (interactionTick < 1) interactionTick = 1;

        this.refreshDimensions();
    }

    @Override
    public void travel() {
        float diffY = entityData.get(TARGET_YAW) - getTurretYRot();
        this.setTurretYRot(this.getTurretYRot() + 0.2f * diffY);

        float diffX = entityData.get(TARGET_PITCH) - getTurretXRot();
        this.setTurretXRot(Mth.clamp(this.getTurretXRot() + 0.2f * diffX, -70f, 7f));
    }

    private void retrieve(Player player) {
        if (level().isClientSide) return;
        for (var stack : getRetrieveItems()) {
            var copy = stack.copy();
            if (!player.addItem(copy)) player.drop(copy, false);
        }
        if (entityData.get(LOADED)) {
            var shell = new ItemStack(tech.vvp.vvp.init.ModItems.SHELL_122MM.get());
            if (!player.addItem(shell)) player.drop(shell, false);
        }
        discard();
    }

    @Override
    public @NotNull List<ItemStack> getRetrieveItems() {
        var list = new ArrayList<ItemStack>();
        list.add(new ItemStack(tech.vvp.vvp.init.ModItems.D30_ITEM.get()));
        return list;
    }

    @Override
    public @NotNull List<OBB> getOBBs() {
        return List.of(pitchController, yawController, body);
    }

    @Override
    public void updateOBB() {
        Matrix4d turretTransform = getTurretTransform(1);

        // Вентиль yaw (vertelkanekrutoi) - передний, ниже и назад
        Vector4d yawPos = transformPosition(turretTransform, 0.6, -0.29, -0.9);
        this.yawController.center().set(new Vector3f((float) yawPos.x, (float) yawPos.y, (float) yawPos.z));
        this.yawController.rotation().set(VectorTool.combineRotationsTurret(1, this));

        // Вентиль pitch (vertelkakrytai) - привязан к turret, не к barrel
        Vector4d pitchPos = transformPosition(turretTransform, 0.84, -0.02, -0.66);
        this.pitchController.center().set(new Vector3f((float) pitchPos.x, (float) pitchPos.y, (float) pitchPos.z));
        this.pitchController.rotation().set(VectorTool.combineRotationsTurret(1, this));

        // Корпус - ниже чтобы не мешать вентилям
        Matrix4d vehicleTransform = getVehicleTransform(1);
        Vector4d bodyPos = transformPosition(vehicleTransform, 0, 0.2, 0);
        this.body.center().set(new Vector3f((float) bodyPos.x, (float) bodyPos.y, (float) bodyPos.z));
        this.body.rotation().set(VectorTool.combineRotations(1, this));
    }

    @Override
    public boolean banHand(net.minecraft.world.entity.LivingEntity entity) {
        return false;
    }

    @Override
    public boolean canAddPassenger(@NotNull net.minecraft.world.entity.Entity passenger) {
        return false;
    }
}
