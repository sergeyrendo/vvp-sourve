package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.client.gui.RadarHud;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class S2CRadarSyncPacket {

    private final List<Vec3> targets;

    public S2CRadarSyncPacket(List<Vec3> targets) {
        this.targets = targets;
    }

    public S2CRadarSyncPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.targets = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.targets.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
    }

    public static void buffer(S2CRadarSyncPacket message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.targets.size());
        for (Vec3 target : message.targets) {
            buf.writeDouble(target.x);
            buf.writeDouble(target.y);
            buf.writeDouble(target.z);
        }
    }

    public static void handler(S2CRadarSyncPacket message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // --- КЛИЕНТСКИЙ МАЯЧОК: Сообщаем о получении пакета ---
            System.out.println("[RADAR CLIENT DEBUG] Packet received with " + message.targets.size() + " targets.");
            RadarHud.radarTargets = message.targets;
        });
        context.setPacketHandled(true);
    }
}