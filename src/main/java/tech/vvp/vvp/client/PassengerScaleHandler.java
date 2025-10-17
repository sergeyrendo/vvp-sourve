package tech.vvp.vvp.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod; // Важно: это Forge Mod, а не ваш класс
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.entity.vehicle.Mi28Entity;
import tech.vvp.vvp.VVP; // ваш главный класс с MODID

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PassengerScaleHandler {

    private static final float SCALE_SEAT_0_1 = 0.9f;

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity living = event.getEntity();
        if (!(living instanceof Player player)) return;

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Mi28Entity heli)) return;

        int seat = heli.getSeatIndex(player);
        if (seat == 0 || seat == 1) {
            PoseStack pose = event.getPoseStack();
            pose.pushPose();

            float s = SCALE_SEAT_0_1;
            double yOffset = (1.0 - s) * player.getBbHeight() * 0.5;
            pose.translate(0.0, yOffset, 0.0);
            pose.scale(s, s, s);
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity living = event.getEntity();
        if (!(living instanceof Player player)) return;

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof Mi28Entity heli)) return;

        int seat = heli.getSeatIndex(player);
        if (seat == 0 || seat == 1) {
            event.getPoseStack().popPose();
        }
    }
}