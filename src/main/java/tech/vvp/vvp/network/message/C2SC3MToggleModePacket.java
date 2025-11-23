package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.C3MEntity;

import java.util.function.Supplier;

public class C2SC3MToggleModePacket {
    
    public C2SC3MToggleModePacket() {
    }

    public C2SC3MToggleModePacket(FriendlyByteBuf buf) {
        // Пустой пакет, данных нет
    }

    public void toBytes(FriendlyByteBuf buf) {
        // Пустой пакет, данных нет
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.getVehicle() instanceof C3MEntity c3m) {
                // Проверяем что игрок водитель (seat index 0)
                if (c3m.getSeatIndex(player) == 0) {
                    c3m.toggleMode();
                }
            }
        });
        return true;
    }
}

