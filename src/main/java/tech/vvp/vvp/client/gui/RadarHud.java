package tech.vvp.vvp.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.base.AirEntity;
import tech.vvp.vvp.entity.vehicle.mi24Entity;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RadarHud {

    public static List<Vec3> radarTargets = new ArrayList<>();
    private static final ResourceLocation RADAR_BACKGROUND = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_bg.png");
    private static final ResourceLocation RADAR_TARGET = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_target.png");

    // ИЗМЕНЕНИЕ: Используем RenderLevelStageEvent, который сложнее отменить
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Выполняем код только на стадии ПОСЛЕ отрисовки GUI
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_GUI) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || !player.isAlive() || mc.level == null) {
            return;
        }

        if (!(player.getVehicle() instanceof AirEntity)) {
            if (!radarTargets.isEmpty()) {
                radarTargets.clear();
            }
            return;
        }

        // Получаем PoseStack из события
        PoseStack poseStack = event.getPoseStack();
        renderRadar(poseStack, player);
    }

    private static void renderRadar(PoseStack poseStack, Player player) {
        int radarSize = 96;
        int radarX = 10;
        int radarY = 10;
        int radarCenterX = radarX + radarSize / 2;
        int radarCenterY = radarY + radarSize / 2;
        float radarDisplayRange = 150f;

        // Сохраняем текущее состояние, чтобы не сломать другой рендер
        poseStack.pushPose();

        // Настраиваем систему рендера для 2D
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        // Рисуем фон
        RenderSystem.setShaderTexture(0, RADAR_BACKGROUND);
        GuiComponent.blit(poseStack, radarX, radarY, 0, 0, radarSize, radarSize, radarSize, radarSize);

        // Рисуем цели
        RenderSystem.setShaderTexture(0, RADAR_TARGET);
        for (Vec3 targetPos : radarTargets) {
            Vec3 relativePos = targetPos.subtract(player.position());
            double distance = Math.sqrt(relativePos.x * relativePos.x + relativePos.z * relativePos.z);

            if (distance > mi24Entity.RADAR_RANGE) continue;

            float playerYaw = (player.getViewYRot(1.0f) % 360 + 360) % 360;
            double angleToTarget = Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90;
            double rotatedAngle = Math.toRadians(angleToTarget - playerYaw);

            double displayDist = (distance / radarDisplayRange) * (radarSize / 2.0 - 4);
            displayDist = Math.min(displayDist, radarSize / 2.0 - 4);

            int targetX = radarCenterX + (int) (Math.cos(rotatedAngle) * displayDist);
            int targetY = radarCenterY + (int) (Math.sin(rotatedAngle) * displayDist);

            GuiComponent.blit(poseStack, targetX - 2, targetY - 2, 0, 0, 4, 4, 4, 4);
        }

        // Восстанавливаем состояние
        poseStack.popPose();
    }
}