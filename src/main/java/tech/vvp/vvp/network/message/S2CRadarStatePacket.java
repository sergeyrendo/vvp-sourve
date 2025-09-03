package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.client.gui.RadarHud;

import java.util.function.Supplier;

public class S2CRadarStatePacket {
    private final boolean enabled;

    public S2CRadarStatePacket(boolean enabled) {
        this.enabled = enabled;
    }

    public static void buffer(S2CRadarStatePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.enabled);
    }

    public S2CRadarStatePacket(FriendlyByteBuf buf) {
        this.enabled = buf.readBoolean();
    }

    public static void handler(S2CRadarStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> {
            // Клиент: включаем/выключаем HUD в соответствии с состоянием радара на сервере
            RadarHud.hudEnabled = msg.enabled;
        });
        context.setPacketHandled(true);
    }
}