package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.client.PantsirClientHandler;

import java.util.function.Supplier;

/**
 * Сообщение от сервера к клиенту для синхронизации состояния радара Pantsir
 */
public class PantsirRadarSyncMessage {
    
    // Состояния радара
    public static final int STATE_IDLE = 0;        // Поиск - нет цели
    public static final int STATE_DETECTED = 1;    // Цель обнаружена, но не захвачена
    public static final int STATE_LOCKING = 2;     // Идёт процесс захвата
    public static final int STATE_LOCKED = 3;      // Цель захвачена
    public static final int STATE_LOST = 4;        // Захват потерян
    
    public final int vehicleId;       // ID панциря, от которого пришло сообщение
    public final int radarState;
    public final int targetEntityId;  // -1 если нет цели
    public final double targetX;
    public final double targetY;
    public final double targetZ;
    public final int lockProgress;    // 0-100, прогресс захвата
    public final double targetDistance;
    public final float radarAngle;    // Угол вращения радара в градусах (абсолютный)
    public final float turretAngle;   // Угол башни (пассивный радар) в градусах
    
    // Все обнаруженные цели для отображения на радаре
    public final int[] allTargetIds;
    public final double[] allTargetX;
    public final double[] allTargetY;
    public final double[] allTargetZ;
    
    // Выпущенные ракеты для отображения на радаре
    public final double[] missileX;
    public final double[] missileY;
    public final double[] missileZ;
    
    public PantsirRadarSyncMessage(int vehicleId, int radarState, int targetEntityId, double targetX, double targetY, double targetZ, 
                                   int lockProgress, double targetDistance, float radarAngle, float turretAngle,
                                   int[] allTargetIds, double[] allTargetX, double[] allTargetY, double[] allTargetZ,
                                   double[] missileX, double[] missileY, double[] missileZ) {
        this.vehicleId = vehicleId;
        this.radarState = radarState;
        this.targetEntityId = targetEntityId;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.lockProgress = lockProgress;
        this.targetDistance = targetDistance;
        this.radarAngle = radarAngle;
        this.turretAngle = turretAngle;
        this.allTargetIds = allTargetIds;
        this.allTargetX = allTargetX;
        this.allTargetY = allTargetY;
        this.allTargetZ = allTargetZ;
        this.missileX = missileX;
        this.missileY = missileY;
        this.missileZ = missileZ;
    }
    
    public static void encode(PantsirRadarSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.vehicleId);
        buffer.writeInt(message.radarState);
        buffer.writeInt(message.targetEntityId);
        buffer.writeDouble(message.targetX);
        buffer.writeDouble(message.targetY);
        buffer.writeDouble(message.targetZ);
        buffer.writeInt(message.lockProgress);
        buffer.writeDouble(message.targetDistance);
        buffer.writeFloat(message.radarAngle);
        buffer.writeFloat(message.turretAngle);
        
        // Все цели
        buffer.writeInt(message.allTargetIds.length);
        for (int i = 0; i < message.allTargetIds.length; i++) {
            buffer.writeInt(message.allTargetIds[i]);
            buffer.writeDouble(message.allTargetX[i]);
            buffer.writeDouble(message.allTargetY[i]);
            buffer.writeDouble(message.allTargetZ[i]);
        }
        
        // Ракеты
        buffer.writeInt(message.missileX.length);
        for (int i = 0; i < message.missileX.length; i++) {
            buffer.writeDouble(message.missileX[i]);
            buffer.writeDouble(message.missileY[i]);
            buffer.writeDouble(message.missileZ[i]);
        }
    }
    
    public static PantsirRadarSyncMessage decode(FriendlyByteBuf buffer) {
        int vehicleId = buffer.readInt();
        int radarState = buffer.readInt();
        int targetEntityId = buffer.readInt();
        double targetX = buffer.readDouble();
        double targetY = buffer.readDouble();
        double targetZ = buffer.readDouble();
        int lockProgress = buffer.readInt();
        double targetDistance = buffer.readDouble();
        float radarAngle = buffer.readFloat();
        float turretAngle = buffer.readFloat();
        
        int count = buffer.readInt();
        int[] allTargetIds = new int[count];
        double[] allTargetX = new double[count];
        double[] allTargetY = new double[count];
        double[] allTargetZ = new double[count];
        
        for (int i = 0; i < count; i++) {
            allTargetIds[i] = buffer.readInt();
            allTargetX[i] = buffer.readDouble();
            allTargetY[i] = buffer.readDouble();
            allTargetZ[i] = buffer.readDouble();
        }
        
        int missileCount = buffer.readInt();
        double[] missileX = new double[missileCount];
        double[] missileY = new double[missileCount];
        double[] missileZ = new double[missileCount];
        
        for (int i = 0; i < missileCount; i++) {
            missileX[i] = buffer.readDouble();
            missileY[i] = buffer.readDouble();
            missileZ[i] = buffer.readDouble();
        }
        
        return new PantsirRadarSyncMessage(
            vehicleId, radarState, targetEntityId, targetX, targetY, targetZ, lockProgress, targetDistance, 
            radarAngle, turretAngle, allTargetIds, allTargetX, allTargetY, allTargetZ,
            missileX, missileY, missileZ
        );
    }
    
    public static void handler(PantsirRadarSyncMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> PantsirClientHandler.handleRadarSync(message)));
        ctx.get().setPacketHandled(true);
    }
}
