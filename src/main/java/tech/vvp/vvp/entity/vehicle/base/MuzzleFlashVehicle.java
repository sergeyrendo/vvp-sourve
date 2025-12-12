package tech.vvp.vvp.entity.vehicle.base;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.vehicle.CamoVehicleBase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Базовый класс для вехиклов с muzzle flash эффектом
 * Поддерживает камуфляж и автоматический muzzle flash при стрельбе
 */
public abstract class MuzzleFlashVehicle extends CamoVehicleBase {

    /**
     * Размеры muzzle flash эффектов
     */
    public enum MuzzleFlashSize {
        SMALL,    // Для пулемётов, малокалиберных пушек
        MEDIUM,   // Для средних пушек (20-40mm)
        LARGE     // Для больших пушек (100mm+)
    }

    // Карта: индекс оружия -> размер muzzle flash
    private final Map<Integer, MuzzleFlashSize> muzzleFlashConfig = new HashMap<>();

    public MuzzleFlashVehicle(EntityType<? extends MuzzleFlashVehicle> type, Level world) {
        super(type, world);
    }

    /**
     * Настройка muzzle flash для оружия
     * Вызывать в конструкторе дочернего класса
     * 
     * @param weaponIndex индекс оружия (0, 1, 2...)
     * @param size размер эффекта (SMALL, MEDIUM, LARGE)
     */
    protected void setMuzzleFlash(int weaponIndex, MuzzleFlashSize size) {
        muzzleFlashConfig.put(weaponIndex, size);
    }

    @Override
    public void vehicleShoot(@Nullable LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        super.vehicleShoot(living, uuid, targetPos);
        
        if (living == null || !(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        int seatIndex = getSeatIndex(living);
        int weaponIndex = getSelectedWeapon(seatIndex);
        
        // Проверяем есть ли настройка muzzle flash для этого оружия
        MuzzleFlashSize size = muzzleFlashConfig.get(weaponIndex);
        if (size == null) {
            return;
        }
        
        // Получаем позицию и направление ствола
        Vec3 barrelVector = getBarrelVector(1);
        Vec3 shootPos = getShootPos(living, 1);
        
        // Спавним частицы в зависимости от размера
        switch (size) {
            case SMALL:
                spawnSmallMuzzleFlash(barrelVector, shootPos, serverLevel);
                break;
            case MEDIUM:
                spawnMediumMuzzleFlash(barrelVector, shootPos, serverLevel);
                break;
            case LARGE:
                spawnLargeMuzzleFlash(barrelVector, shootPos, serverLevel);
                break;
        }
    }

    /**
     * Маленький muzzle flash для пулемётов - яркая вспышка со светом и минимальным дымом
     */
    private void spawnSmallMuzzleFlash(Vec3 direction, Vec3 position, ServerLevel level) {
        // Вспышка
        tech.vvp.vvp.client.particle.MuzzleFlashParticleOption flash = 
            new tech.vvp.vvp.client.particle.MuzzleFlashParticleOption(0.15f, 3);
        level.sendParticles(flash, position.x, position.y, position.z, 1, 0, 0, 0, 0);
        
        // Дым - очень маленький, почти незаметный
        spawnMuzzleSmoke(direction, position, level, 0.2f, 20);
        
        // Свет
        createTemporaryLight(position, level, 10, 3);
    }

    /**
     * Средний muzzle flash для пушек 20-40mm - яркая вспышка со светом и дымом
     */
    private void spawnMediumMuzzleFlash(Vec3 direction, Vec3 position, ServerLevel level) {
        // Вспышка
        tech.vvp.vvp.client.particle.MuzzleFlashParticleOption flash = 
            new tech.vvp.vvp.client.particle.MuzzleFlashParticleOption(0.35f, 4);
        level.sendParticles(flash, position.x, position.y, position.z, 1, 0, 0, 0, 0);
        
        // Дым
        spawnMuzzleSmoke(direction, position, level, 0.6f, 35);
        
        // Свет
        createTemporaryLight(position, level, 12, 4);
    }

    /**
     * Большой muzzle flash для пушек 100mm+ - яркая вспышка со светом и дымом
     */
    private void spawnLargeMuzzleFlash(Vec3 direction, Vec3 position, ServerLevel level) {
        // Вспышка
        tech.vvp.vvp.client.particle.MuzzleFlashParticleOption flash = 
            new tech.vvp.vvp.client.particle.MuzzleFlashParticleOption(0.6f, 5);
        level.sendParticles(flash, position.x, position.y, position.z, 1, 0, 0, 0, 0);
        
        // Дым
        spawnMuzzleSmoke(direction, position, level, 0.8f, 40);
        
        // Свет
        createTemporaryLight(position, level, 15, 5);
    }

    /**
     * Спавнит дым после выстрела - дым разлетается на 1-2 блока влево-вправо
     */
    private void spawnMuzzleSmoke(Vec3 direction, Vec3 position, ServerLevel level, float scale, int lifetime) {
        // Нормализуем направление
        Vec3 forward = direction.normalize();
        
        // Вычисляем вектор вправо (перпендикулярно направлению)
        Vec3 up = Math.abs(forward.y) < 0.99 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 right = forward.cross(up).normalize();
        
        // Спавним 2-3 частицы дыма
        int count = 2 + level.random.nextInt(2);
        for (int i = 0; i < count; i++) {
            tech.vvp.vvp.client.particle.MuzzleSmokeParticleOption smoke = 
                new tech.vvp.vvp.client.particle.MuzzleSmokeParticleOption(scale, lifetime);
            
            // Случайно влево или вправо на 1-2 блока
            double sideways = (Math.random() - 0.5) * 2.0; // -1 до 1
            double distance = 1.0 + Math.random(); // 1-2 блока
            
            // Скорость чтобы пролететь 1-2 блока за lifetime тиков
            // Скорость = расстояние / время (в секундах)
            double sideSpeed = (distance * 20.0) / lifetime; // 20 тиков = 1 секунда
            
            // Скорость: немного вперёд + сильно влево/вправо
            Vec3 velocity = forward.scale(0.1) // Немного вперёд
                .add(right.scale(sideways * sideSpeed)); // Влево или вправо на 1-2 блока
            
            level.sendParticles(smoke, 
                position.x, 
                position.y, 
                position.z, 
                0, velocity.x, velocity.y, velocity.z, 1.0);
        }
    }

    /**
     * Создаёт временный источник света для эффекта вспышки
     */
    private void createTemporaryLight(Vec3 position, ServerLevel level, int lightLevel, int duration) {
        net.minecraft.core.BlockPos blockPos = new net.minecraft.core.BlockPos(
            (int)Math.floor(position.x), 
            (int)Math.floor(position.y), 
            (int)Math.floor(position.z)
        );
        
        // Проверяем что блок воздух или уже наш свет
        net.minecraft.world.level.block.state.BlockState currentState = level.getBlockState(blockPos);
        if (currentState.isAir() || currentState.is(net.minecraft.world.level.block.Blocks.LIGHT)) {
            // Ставим временный блок света
            level.setBlock(blockPos, net.minecraft.world.level.block.Blocks.LIGHT.defaultBlockState()
                .setValue(net.minecraft.world.level.block.LightBlock.LEVEL, lightLevel), 3);
            
            // Убираем свет через несколько тиков
            com.atsuishio.superbwarfare.Mod.queueServerWork(duration, () -> {
                // Проверяем что это всё ещё наш блок света
                if (level.getBlockState(blockPos).is(net.minecraft.world.level.block.Blocks.LIGHT)) {
                    level.removeBlock(blockPos, false);
                }
            });
        }
    }
}
