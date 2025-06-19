package tech.vvp.vvp.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;

public class VehicleEngineSoundInstance extends AbstractTickableSoundInstance {
    private final ContainerMobileVehicleEntity vehicle; // ← УНИВЕРСАЛЬНЫЙ ТИП!
    private float targetVolume = 0.0f;
    private float currentVolume = 0.0f;
    private float targetPitch = 1.0f;
    private float currentPitch = 1.0f;
    
    // Переменные для плавных переходов
    private boolean isStarting = true;
    private int startupTicks = 0;
    private boolean isStopping = false;
    private int stopTicks = 0;

    public VehicleEngineSoundInstance(ContainerMobileVehicleEntity vehicle, SoundEvent soundEvent) {
        super(soundEvent, SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.vehicle = vehicle;
        this.looping = true;
        this.delay = 0;
        this.volume = 0.0f;
        this.pitch = 0.8f;
        
        // Устанавливаем позицию
        this.x = vehicle.getX();
        this.y = vehicle.getY();
        this.z = vehicle.getZ();
        
        // Инициализируем плавный запуск
        this.isStarting = true;
        this.startupTicks = 0;
        this.currentVolume = 0.0f;
        this.currentPitch = 0.8f;
    }

    @Override
    public void tick() {
        if (vehicle.isRemoved()) {
            this.stop();
            return;
        }

        // Обновляем позицию звука
        this.x = vehicle.getX();
        this.y = vehicle.getY();
        this.z = vehicle.getZ();

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            this.stop();
            return;
        }

        // УНИВЕРСАЛЬНАЯ проверка энергии
        if (vehicle.getEnergy() <= 0) {
            // Плавная остановка при отсутствии энергии
            if (!isStopping) {
                isStopping = true;
                stopTicks = 0;
            }
            
            stopTicks++;
            float stopProgress = Math.min(stopTicks / 60.0f, 1.0f);
            currentVolume *= (1.0f - stopProgress * 0.05f);
            currentPitch *= (1.0f - stopProgress * 0.02f);
            
            this.volume = currentVolume;
            this.pitch = Math.max(currentPitch, 0.4f);
            
            if (currentVolume < 0.01f || stopProgress >= 1.0f) {
                this.stop();
            }
            return;
        }

        // Сброс флага остановки если энергия восстановилась
        isStopping = false;
        stopTicks = 0;

        // Рассчитываем расстояние до игрока
        double distance = player.distanceTo(vehicle);
        
        // УНИВЕРСАЛЬНОЕ получение данных о двигателе
        float enginePower = getVehicleEnginePower();
        float speed = (float) vehicle.getDeltaMovement().horizontalDistance();
        int currentEnergy = vehicle.getEnergy();
        int maxEnergy = getVehicleMaxEnergy();
        
        // Настройки дистанции в зависимости от типа техники
        float maxDistance = getMaxSoundDistance();
        float distanceFactor = Math.max(0.0f, 1.0f - (float)distance / maxDistance);
        
        // Рассчитываем целевую громкость в зависимости от типа техники
        float baseVolume = calculateBaseVolume(enginePower, speed);
        
        // Модификатор на основе уровня энергии
        float energyFactor = (float) currentEnergy / Math.max(maxEnergy, 1);
        if (energyFactor < 0.1f) {
            baseVolume *= 0.7f + 0.3f * energyFactor;
        }
        
        // Дополнительные эффекты
        baseVolume += getEnvironmentVolumeBonus();
        
        // Применяем фактор расстояния
        targetVolume = baseVolume * distanceFactor;
        
        // Рассчитываем целевую высоту тона
        targetPitch = calculateTargetPitch(enginePower, speed);
        
        // ПЛАВНЫЕ ПЕРЕХОДЫ (тот же код что и раньше)
        if (isStarting) {
            startupTicks++;
            float startupProgress = Math.min(startupTicks / 40.0f, 1.0f);
            float startupVolume = targetVolume * startupProgress;
            targetVolume = startupVolume;
            
            if (startupProgress >= 1.0f) {
                isStarting = false;
            }
        }
        
        // Скорость изменения
        float volumeChangeSpeed = isStarting ? 0.015f : 
                                 (Math.abs(enginePower) > 0.1f ? 0.04f : 0.025f);
        float pitchChangeSpeed = isStarting ? 0.008f : 0.012f;
        
        // Применяем плавные изменения
        if (currentVolume < targetVolume) {
            currentVolume = Math.min(targetVolume, currentVolume + volumeChangeSpeed);
        } else if (currentVolume > targetVolume) {
            currentVolume = Math.max(targetVolume, currentVolume - volumeChangeSpeed);
        }
        
        if (currentPitch < targetPitch) {
            currentPitch = Math.min(targetPitch, currentPitch + pitchChangeSpeed);
        } else if (currentPitch > targetPitch) {
            currentPitch = Math.max(targetPitch, currentPitch - pitchChangeSpeed);
        }
        
        // Применяем финальные значения
        this.volume = Mth.clamp(currentVolume, 0.0f, getMaxVolume());
        this.pitch = Mth.clamp(currentPitch, 0.5f, 1.8f);
        
        // Останавливаем звук если громкость слишком мала
        if (this.volume < 0.005f && distance > maxDistance * 0.95f) {
            this.stop();
        }
    }

