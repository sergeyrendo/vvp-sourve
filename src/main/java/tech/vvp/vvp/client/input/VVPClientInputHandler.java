package tech.vvp.vvp.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.gui.RadarHud;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.C2SRadarTogglePacket;
import tech.vvp.vvp.radar.IRadarVehicle;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class VVPClientInputHandler {


    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Переключение серверного радара (и HUD через S2C)
        while (VVPKeyMappings.TOGGLE_RADAR.consumeClick()) {
            var v = mc.player.getVehicle();
            if (v instanceof IRadarVehicle && v.getFirstPassenger() == mc.player) {
                VVPNetwork.VVP_HANDLER.sendToServer(new C2SRadarTogglePacket());
            }
        }

        while (VVPKeyMappings.MOVE_RADAR_POS.consumeClick()) {
            var newPos = tech.vvp.vvp.client.gui.RadarHud.cyclePosition();
            Minecraft.getInstance().player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("msg.vvp.radar_pos." + tech.vvp.vvp.client.gui.RadarHud.positionId(newPos)),
                    true
            );
        }
        
        // Переключение режима HIMARS
        while (VVPKeyMappings.TOGGLE_FIRING_MODE.consumeClick()) {
            var v = mc.player.getVehicle();
            if (v instanceof tech.vvp.vvp.entity.vehicle.M142HimarsEntity himars && v.getFirstPassenger() == mc.player) {
                // Отправляем пакет на сервер для переключения режима
                VVPNetwork.VVP_HANDLER.sendToServer(new tech.vvp.vvp.network.message.C2SHimarsToggleModePacket());
            }
        }
    }
}