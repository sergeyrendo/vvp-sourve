package tech.vvp.vvp.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tech.vvp.vvp.client.overlay.*;
import tech.vvp.vvp.VVP;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRenderHandler {
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll(TowOverlay.ID, new TowOverlay());
    }


    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
//        event.registerAboveAll(AbramsOverlay.ID, new AbramsOverlay());
    }
}