    // ===== УНИВЕРСАЛЬНЫЕ МЕТОДЫ ДЛЯ РАЗНЫХ ТИПОВ ТЕХНИКИ =====
    
    private float getVehicleEnginePower() {
        // ИСПРАВЛЕНО: Используем рефлексию или создаем интерфейс
        
        // СПОСОБ 1: Попробуем привести к конкретным типам
        if (vehicle instanceof tech.vvp.vvp.entity.vehicle.btr80aEntity) {
            return ((tech.vvp.vvp.entity.vehicle.btr80aEntity) vehicle).getEnginePower();
        }
        
        if (vehicle instanceof tech.vvp.vvp.entity.vehicle.btr80a_1Entity) {
            return ((tech.vvp.vvp.entity.vehicle.btr80a_1Entity) vehicle).getEnginePower();
        }
        
        // СПОСОБ 2: Используем приблизительные вычисления через скорость
        return (float) vehicle.getDeltaMovement().horizontalDistance() * 10.0f; // Приблизительная мощность
    }
    
    
    private int getVehicleMaxEnergy() {
        // Можно добавить в базовый класс или использовать стандартное значение
        return 1000; // Или vehicle.getMaxEnergy() если такой метод есть
    }
    
    private float getMaxSoundDistance() {
        // Разные дистанции для разных типов техники
        String vehicleType = vehicle.getClass().getSimpleName().toLowerCase();
        
        if (vehicleType.contains("tank") || vehicleType.contains("btr") || vehicleType.contains("bmp")) {
            return 120.0f; // Тяжелая техника - дальше слышно
        } else if (vehicleType.contains("helicopter") || vehicleType.contains("aircraft")) {
            return 200.0f; // Авиация - очень далеко слышно
        } else if (vehicleType.contains("humvee") || vehicleType.contains("lav")) {
            return 80.0f;  // Легкая техника - ближе
        }
        
        return 100.0f; // По умолчанию
    }
    
    private float calculateBaseVolume(float enginePower, float speed) {
        String vehicleType = vehicle.getClass().getSimpleName().toLowerCase();
        
        float baseMultiplier = 1.0f;
        float idleVolume = 0.15f;
        
        // Настройки громкости для разных типов
        if (vehicleType.contains("tank")) {
            baseMultiplier = 1.8f;  // Танки очень громкие
            idleVolume = 0.4f;
        } else if (vehicleType.contains("btr") || vehicleType.contains("bmp")) {
            baseMultiplier = 1.5f;  // БТР/БМП громкие
            idleVolume = 0.3f;
        } else if (vehicleType.contains("helicopter")) {
            baseMultiplier = 2.0f;  // Вертолеты очень громкие
            idleVolume = 0.5f;
        } else if (vehicleType.contains("aircraft")) {
            baseMultiplier = 2.5f;  // Самолеты самые громкие
            idleVolume = 0.6f;
        } else if (vehicleType.contains("humvee")) {
            baseMultiplier = 1.0f;  // Легкая техника тише
            idleVolume = 0.15f;
        }
        
        if (Math.abs(enginePower) > 0.01f) {
            return (0.4f + Math.abs(enginePower) * 1.2f + speed * 0.6f) * baseMultiplier;
        } else {
            return idleVolume;
        }
    }
    
    private float getEnvironmentVolumeBonus() {
        float bonus = 0.0f;
        
        if (vehicle.isInWater()) {
            bonus += 0.3f; // В воде громче
        }
        
        // Можно добавить другие эффекты: в пещере, под дождем и т.д.
        
        return bonus;
    }
    
    private float calculateTargetPitch(float enginePower, float speed) {
        String vehicleType = vehicle.getClass().getSimpleName().toLowerCase();
        
        float basePitch = 0.8f;
        float pitchRange = 0.3f;
        
        // Разные диапазоны тона для разных типов
        if (vehicleType.contains("tank")) {
            basePitch = 0.6f;  // Танки - низкий рык
            pitchRange = 0.2f;
        } else if (vehicleType.contains("helicopter")) {
            basePitch = 1.0f;  // Вертолеты - высокий звук
            pitchRange = 0.4f;
        } else if (vehicleType.contains("aircraft")) {
            basePitch = 1.2f;  // Самолеты - очень высокий
            pitchRange = 0.5f;
        }
        
        float targetPitch = basePitch + (Math.abs(enginePower) * pitchRange) + (speed * pitchRange * 0.5f);
        
        // В воде звук ниже
        if (vehicle.isInWater()) {
            targetPitch *= 0.9f;
        }
        
        return targetPitch;
    }
    
    private float getMaxVolume() {
        String vehicleType = vehicle.getClass().getSimpleName().toLowerCase();
        
        if (vehicleType.contains("aircraft")) {
            return 3.5f; // Самолеты очень громкие
        } else if (vehicleType.contains("helicopter")) {
            return 3.0f; // Вертолеты громкие
        } else if (vehicleType.contains("tank")) {
            return 2.5f; // Танки громкие
        }
        
        return 2.0f; // По умолчанию
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public boolean canPlaySound() {
        return !vehicle.isRemoved();
    }
}
