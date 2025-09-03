package tech.vvp.vvp.radar;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.server.ServerLifecycleHooks;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.S2CRadarStatePacket;
import tech.vvp.vvp.network.message.S2CRadarSyncPacket;

import java.util.Collections;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID)
public class RadarSystem {

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        for (ServerLevel level : server.getAllLevels()) {
            for (ServerPlayer player : level.players()) {
                if (player.tickCount % 20 != 0) continue; // ~1 раз в секунду

                Entity vehicle = player.getVehicle();
                if (vehicle instanceof IRadarVehicle rv && vehicle.getFirstPassenger() == player) {
                    if (!rv.isRadarEnabled()) continue;

                    // Пытаемся списать энергию у конкретной техники
                    if (!rv.consumeRadarEnergy()) {
                        rv.setRadarEnabled(false);

                        // Без сообщения игроку
                        VVPNetwork.VVP_HANDLER.sendTo(
                                new S2CRadarStatePacket(false),
                                player.connection.connection,
                                NetworkDirection.PLAY_TO_CLIENT
                        );
                        VVPNetwork.VVP_HANDLER.sendTo(
                                new S2CRadarSyncPacket(Collections.emptyList()),
                                player.connection.connection,
                                NetworkDirection.PLAY_TO_CLIENT
                        );
                        continue;
                    }

                    // Энергии хватило — сканируем и шлём цели
                    rv.scanAndSendRadarTo(player);
                }
            }
        }
    }
}