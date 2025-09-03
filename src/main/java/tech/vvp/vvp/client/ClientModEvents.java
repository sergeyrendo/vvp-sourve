package tech.vvp.vvp.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.gui.RadarHud;
// Этот класс будет автоматически найден Forge благодаря аннотации
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        // Регистрируем наш радар под ID "radar" и указываем, что он должен рисоваться поверх всего
        event.registerAboveAll("radar", RadarHud.HUD_RADAR);
    }

}