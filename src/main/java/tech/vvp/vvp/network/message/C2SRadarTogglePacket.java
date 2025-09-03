package tech.vvp.vvp.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.radar.IRadarVehicle;

import java.util.Collections;
import java.util.function.Supplier;

public class C2SRadarTogglePacket {

    public C2SRadarTogglePacket() {}

    public static void encode(C2SRadarTogglePacket msg, FriendlyByteBuf buf) {}

    public static C2SRadarTogglePacket decode(FriendlyByteBuf buf) {
        return new C2SRadarTogglePacket();
    }

    public static void handle(C2SRadarTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        var context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) return;

            var vehicle = sender.getVehicle();
            if (vehicle instanceof IRadarVehicle rv && vehicle.getFirstPassenger() == sender) {
                boolean newState = !rv.isRadarEnabled();
                rv.setRadarEnabled(newState);

                // Очистить метки при выключении
                if (!newState) {
                    VVPNetwork.VVP_HANDLER.sendTo(
                            new S2CRadarSyncPacket(Collections.emptyList()),
                            sender.connection.connection,
                            NetworkDirection.PLAY_TO_CLIENT
                    );
                }

                // Синхронизировать состояние HUD (вкл/выкл) с клиентом
                VVPNetwork.VVP_HANDLER.sendTo(
                        new S2CRadarStatePacket(newState),
                        sender.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        });
        context.setPacketHandled(true);
    }
}