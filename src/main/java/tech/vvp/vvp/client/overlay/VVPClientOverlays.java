//package tech.vvp.vvp.client.overlay;
//
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
//import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import tech.vvp.vvp.VVP;
//
//@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
//public final class VVPClientOverlays {
//    private VVPClientOverlays() {}
//
//    @SubscribeEvent
//    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
//        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), AbramsOverlay.ID, new AbramsOverlay());
//        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), AircraftOverlay.ID, new AircraftOverlay());
//        event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), CobraOverlay.ID, new CobraOverlay());
//    }
//}