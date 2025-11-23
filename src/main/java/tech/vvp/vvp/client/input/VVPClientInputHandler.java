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
        
        while (VVPKeyMappings.TOGGLE_FIRING_MODE.consumeClick()) {
            var vehicle = mc.player.getVehicle();
            if (vehicle instanceof tech.vvp.vvp.entity.vehicle.M142HimarsEntity himars && vehicle.getFirstPassenger() == mc.player) {
                VVPNetwork.VVP_HANDLER.sendToServer(new tech.vvp.vvp.network.message.C2SHimarsToggleModePacket());
            } else if (vehicle instanceof tech.vvp.vvp.entity.vehicle.C3MEntity c3m && vehicle.getFirstPassenger() == mc.player) {
                VVPNetwork.VVP_HANDLER.sendToServer(new tech.vvp.vvp.network.message.C2SC3MToggleModePacket());
            }
        }
        
        while (VVPKeyMappings.OPEN_COORDINATE_INPUT.consumeClick()) {
            var coordVehicle = mc.player.getVehicle();
            if (coordVehicle instanceof tech.vvp.vvp.init.CoordinateTargetVehicle coordinateVehicle && coordVehicle.getFirstPassenger() == mc.player) {
                mc.setScreen(new tech.vvp.vvp.client.screen.CoordinateInputScreen(coordinateVehicle));
            }
        }
    }
}