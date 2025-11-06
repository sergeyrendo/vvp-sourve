package tech.vvp.vvp.tools;

import com.atsuishio.superbwarfare.config.server.SeekConfig;
import com.atsuishio.superbwarfare.entity.projectile.SmokeDecoyEntity;
import com.atsuishio.superbwarfare.entity.projectile.SwarmDroneEntity;
import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.VectorTool;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import static com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.LAST_DRIVER_UUID;

public class SeekTool {

    // ===== Helpers: выборка по дистанции / командам =====

    public static List<Entity> getVehicleWithinRange(Player player, Level level, double range) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> e.position().distanceTo(player.getEyePosition()) <= range
                        && e instanceof MobileVehicleEntity)
                .toList();
    }

    public static List<Entity> getEntityWithinRange(Player player, Level level, double range) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> e.position().distanceTo(player.getEyePosition()) <= range)
                .toList();
    }

    public static List<Entity> getEntityWithinRange(Entity entity, Level level, double range) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> e.position().distanceTo(entity.getEyePosition()) <= range)
                .toList();
    }

    public static List<Entity> getTeammate(Player player, Level level) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> friendlyToPlayer(player, e))
                .toList();
    }

    public static boolean friendlyToPlayer(Entity e, Entity entity) {
        if (teamFilter(e, entity)) return true;
        if (entity instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() != null && teamFilter(e, ownableEntity.getOwner()))
            return true;
        if (e instanceof Player player && teammateDrone(entity, player)) return true;

        List<Entity> entities = entity.getPassengers();
        for (var passenger : entities) {
            if (teamFilter(e, passenger)) {
                return true;
            }
        }

        if (entity instanceof VehicleEntity vehicle) {
            Entity lastDriver = EntityFindUtil.findEntity(vehicle.level(), vehicle.getEntityData().get(LAST_DRIVER_UUID));
            return lastDriver != null && teamFilter(e, lastDriver);
        }

        return false;
    }

    public static boolean teamFilter(Entity e, Entity entity) {
        if (e == null) return false;
        if (entity == null) return false;
        return e == entity || (entity.getTeam() != null && !entity.getTeam().getName().equals("TDM") && entity.getTeam() == e.getTeam());
    }

    public static boolean teammateDrone(Entity e, Player player) {
        ItemStack stack = player.getMainHandItem();
        DroneEntity drone2 = null;
        if (stack.is(ModItems.MONITOR.get()) && stack.getOrCreateTag().getBoolean("Using") && stack.getOrCreateTag().getBoolean("Linked")) {
            drone2 = EntityFindUtil.findDrone(player.level(), stack.getOrCreateTag().getString("LinkedDrone"));
        }

        return e instanceof DroneEntity drone
                && drone != drone2
                && drone.getController() != null
                && teamFilter(e, drone.getController());
    }

    // ===== Базовые «seek» по взгляду сущности =====

    public static Entity seekEntity(Entity entity, Level level, double seekRange, double seekAngle) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e.distanceTo(entity) <= seekRange && calculateAngle(e, entity) < seekAngle
                            && e != entity
                            && baseFilter(e)
                            && smokeFilter(e)
                            && e.getVehicle() == null
                    ) {
                        return level.clip(new ClipContext(entity.getEyePosition(), e.getEyePosition(),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
                    }
                    return false;
                }).min(Comparator.comparingDouble(e -> calculateAngle(e, entity))).orElse(null);
    }

    public static Entity seekCustomSizeEntity(Entity entity, Level level, double seekRange, double seekAngle, double size, boolean checkOnGround) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e.distanceTo(entity) <= seekRange && calculateAngle(e, entity) < seekAngle
                            && e != entity
                            && baseFilter(e)
                            && (!checkOnGround || isOnGround(e, 10))
                            && e.getBoundingBox().getSize() >= size
                            && smokeFilter(e)
                            && e.getVehicle() == null
                    ) {
                        return level.clip(new ClipContext(entity.getEyePosition(), e.getEyePosition(),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
                    }
                    return false;
                }).min(Comparator.comparingDouble(e -> calculateAngle(e, entity))).orElse(null);
    }

    public static Entity seekLivingEntity(Entity entity, Level level, double seekRange, double seekAngle) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e.distanceTo(entity) <= seekRange && calculateAngle(e, entity) < seekAngle
                            && e != entity
                            && baseFilter(e)
                            && smokeFilter(e)
                            && e.getVehicle() == null
                            && !(e instanceof SwarmDroneEntity swarmDrone && swarmDrone.getOwner() != entity)
                            && !friendlyToPlayer(entity, e)) {
                        return level.clip(new ClipContext(entity.getEyePosition(), e.getEyePosition(),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
                    }
                    return false;
                }).min(Comparator.comparingDouble(e -> calculateAngle(e, entity))).orElse(null);
    }

    public static List<Entity> seekLivingEntities(Entity entity, Level level, double seekRange, double seekAngle) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e.distanceTo(entity) <= seekRange && calculateAngle(e, entity) < seekAngle
                            && e != entity
                            && baseFilter(e)
                            && smokeFilter(e)
                            && e.getVehicle() == null
                            && !friendlyToPlayer(entity, e)) {
                        return level.clip(new ClipContext(entity.getEyePosition(), e.getEyePosition(),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
                    }
                    return false;
                }).toList();
    }

    public static List<Entity> seekCustomSizeEntities(Entity entity, Level level, double seekRange, double seekAngle, double size, boolean checkOnGround) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e.distanceTo(entity) <= seekRange && calculateAngle(e, entity) < seekAngle
                            && e != entity
                            && e.getBoundingBox().getSize() >= size
                            && baseFilter(e)
                            && (!checkOnGround || isOnGround(e, 10))
                            && smokeFilter(e)
                            && e.getVehicle() == null
                            && !friendlyToPlayer(entity, e)) {
                        return level.clip(new ClipContext(entity.getEyePosition(), e.getEyePosition(),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() != HitResult.Type.BLOCK;
                    }
                    return false;
                }).toList();
    }

    // ===== Поиск целей для техники с учётом ствола/турели =====

    /**
     * Ближайшая по углу цель относительно направления ствола техники.
     */
    public static Entity seekFromGunNearest(VehicleEntity vehicle, Level level,
                                            double seekRange, double seekAngle, double minSize, boolean requireLoS) {
        return seekFromGun(vehicle, level, seekRange, seekAngle, minSize, requireLoS)
                .stream()
                .min(Comparator.comparingDouble(e -> calculateAngleVehicle(e, vehicle)))
                .orElse(null);
    }

    /**
     * Список целей в конусе относительно направления ствола техники.
     */
    public static List<Entity> seekFromGun(VehicleEntity vehicle, Level level,
                                           double seekRange, double seekAngle, double minSize, boolean requireLoS) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e == vehicle) return false;
                    if (e.distanceTo(vehicle) > seekRange) return false;
                    if (calculateAngleVehicle(e, vehicle) >= seekAngle) return false;
                    if (!baseFilter(e)) return false;
                    if (e.getBoundingBox().getSize() < minSize) return false;
                    if (!smokeFilter(e)) return false;
                    if (e.getVehicle() != null) return false;
                    if (friendlyToPlayer(vehicle, e)) return false;

                    if (!requireLoS) return true;

                    // ВАЖНО: LoS от глаза техники к глазам цели
                    return level.clip(new ClipContext(
                            vehicle.getNewEyePos(1), e.getEyePosition(),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, vehicle)
                    ).getType() != HitResult.Type.BLOCK;
                })
                .toList();
    }

    // ===== Поиск для Vehicle (исправлен LoS) =====

    public static Entity vehicleSeekEntity(VehicleEntity vehicle, Level level, double seekRange, double seekAngle) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> {
                    if (e.distanceTo(vehicle) <= seekRange && calculateAngleVehicle(e, vehicle) < seekAngle
                            && e != vehicle
                            && baseFilter(e)
                            && smokeFilter(e)
                            && e.getVehicle() == null
                            && !friendlyToPlayer(vehicle, e)) {
                        // FIX: раньше старт и конец луча совпадали → LoS всегда «true»
                        return level.clip(new ClipContext(
                                vehicle.getNewEyePos(1), e.getEyePosition(),
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, vehicle)
                        ).getType() != HitResult.Type.BLOCK;
                    }
                    return false;
                }).min(Comparator.comparingDouble(e -> calculateAngleVehicle(e, vehicle))).orElse(null);
    }

    // ===== Версии «сквозь стену» =====

    public static List<Entity> seekLivingEntitiesThroughWall(Entity entity, Level level, double seekRange, double seekAngle) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> e.distanceTo(entity) <= seekRange && calculateAngle(e, entity) < seekAngle
                        && e != entity
                        && baseFilter(e)
                        && e.getVehicle() == null
                        && !friendlyToPlayer(entity, e)).toList();
    }

    public static Entity seekEntityThroughWall(Entity entity, Level level, double seekRange, double seekAngle) {
        return seekLivingEntitiesThroughWall(entity, level, seekRange, seekAngle)
                .stream().min(Comparator.comparingDouble(e -> calculateAngle(e, entity))).orElse(null);
    }

    // ===== Прочие утилиты =====

    public static List<Entity> getEntitiesWithinRange(BlockPos pos, Level level, double range) {
        return StreamSupport.stream(EntityFindUtil.getEntities(level).getAll().spliterator(), false)
                .filter(e -> e.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) <= range * range
                        && baseFilter(e) && smokeFilter(e) && !e.getType().is(ModTags.EntityTypes.DECOY))
                .toList();
    }

    private static double calculateAngle(Entity entityA, Entity entityB) {
        Vec3 start = new Vec3(entityA.getX() - entityB.getX(), entityA.getY() - entityB.getY(), entityA.getZ() - entityB.getZ());
        Vec3 end = entityB.getLookAngle();
        return VectorTool.calculateAngle(start, end);
    }

    private static double calculateAngleVehicle(Entity entityA, VehicleEntity entityB) {
        Vec3 entityBEyePos = entityB.getNewEyePos(1);
        Vec3 start = new Vec3(entityA.getX() - entityBEyePos.x, entityA.getY() - entityBEyePos.y, entityA.getZ() - entityBEyePos.z);
        Vec3 end = entityB.getBarrelVector(1);
        return VectorTool.calculateAngle(start, end);
    }

    public static boolean baseFilter(Entity entity) {
        return entity.isAlive()
                && !(entity instanceof HangingEntity || (entity instanceof Projectile && !entity.getType().is(ModTags.EntityTypes.DESTROYABLE_PROJECTILE)))
                && !(entity instanceof Player player && player.isSpectator())
                && !isInBlackList(entity);
    }

    public static boolean isOnGround(Entity entity) {
        return isOnGround(entity, 0);
    }

    /**
     * Проверка, что сущность находится в пределах height блоков от земли/воды.
     */
    public static boolean isOnGround(Entity entity, double height) {
        Level level = entity.level();

        double y = entity.getY();
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        if (y < minY || y > maxY) {
            return false;
        }

        boolean[] onGround = {false};
        AABB aabb = entity.getBoundingBox().expandTowards(0, -height, 0);
        BlockPos.betweenClosedStream(aabb).forEach((pos) -> {
            if (pos.getY() < minY || pos.getY() > maxY) return;

            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                onGround[0] = true;
            }
        });
        return entity.onGround() || entity.isInWater() || onGround[0];
    }

    public static boolean smokeFilter(Entity pEntity) {
        var Box = pEntity.getBoundingBox().inflate(8);

        var entities = pEntity.level().getEntities(EntityTypeTest.forClass(Entity.class), Box,
                        entity -> entity instanceof SmokeDecoyEntity)
                .stream().toList();

        boolean result = true;

        for (var e : entities) {
            if (e != null) {
                result = false;
                break;
            }
        }

        return result;
    }

    public static boolean isInBlackList(Entity entity) {
        var type = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (type == null) return false;
        return SeekConfig.SEEK_BLACKLIST.get().contains(type.toString());
    }

    // ===== Упреждение без гравитации (простая баллистика) =====

    /**
     * Расчет точки упреждения без учета гравитации.
     * @param shooterPos позиция ствола
     * @param bulletSpeed скорость пули/снаряда (блоков/тик)
     * @param targetPos текущая позиция цели
     * @param targetVel скорость цели (Vec3 getDeltaMovement)
     * @return точка прицеливания
     */
    public static Vec3 leadNoGravity(Vec3 shooterPos, double bulletSpeed, Vec3 targetPos, Vec3 targetVel) {
        Vec3 r = targetPos.subtract(shooterPos);
        double a = targetVel.lengthSqr() - bulletSpeed * bulletSpeed;
        double b = 2.0 * r.dot(targetVel);
        double c = r.lengthSqr();

        double t;
        if (Math.abs(a) < 1e-6) {
            // почти прямой случай (скорость цели близка к скорости пули)
            t = -c / Math.max(b, 1e-6);
        } else {
            double disc = b * b - 4 * a * c;
            if (disc < 0) return targetPos; // нет решения — цель слишком быстрая/далека
            double sqrt = Math.sqrt(disc);
            double t1 = (-b - sqrt) / (2 * a);
            double t2 = (-b + sqrt) / (2 * a);
            t = (t1 > 0 && t2 > 0) ? Math.min(t1, t2) : Math.max(t1, t2);
        }
        if (t < 0) t = 0;
        return targetPos.add(targetVel.scale(t));
    }
}