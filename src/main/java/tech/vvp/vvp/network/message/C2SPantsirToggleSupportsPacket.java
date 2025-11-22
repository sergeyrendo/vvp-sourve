package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

import java.util.function.Supplier;

public class C2SPantsirToggleSupportsPacket {
    
    public C2SPantsirToggleSupportsPacket() {
    }

    public C2SPantsirToggleSupportsPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.getVehicle() instanceof PantsirS1Entity pantsir) {
                pantsir.toggleSupports();
            }
        });
        return true;
    }
}
