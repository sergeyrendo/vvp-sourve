package tech.vvp.vvp.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Tos1Entity;

/**
 * Вычислитель траектории для ТОС-1 "Солнцепёк"
 * Показывает Pitch, Yaw и Range слева на экране
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class Tos1Overlay {

    private static final int HUD_COLOR = 0x66FF00; // Зелёный цвет HUD

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null || mc.options.hideGui) {
            return;
        }

        Entity vehicle = player.getVehicle();
        
        // Проверяем что игрок в ТОС-1
        if (!(vehicle instanceof Tos1Entity tos1)) {
            return;
        }
        
        // Проверяем что игрок на месте наводчика (сидушка 0)
        int seatIndex = tos1.getSeatIndex(player);
        if (seatIndex != 0) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        renderTrajectoryCalculator(guiGraphics, player, tos1, screenWidth, screenHeight, event.getPartialTick());
    }

    /**
     * Рендерит вычислитель траектории слева на экране
     */
    private static void renderTrajectoryCalculator(GuiGraphics guiGraphics, Player player, Tos1Entity vehicle, 
                                                    int screenWidth, int screenHeight, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        
        // Позиция вычислителя - слева вверху
        int calcX = 10;
        int calcY = 60;
        
        // Получаем углы башни
        float pitch = getTurretPitch(vehicle, player, partialTick);
        float yaw = getTurretYaw(vehicle, player, partialTick);
        
        // Вычисляем дальность
        double range = calculateRange(player, vehicle, pitch);
        
        // Рисуем заголовок
        guiGraphics.drawString(mc.font, Component.literal("=== BALLISTIC COMPUTER ==="), 
            calcX, calcY, HUD_COLOR, false);
        
        // Рисуем Pitch (угол возвышения)
        String pitchText = String.format("PITCH: %.1f°", pitch);
        guiGraphics.drawString(mc.font, Component.literal(pitchText), 
            calcX, calcY + 15, HUD_COLOR, false);
        
        // Рисуем Yaw (азимут)
        String yawText = String.format("YAW:   %.1f°", normalizeAngle(yaw));
        guiGraphics.drawString(mc.font, Component.literal(yawText), 
            calcX, calcY + 27, HUD_COLOR, false);
        
        // Рисуем Range (дальность)
        String rangeText = String.format("RANGE: %.0fm", range);
        guiGraphics.drawString(mc.font, Component.literal(rangeText), 
            calcX, calcY + 39, HUD_COLOR, false);
        
        // DEBUG - показываем сырое значение
        guiGraphics.drawString(mc.font, Component.literal("DEBUG: " + String.format("%.1f", range)), 
            calcX, calcY + 51, 0xFF0000, false);
    }

    /**
     * Получает угол возвышения башни
     */
    private static float getTurretPitch(Tos1Entity vehicle, Player player, float partialTick) {
        try {
            // Получаем pitch башни напрямую из vehicle
            return -vehicle.getTurretXRot(); // Инвертируем для правильного отображения
        } catch (Exception ignored) {}
        return 0;
    }

    /**
     * Получает азимут башни
     */
    private static float getTurretYaw(Tos1Entity vehicle, Player player, float partialTick) {
        try {
            return vehicle.getTurretYRot();
        } catch (Exception ignored) {}
        return 0;
    }

    /**
     * Вычисляет дальность до цели с правильной баллистикой (как у small_rocket)
     */
    private static double calculateRange(Player player, Tos1Entity vehicle, float pitch) {
        try {
            // Параметры ракеты из конфига ТОС-1
            double velocity = 15.0; // Velocity из конфига
            double gravity = 0.08; // Увеличенная гравитация для меньшей дальности
            double friction = 0.99; // Трение воздуха
            
            // Получаем позицию башни
            Vec3 turretPos = vehicle.position().add(0, 2.5, 0);
            
            // Углы башни
            float yaw = vehicle.getTurretYRot();
            float pitchRad = (float) Math.toRadians(pitch); // БЕЗ инверсии - pitch уже правильный
            float yawRad = (float) Math.toRadians(yaw + vehicle.getYRot());
            
            // Начальная скорость ракеты
            double vx = -Math.sin(yawRad) * Math.cos(pitchRad) * velocity;
            double vy = Math.sin(pitchRad) * velocity;
            double vz = Math.cos(yawRad) * Math.cos(pitchRad) * velocity;
            
            // Симуляция траектории ракеты
            Vec3 pos = turretPos;
            Vec3 vel = new Vec3(vx, vy, vz);
            
            Vec3 hitLocation = null;
            
            // Симулируем до 60 тиков (3 секунды) или до столкновения
            for (int tick = 0; tick < 60; tick++) {
                // 1. Двигаемся (position += velocity)
                Vec3 nextPos = pos.add(vel);
                
                // Проверяем столкновение
                ClipContext context = new ClipContext(
                    pos,
                    nextPos,
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    vehicle
                );
                
                HitResult result = vehicle.level().clip(context);
                
                if (result != null && result.getType() != HitResult.Type.MISS) {
                    // Попали в блок
                    hitLocation = result.getLocation();
                    break;
                }
                
                // Обновляем позицию
                pos = nextPos;
                
                // 2. Применяем гравитацию (velocity.y -= gravity)
                vel = new Vec3(vel.x, vel.y - gravity, vel.z);
                
                // 3. Применяем трение (velocity *= friction)
                vel = vel.scale(friction);
                
                // Если ракета упала слишком низко
                if (pos.y < turretPos.y - 50) {
                    hitLocation = pos;
                    break;
                }
            }
            
            // Вычисляем горизонтальную дистанцию
            if (hitLocation != null) {
                return Math.sqrt(
                    Math.pow(hitLocation.x - turretPos.x, 2) + 
                    Math.pow(hitLocation.z - turretPos.z, 2)
                );
            }
            
            // Если не попали - возвращаем текущую горизонтальную дистанцию
            return Math.sqrt(
                Math.pow(pos.x - turretPos.x, 2) + 
                Math.pow(pos.z - turretPos.z, 2)
            );
        } catch (Exception e) {
            return 999.0;
        }
    }

    /**
     * Нормализует угол в диапазон -180 до 180
     */
    private static float normalizeAngle(float angle) {
        angle = angle % 360;
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }
}
