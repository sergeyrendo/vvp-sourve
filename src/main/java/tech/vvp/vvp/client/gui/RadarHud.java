package tech.vvp.vvp.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.base.AirEntity;
import tech.vvp.vvp.entity.vehicle.mi24Entity;

import java.util.ArrayList;
import java.util.List;

// ИЗМЕНЕНИЕ ЗДЕСЬ: Добавлен параметр bus = Mod.EventBusSubscriber.Bus.FORGE
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RadarHud {

    public static List<Vec3> radarTargets = new ArrayList<>();
    private static final ResourceLocation RADAR_BACKGROUND = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_bg.png");
    private static final ResourceLocation RADAR_TARGET = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_target.png");

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || !player.isAlive()) return;

        if (!(player.getVehicle() instanceof AirEntity)) {
            if (!radarTargets.isEmpty()) {
                radarTargets.clear();
            }
            return;
        }

        renderRadar(event.getGuiGraphics(), player);
    }

    private static void renderRadar(GuiGraphics guiGraphics, Player player) {
        int radarSize = 96;
        int radarX = 10;
        int radarY = 10;
        int radarCenterX = radarX + radarSize / 2;
        int radarCenterY = radarY + radarSize / 2;
        float radarDisplayRange = 64f;

        RenderSystem.enableBlend();
        guiGraphics.blit(RADAR_BACKGROUND, radarX, radarY, 0, 0, radarSize, radarSize, radarSize, radarSize);

        for (Vec3 targetPos : radarTargets) {
            Vec3 relativePos = targetPos.subtract(player.position());
            double distance = Math.sqrt(relativePos.x * relativePos.x + relativePos.z * relativePos.z);

            if (distance > mi24Entity.RADAR_RANGE) continue;

            float playerYaw = (player.getViewYRot(1.0f) % 360 + 360) % 360;
            double angleToTarget = Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90;
            double rotatedAngle = Math.toRadians(angleToTarget - playerYaw);

            double displayDist = Math.min(distance, radarDisplayRange) / radarDisplayRange * (radarSize / 2.0 - 4);

            int targetX = radarCenterX + (int) (Math.cos(rotatedAngle) * displayDist);
            int targetY = radarCenterY + (int) (Math.sin(rotatedAngle) * displayDist);

            guiGraphics.blit(RADAR_TARGET, targetX - 2, targetY - 2, 0, 0, 4, 4, 4, 4);
        }
        RenderSystem.disableBlend();
    }
}