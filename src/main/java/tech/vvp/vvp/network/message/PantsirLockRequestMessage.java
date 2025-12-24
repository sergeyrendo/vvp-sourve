package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

import java.util.function.Supplier;

/**
 * Сообщение от клиента к серверу для запроса захвата цели
 */
public class PantsirLockRequestMessage {
    
    public static final int ACTION_START_LOCK = 0;   // Начать захват
    public static final int ACTION_CANCEL_LOCK = 1;  // Отменить захват
    
    public final int action;
    
    public PantsirLockRequestMessage(int action) {
        this.action = action;
    }
    
    public static void encode(PantsirLockRequestMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.action);
    }
    
    public static PantsirLockRequestMessage decode(FriendlyByteBuf buffer) {
        return new PantsirLockRequestMessage(buffer.readInt());
    }
    
    public static void handler(PantsirLockRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            
            Entity vehicle = player.getVehicle();
            if (!(vehicle instanceof PantsirS1Entity pantsir)) return;
            
            // Проверяем что игрок на месте оператора (сидушка 1)
            int seatIndex = pantsir.getSeatIndex(player);
            if (seatIndex != 1) return;
            
            if (message.action == ACTION_START_LOCK) {
                pantsir.requestLock(player);
            } else if (message.action == ACTION_CANCEL_LOCK) {
                pantsir.cancelLock(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
