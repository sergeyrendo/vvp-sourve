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
        int size = buf.readInt();
        this.targets = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.targets.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
        }
    }

    public static void buffer(S2CRadarSyncPacket message, FriendlyByteBuf buf) {
        buf.writeInt(message.targets.size());
        for (Vec3 target : message.targets) {
            buf.writeDouble(target.x);
            buf.writeDouble(target.y);
            buf.writeDouble(target.z);
        }
    }

    public static void handler(S2CRadarSyncPacket message, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> supplier) {
        net.minecraftforge.network.NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Копируем список, чтобы не держать ссылку на внутреннюю коллекцию пакета
            tech.vvp.vvp.client.gui.RadarHud.onServerTargetsUpdate(new java.util.ArrayList<>(message.targets));
        });
        context.setPacketHandled(true);
    }
}