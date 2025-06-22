package tech.vvp.vvp.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.mi24Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class RadarHud {
    public static List<Vec3> radarTargets = new ArrayList<>();

    private static final ResourceLocation RADAR_BACKGROUND = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_bg.png");
    private static final ResourceLocation RADAR_TARGET = new ResourceLocation(VVP.MOD_ID, "textures/gui/radar_target.png");
    
    // СПИСОК ПОДДЕРЖИВАЕМЫХ ВЕРТОЛЕТОВ
    private static final List<String> SUPPORTED_HELICOPTERS = Arrays.asList(
        "mi24Entity",
        "mi24ukrEntity", 
        "mi24polEntity"
    );

    public static final IGuiOverlay HUD_RADAR = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || !player.isAlive() || mc.level == null) {
            return;
        }

        Entity vehicle = player.getVehicle();

        // ПРОВЕРКА НА ПОДДЕРЖИВАЕМЫЕ ВЕРТОЛЕТЫ
        if (vehicle != null && isRadarHelicopter(vehicle)) {
            // --- МАЯЧОК: Если проверка прошла, мы увидим это в логе ---
            System.out.println("[RADAR DEBUG] VEHICLE CHECK PASSED: " + vehicle.getClass().getName());

            int radarSize = 96;
            int radarX = 10;
            int radarY = 10;
            int radarCenterX = radarX + radarSize / 2;
            int radarCenterY = radarY + radarSize / 2;
            float radarDisplayRange = 150f;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            guiGraphics.blit(RADAR_BACKGROUND, radarX, radarY, 0, 0, radarSize, radarSize, radarSize, radarSize);

            for (Vec3 targetPos : radarTargets) {
                Vec3 relativePos = targetPos.subtract(player.position());
                double distance = Math.sqrt(relativePos.x * relativePos.x + relativePos.z * relativePos.z);

                if (distance > mi24Entity.RADAR_RANGE) continue;

                float playerYaw = (player.getViewYRot(1.0f) % 360 + 360) % 360;
                double angleToTarget = Math.toDegrees(Math.atan2(relativePos.z, relativePos.x)) - 90;
                
                // --- ИЗМЕНЕНИЕ ТОЛЬКО ЗДЕСЬ: Поворот на 90 градусов налево ---
                double rotatedAngle = Math.toRadians(angleToTarget - playerYaw - 90);

                double displayDist = (distance / radarDisplayRange) * (radarSize / 2.0 - 4);
                displayDist = Math.min(displayDist, radarSize / 2.0 - 4);

                int targetX = radarCenterX + (int) (Math.cos(rotatedAngle) * displayDist);
                int targetY = radarCenterY + (int) (Math.sin(rotatedAngle) * displayDist);

                guiGraphics.blit(RADAR_TARGET, targetX - 2, targetY - 2, 0, 0, 4, 4, 4, 4);
            }

            RenderSystem.disableBlend();
        } else if (vehicle != null) {
            // --- МАЯЧОК: Если мы в технике, но проверка не прошла, мы узнаем, что это за техника ---
            System.out.println("[RADAR DEBUG] In vehicle, but check failed. Vehicle class is: " + vehicle.getClass().getName());
        }
    };
    
    // МЕТОД ПРОВЕРКИ ПОДДЕРЖИВАЕМЫХ ВЕРТОЛЕТОВ
    private static boolean isRadarHelicopter(Entity vehicle) {
        String vehicleClassName = vehicle.getClass().getSimpleName();
        return SUPPORTED_HELICOPTERS.contains(vehicleClassName);
    }
}