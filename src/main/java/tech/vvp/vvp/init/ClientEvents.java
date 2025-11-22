package tech.vvp.vvp.init;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.input.VVPKeyMappings;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;
import tech.vvp.vvp.network.message.C2SHimarsToggleModePacket;
import tech.vvp.vvp.network.message.C2SPantsirToggleSupportsPacket;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        Entity vehicle = mc.player.getVehicle();

        // Клавиша Q теперь переключает режим стрельбы для HIMARS
        if (VVPKeyMappings.TOGGLE_FIRING_MODE.consumeClick()) {
            if (vehicle instanceof M142HimarsEntity) {
                VVP.PACKET_HANDLER.sendToServer(new C2SHimarsToggleModePacket());
            }
        }

        // Клавиша R - переключение радара для Панциря
        // Клавиша B - переключение опор для Панциря
        if (VVPKeyMappings.TOGGLE_SUPPORTS.consumeClick()) {
            if (vehicle instanceof PantsirS1Entity) {
                VVP.PACKET_HANDLER.sendToServer(new C2SPantsirToggleSupportsPacket());
            }
        }
    }
}
