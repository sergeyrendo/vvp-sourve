package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;

import java.util.function.Supplier;

public class C2SHimarsToggleModePacket {
    
    public C2SHimarsToggleModePacket() {
    }

    public C2SHimarsToggleModePacket(FriendlyByteBuf buf) {
        // Пустой пакет, данных нет
    }

    public void toBytes(FriendlyByteBuf buf) {
        // Пустой пакет, данных нет
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.getVehicle() instanceof M142HimarsEntity himars) {
                himars.toggleMode();
            }
        });
        return true;
    }
}
