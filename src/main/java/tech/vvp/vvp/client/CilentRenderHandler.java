package tech.vvp.vvp.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tech.vvp.vvp.client.overlay.*;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CilentRenderHandler {
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll(Mi28Overlay.ID,new Mi28Overlay());
        event.registerBelowAll(Mi28_1Overlay.ID,new Mi28_1Overlay());
        event.registerBelowAll(Mi28_1HudOverlay.ID,new Mi28_1HudOverlay());
    }


    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll(AbramsOverlay.ID, new AbramsOverlay());
        event.registerAboveAll(AircraftOverlay.ID, new AircraftOverlay());
        event.registerAboveAll(CobraOverlay.ID, new CobraOverlay());
    }
}