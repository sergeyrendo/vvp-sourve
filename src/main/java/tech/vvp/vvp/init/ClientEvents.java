package tech.vvp.vvp.init;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.input.VVPKeyMappings;
import tech.vvp.vvp.client.screen.CoordinateInputScreen;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) return;

        if (VVPKeyMappings.OPEN_COORDINATE_SCREEN.consumeClick()) {
            Entity vehicle = mc.player.getVehicle();
            if (vehicle instanceof CoordinateTargetVehicle targetable) {
                mc.setScreen(new CoordinateInputScreen(targetable));
            }
        }
    }
}
