package tech.vvp.vvp.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;
import tech.vvp.vvp.network.message.PantsirRadarSyncMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Клиентский обработчик данных радара Pantsir
 */
@OnlyIn(Dist.CLIENT)
public class PantsirClientHandler {
    
    // Текущее состояние радара
    public static int radarState = PantsirRadarSyncMessage.STATE_IDLE;
    
    // ID основной цели (-1 если нет)
    public static int targetEntityId = -1;
    
    // Позиция основной цели для отрисовки
    public static double targetX = 0;
    public static double targetY = 0;
    public static double targetZ = 0;
    
    // Скорость цели (блоков/тик)
    public static double targetVelX = 0;
    public static double targetVelY = 0;
    public static double targetVelZ = 0;
    
    // Прогресс захвата (0-100)
    public static int lockProgress = 0;
    
    // Дистанция до цели
    public static double targetDistance = 0;
    
    // Угол вращения радара от сервера (абсолютный)
    public static float radarAngle = 0;
    
    // Угол башни (пассивный радар) - направление куда смотрит пушка
    public static float turretAngle = 0;
    
    // Все обнаруженные цели для отображения на радаре
    public static final List<RadarTarget> allTargets = new ArrayList<>();
    
    // Для плавной интерполяции радара на клиенте
    private static float clientRadarAngle = 0;
    private static float serverRadarAngle = 0;
    private static long lastSyncTime = 0;
    private static long lastUpdateTime = 0;
    
    // Скорость вращения радара (градусов в миллисекунду)
    // 360° за 42.5 тика = 360° за 2.125 сек = ~169.4°/сек = ~0.1694°/мс
    private static final float RADAR_ROTATION_SPEED = 360.0f / 2125.0f;
    
    /**
     * Данные о цели на радаре
     */
    public static class RadarTarget {
        public final int entityId;
        public final double x, y, z;
        
        public RadarTarget(int entityId, double x, double y, double z) {
            this.entityId = entityId;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    /**
     * Данные о ракете на радаре
     */
    public static class MissilePosition {
        public final double x, y, z;
        
        public MissilePosition(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    // Все выпущенные ракеты для отображения на радаре
    public static final List<MissilePosition> missiles = new ArrayList<>();
    
    /**
     * Обработка сообщения синхронизации радара от сервера
     * Фильтрует сообщения - принимает только от панциря в котором сидит игрок
     */
    public static void handleRadarSync(PantsirRadarSyncMessage message) {
        // Проверяем что игрок сидит именно в этом панцире
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof PantsirS1Entity)) return;
        
        // Фильтруем - принимаем только сообщения от нашего панциря
        if (vehicle.getId() != message.vehicleId) return;
        
        radarState = message.radarState;
        targetEntityId = message.targetEntityId;
        targetX = message.targetX;
        targetY = message.targetY;
        targetZ = message.targetZ;
        targetVelX = message.targetVelX;
        targetVelY = message.targetVelY;
        targetVelZ = message.targetVelZ;
        lockProgress = message.lockProgress;
        targetDistance = message.targetDistance;
        
        // Синхронизируем угол с сервером и сбрасываем время для интерполяции
        serverRadarAngle = message.radarAngle;
        clientRadarAngle = message.radarAngle;
        lastSyncTime = System.currentTimeMillis();
        
        // Угол башни (пассивный радар)
        turretAngle = message.turretAngle;
        
        // Обновляем список всех целей
        allTargets.clear();
        for (int i = 0; i < message.allTargetIds.length; i++) {
            allTargets.add(new RadarTarget(
                message.allTargetIds[i],
                message.allTargetX[i],
                message.allTargetY[i],
                message.allTargetZ[i]
            ));
        }
        
        // Обновляем список ракет
        missiles.clear();
        for (int i = 0; i < message.missileX.length; i++) {
            missiles.add(new MissilePosition(
                message.missileX[i],
                message.missileY[i],
                message.missileZ[i]
            ));
        }
    }
    
    /**
     * Возвращает интерполированный угол радара для плавного вращения
     * Используется для кости модели и GUI
     * Вращается автономно на клиенте, синхронизируется с сервером
     */
    public static float getInterpolatedRadarAngle() {
        long currentTime = System.currentTimeMillis();
        
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            return clientRadarAngle;
        }
        
        // Время с последнего обновления
        float deltaTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;
        
        // Ограничиваем deltaTime чтобы избежать скачков
        deltaTime = Math.min(deltaTime, 50);
        
        // Вращаем радар с постоянной скоростью (против часовой стрелки)
        clientRadarAngle -= RADAR_ROTATION_SPEED * deltaTime;
        
        // Нормализуем угол в диапазон [-360, 0]
        while (clientRadarAngle < -360) {
            clientRadarAngle += 360;
        }
        while (clientRadarAngle > 0) {
            clientRadarAngle -= 360;
        }
        
        return clientRadarAngle;
    }
    
    /**
     * Сброс состояния (когда игрок выходит из машины)
     */
    public static void reset() {
        radarState = PantsirRadarSyncMessage.STATE_IDLE;
        targetEntityId = -1;
        targetX = 0;
        targetY = 0;
        targetZ = 0;
        targetVelX = 0;
        targetVelY = 0;
        targetVelZ = 0;
        lockProgress = 0;
        targetDistance = 0;
        radarAngle = 0;
        turretAngle = 0;
        serverRadarAngle = 0;
        clientRadarAngle = 0;
        lastSyncTime = 0;
        lastUpdateTime = 0;  // Сбрасываем чтобы избежать скачков после респавна
        allTargets.clear();
        missiles.clear();
    }
    
    /**
     * Проверяет, захвачена ли цель
     */
    public static boolean isTargetLocked() {
        return radarState == PantsirRadarSyncMessage.STATE_LOCKED;
    }
    
    /**
     * Проверяет, идёт ли процесс захвата
     */
    public static boolean isLocking() {
        return radarState == PantsirRadarSyncMessage.STATE_LOCKING;
    }
    
    /**
     * Проверяет, обнаружена ли основная цель (для world-marker)
     */
    public static boolean isTargetDetected() {
        return targetEntityId != -1 && (
            radarState == PantsirRadarSyncMessage.STATE_DETECTED ||
            radarState == PantsirRadarSyncMessage.STATE_LOCKING ||
            radarState == PantsirRadarSyncMessage.STATE_LOCKED
        );
    }
    
    /**
     * Проверяет, есть ли цели на радаре
     */
    public static boolean hasTargets() {
        return !allTargets.isEmpty();
    }
}
