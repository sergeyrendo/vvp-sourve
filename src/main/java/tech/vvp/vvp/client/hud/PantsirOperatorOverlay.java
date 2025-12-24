package tech.vvp.vvp.client.hud;

import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.tools.VectorUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.PantsirClientHandler;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.PantsirLockRequestMessage;
import tech.vvp.vvp.network.message.PantsirRadarSyncMessage;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class PantsirOperatorOverlay {

    private static final int COLOR_RADAR_GREEN = 0xFF00FF00;
    private static final int COLOR_RADAR_DARK = 0xFF003300;
    private static final int COLOR_RADAR_BG = 0xE0001100;
    private static final int COLOR_TARGET_YELLOW = 0xFFFFFF00;
    private static final int COLOR_TARGET_RED = 0xFFFF3333;
    private static final int COLOR_LOCKED = 0xFF00FF00;
    private static final int COLOR_TARGET_BLIP = 0xFFFF6600;
    private static final int COLOR_TURRET_BEAM = 0xFFFF00FF;
    private static final int COLOR_SSC_SECTOR = 0x40FF00FF;
    private static final int COLOR_PANEL_BG = 0xC0000000; // Полупрозрачный чёрный для фона панели
    private static final int COLOR_MISSILE = 0xFF00FFFF; // Голубой для ракет
    
    private static final int RADAR_RADIUS = 60;
    private static final int RADAR_SEGMENTS = 32;
    private static final int MARKER_SIZE = 24;
    private static final int MARKER_HALF = MARKER_SIZE / 2;
    private static final double RADAR_RANGE = 700.0;
    private static final float SSC_HALF_ANGLE = 3.0f;
    
    private static boolean wasLockKeyPressed = false;
    private static int lastRadarState = -1;
    private static long lastLockingSoundTime = 0;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null || mc.options.hideGui) return;

        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof PantsirS1Entity pantsir)) {
            PantsirClientHandler.reset();
            return;
        }
        
        int seatIndex = pantsir.getSeatIndex(player);
        if (seatIndex != 1) return;
        
        handleLockKeyInput(player);
        handleLockSounds(player);

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        renderRadarDisplay(guiGraphics, poseStack, screenWidth, screenHeight, player);
        renderStatusPanel(guiGraphics, screenWidth, screenHeight);
        renderTargetMarker(guiGraphics, screenWidth, screenHeight);
        renderControlHint(guiGraphics, screenWidth, screenHeight);
        
        poseStack.popPose();
    }
    
    private static void handleLockKeyInput(Player player) {
        // Используем кнопку VEHICLE_SEEK из SBW (X по умолчанию)
        boolean isPressed = ModKeyMappings.VEHICLE_SEEK.isDown();
        
        if (isPressed && !wasLockKeyPressed) {
            int state = PantsirClientHandler.radarState;
            
            // X начинает захват когда цель обнаружена
            if (state == PantsirRadarSyncMessage.STATE_DETECTED) {
                VVPNetwork.VVP_HANDLER.sendToServer(
                    new PantsirLockRequestMessage(PantsirLockRequestMessage.ACTION_START_LOCK)
                );
            } 
            // X отменяет захват во время LOCKING или сбрасывает когда LOCKED
            else if (state == PantsirRadarSyncMessage.STATE_LOCKING || 
                     state == PantsirRadarSyncMessage.STATE_LOCKED) {
                VVPNetwork.VVP_HANDLER.sendToServer(
                    new PantsirLockRequestMessage(PantsirLockRequestMessage.ACTION_CANCEL_LOCK)
                );
            }
        }
        wasLockKeyPressed = isPressed;
    }
    
    private static void handleLockSounds(Player player) {
        int currentState = PantsirClientHandler.radarState;
        long currentTime = System.currentTimeMillis();
        
        // Звук в начале LOCKING (один раз)
        if (currentState == PantsirRadarSyncMessage.STATE_LOCKING && lastRadarState != PantsirRadarSyncMessage.STATE_LOCKING) {
            player.playSound(ModSounds.MISSILE_LOCKING.get(), 2.0f, 1.0f);
        }
        
        // Звук когда LOCKED - пищит постоянно как у иглы (каждый кадр)
        if (currentState == PantsirRadarSyncMessage.STATE_LOCKED) {
            player.playSound(ModSounds.MISSILE_LOCKED.get(), 2.0f, 1.0f);
        }
        
        lastRadarState = currentState;
    }

    private static void renderRadarDisplay(GuiGraphics guiGraphics, PoseStack poseStack, 
            int screenWidth, int screenHeight, Player player) {
        int centerX = screenWidth - RADAR_RADIUS - 25;
        int centerY = RADAR_RADIUS + 40;
        
        drawFilledCircle(poseStack, centerX, centerY, RADAR_RADIUS, COLOR_RADAR_BG);
        drawCircleOutline(poseStack, centerX, centerY, RADAR_RADIUS * 0.33f, COLOR_RADAR_DARK);
        drawCircleOutline(poseStack, centerX, centerY, RADAR_RADIUS * 0.66f, COLOR_RADAR_DARK);
        drawCircleOutline(poseStack, centerX, centerY, RADAR_RADIUS - 2, COLOR_RADAR_GREEN);
        
        guiGraphics.fill(centerX - 1, centerY - 8, centerX + 1, centerY + 8, COLOR_RADAR_DARK);
        guiGraphics.fill(centerX - 8, centerY - 1, centerX + 8, centerY + 1, COLOR_RADAR_DARK);
        
        // Полоска 1: Вращающийся обзорный радар (зелёный)
        drawRadarSweep(poseStack, centerX, centerY, player);
        
        // Полоска 2: Пассивный радар - направление башни (фиолетовый)
        drawTurretBeam(poseStack, centerX, centerY, player);
        
        // Все цели на радаре
        drawAllTargetBlips(guiGraphics, centerX, centerY, player);
        
        // Ракеты на радаре (с пунктирной линией)
        drawMissiles(guiGraphics, poseStack, centerX, centerY, player);
        
        Minecraft mc = Minecraft.getInstance();
        // Все буквы направлений одного цвета (зелёный)
        guiGraphics.drawString(mc.font, "N", centerX - 3, centerY - RADAR_RADIUS - 12, COLOR_RADAR_GREEN, false);
        guiGraphics.drawString(mc.font, "S", centerX - 3, centerY + RADAR_RADIUS + 4, COLOR_RADAR_GREEN, false);
        guiGraphics.drawString(mc.font, "W", centerX - RADAR_RADIUS - 10, centerY - 4, COLOR_RADAR_GREEN, false);
        guiGraphics.drawString(mc.font, "E", centerX + RADAR_RADIUS + 4, centerY - 4, COLOR_RADAR_GREEN, false);
        
        guiGraphics.drawString(mc.font, "700m", centerX + RADAR_RADIUS - 20, centerY - RADAR_RADIUS + 5, COLOR_RADAR_DARK, false);
        
        int targetCount = PantsirClientHandler.allTargets.size();
        if (targetCount > 0) {
            guiGraphics.drawString(mc.font, "TGT: " + targetCount, centerX - RADAR_RADIUS + 5, 
                centerY - RADAR_RADIUS + 5, COLOR_TARGET_YELLOW, false);
        }
    }
    
    private static void drawRadarSweep(PoseStack poseStack, int centerX, int centerY, Player player) {
        // radarAngle от сервера - абсолютный угол вращения радара (в градусах, как yaw)
        float radarAngle = PantsirClientHandler.getInterpolatedRadarAngle();
        
        // Та же формула что и для turret beam
        double radians = Math.toRadians(radarAngle);
        
        int beamLength = RADAR_RADIUS - 5;
        int endX = centerX + (int)(Math.sin(radians) * beamLength);
        int endY = centerY + (int)(Math.cos(radians) * beamLength);
        
        int beamColor = switch (PantsirClientHandler.radarState) {
            case PantsirRadarSyncMessage.STATE_LOCKED -> COLOR_LOCKED;
            case PantsirRadarSyncMessage.STATE_LOCKING, PantsirRadarSyncMessage.STATE_DETECTED -> COLOR_TARGET_YELLOW;
            default -> COLOR_RADAR_GREEN;
        };
        
        drawLine(poseStack, centerX, centerY, endX, endY, beamColor, 2);
        
        float sectorAngle = 15.0f;
        double leftRad = radians - Math.toRadians(sectorAngle);
        double rightRad = radians + Math.toRadians(sectorAngle);
        
        int leftX = centerX + (int)(Math.sin(leftRad) * beamLength);
        int leftY = centerY + (int)(Math.cos(leftRad) * beamLength);
        int rightX = centerX + (int)(Math.sin(rightRad) * beamLength);
        int rightY = centerY + (int)(Math.cos(rightRad) * beamLength);
        
        drawTriangle(poseStack, centerX, centerY, leftX, leftY, rightX, rightY, 0x3000FF00);
    }
    
    /**
     * Рисует ССЦ (Станция Сопровождения Целей) - узкий сектор ±3° от направления башни
     * Стабилизированный - север = вверх
     */
    private static void drawTurretBeam(PoseStack poseStack, int centerX, int centerY, Player player) {
        float turretAngle = PantsirClientHandler.turretAngle;
        double radians = Math.toRadians(turretAngle);
        
        int beamLength = RADAR_RADIUS - 5;
        
        // W/E инвертированы - меняем знак X
        int endX = centerX - (int)(Math.sin(radians) * beamLength);
        int endY = centerY + (int)(Math.cos(radians) * beamLength);
        drawLine(poseStack, centerX, centerY, endX, endY, COLOR_TURRET_BEAM, 2);
        
        // Сектор ССЦ (±3°)
        double leftRad = radians - Math.toRadians(SSC_HALF_ANGLE);
        double rightRad = radians + Math.toRadians(SSC_HALF_ANGLE);
        
        int leftX = centerX - (int)(Math.sin(leftRad) * beamLength);
        int leftY = centerY + (int)(Math.cos(leftRad) * beamLength);
        int rightX = centerX - (int)(Math.sin(rightRad) * beamLength);
        int rightY = centerY + (int)(Math.cos(rightRad) * beamLength);
        
        // Полупрозрачный сектор ССЦ
        drawTriangle(poseStack, centerX, centerY, leftX, leftY, rightX, rightY, COLOR_SSC_SECTOR);
    }

    private static void drawAllTargetBlips(GuiGraphics guiGraphics, int centerX, int centerY, Player player) {
        // Стабилизированный радар - используем позицию панциря, не игрока
        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;
        
        Vec3 radarPos = vehicle.position();
        
        for (PantsirClientHandler.RadarTarget target : PantsirClientHandler.allTargets) {
            Vec3 targetPos = new Vec3(target.x, target.y, target.z);
            Vec3 toTarget = targetPos.subtract(radarPos);
            double distance = toTarget.horizontalDistance();
            
            double normalizedDist = Math.min(distance / RADAR_RANGE, 1.0);
            
            // В Minecraft: Z+ = Юг, Z- = Север, X+ = Восток, X- = Запад
            // На радаре: вверх = Север (Y-), вправо = Восток (X+)
            // toTarget.x > 0 = восток = вправо на радаре
            // toTarget.z < 0 = север = вверх на радаре
            int radarDist = (int)(normalizedDist * (RADAR_RADIUS - 8));
            int blipX = centerX + (int)(toTarget.x / distance * radarDist);
            int blipY = centerY + (int)(toTarget.z / distance * radarDist);
            
            boolean isMainTarget = (target.entityId == PantsirClientHandler.targetEntityId);
            int blipColor;
            int size;
            
            if (isMainTarget) {
                blipColor = switch (PantsirClientHandler.radarState) {
                    case PantsirRadarSyncMessage.STATE_LOCKED -> COLOR_LOCKED;
                    case PantsirRadarSyncMessage.STATE_LOCKING -> COLOR_TARGET_YELLOW;
                    default -> COLOR_TARGET_RED;
                };
                size = 3;
            } else {
                blipColor = COLOR_TARGET_BLIP;
                size = 2;
            }
            
            guiGraphics.fill(blipX - size, blipY - size, blipX + size, blipY + size, blipColor);
            
            if (isMainTarget && PantsirClientHandler.radarState == PantsirRadarSyncMessage.STATE_LOCKED) {
                long time = System.currentTimeMillis();
                if ((time / 250) % 2 == 0) {
                    guiGraphics.fill(blipX - size - 1, blipY - size - 1, blipX + size + 1, blipY - size, blipColor);
                    guiGraphics.fill(blipX - size - 1, blipY + size, blipX + size + 1, blipY + size + 1, blipColor);
                    guiGraphics.fill(blipX - size - 1, blipY - size - 1, blipX - size, blipY + size + 1, blipColor);
                    guiGraphics.fill(blipX + size, blipY - size - 1, blipX + size + 1, blipY + size + 1, blipColor);
                }
            }
        }
    }
    
    /**
     * Рисует ракеты на радаре с пунктирной линией от центра
     */
    private static void drawMissiles(GuiGraphics guiGraphics, PoseStack poseStack, int centerX, int centerY, Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle == null) return;
        
        Vec3 radarPos = vehicle.position();
        
        for (PantsirClientHandler.MissilePosition missile : PantsirClientHandler.missiles) {
            Vec3 missilePos = new Vec3(missile.x, missile.y, missile.z);
            Vec3 toMissile = missilePos.subtract(radarPos);
            double distance = toMissile.horizontalDistance();
            
            if (distance < 1.0) continue; // Слишком близко к центру
            
            double normalizedDist = Math.min(distance / RADAR_RANGE, 1.0);
            
            int radarDist = (int)(normalizedDist * (RADAR_RADIUS - 8));
            int blipX = centerX + (int)(toMissile.x / distance * radarDist);
            int blipY = centerY + (int)(toMissile.z / distance * radarDist);
            
            // Пунктирная линия от центра к ракете
            drawDashedLine(poseStack, centerX, centerY, blipX, blipY, COLOR_MISSILE, 4, 3);
            
            // Точка ракеты (треугольник)
            int size = 3;
            guiGraphics.fill(blipX - size, blipY - size, blipX + size, blipY + size, COLOR_MISSILE);
        }
    }

    private static void renderStatusPanel(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        int x = 15;
        int y = 40;
        
        // Фон панели
        int panelWidth = 100;
        int panelHeight = 60;
        guiGraphics.fill(x - 5, y - 5, x + panelWidth, y + panelHeight, COLOR_PANEL_BG);
        
        guiGraphics.drawString(mc.font, Component.literal("▌ PANTSIR-S1"), x, y, COLOR_RADAR_GREEN, false);
        y += 14;
        
        guiGraphics.fill(x, y, x + 90, y + 1, COLOR_RADAR_DARK);
        y += 6;
        
        String stateText;
        int stateColor;
        
        switch (PantsirClientHandler.radarState) {
            case PantsirRadarSyncMessage.STATE_IDLE -> {
                stateText = "SCANNING";
                stateColor = COLOR_RADAR_GREEN;
            }
            case PantsirRadarSyncMessage.STATE_DETECTED -> {
                stateText = "DETECTED";
                stateColor = COLOR_TARGET_YELLOW;
            }
            case PantsirRadarSyncMessage.STATE_LOCKING -> {
                stateText = "LOCKING " + PantsirClientHandler.lockProgress + "%";
                stateColor = COLOR_TARGET_YELLOW;
            }
            case PantsirRadarSyncMessage.STATE_LOCKED -> {
                stateText = "◆ LOCKED";
                stateColor = COLOR_LOCKED;
            }
            case PantsirRadarSyncMessage.STATE_LOST -> {
                stateText = "LOST";
                stateColor = COLOR_TARGET_RED;
            }
            default -> {
                stateText = "---";
                stateColor = COLOR_RADAR_DARK;
            }
        }
        
        guiGraphics.drawString(mc.font, Component.literal(stateText), x, y, stateColor, false);
        y += 12;
        
        if (PantsirClientHandler.radarState == PantsirRadarSyncMessage.STATE_LOCKING) {
            int barWidth = 80;
            int barHeight = 4;
            int progress = PantsirClientHandler.lockProgress;
            int filledWidth = (int)(barWidth * progress / 100.0);
            
            guiGraphics.fill(x, y, x + barWidth, y + barHeight, COLOR_RADAR_DARK);
            guiGraphics.fill(x, y, x + filledWidth, y + barHeight, COLOR_TARGET_YELLOW);
            y += 8;
        }
        
        if (PantsirClientHandler.isTargetDetected()) {
            String distText = String.format("RNG: %.0fm", PantsirClientHandler.targetDistance);
            guiGraphics.drawString(mc.font, Component.literal(distText), x, y, COLOR_RADAR_GREEN, false);
        }
    }

    private static void renderTargetMarker(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        if (PantsirClientHandler.radarState != PantsirRadarSyncMessage.STATE_LOCKED) return;
        
        Vec3 targetPos = new Vec3(
            PantsirClientHandler.targetX,
            PantsirClientHandler.targetY,
            PantsirClientHandler.targetZ
        );
        
        if (!VectorUtil.canSee(targetPos)) return;
        
        Vec3 screenPos = VectorUtil.worldToScreen(targetPos);
        int x = (int) screenPos.x;
        int y = (int) screenPos.y;
        
        drawTargetBrackets(guiGraphics, x, y, COLOR_LOCKED);
        
        Minecraft mc = Minecraft.getInstance();
        String distText = String.format("%.0fm", PantsirClientHandler.targetDistance);
        guiGraphics.drawString(mc.font, Component.literal(distText), x + MARKER_HALF + 5, y - 5, COLOR_LOCKED, false);
        guiGraphics.drawString(mc.font, Component.literal("TRK"), x + MARKER_HALF + 5, y + 5, COLOR_LOCKED, false);
    }
    
    private static void drawTargetBrackets(GuiGraphics guiGraphics, int cx, int cy, int color) {
        int half = MARKER_HALF;
        int corner = 8;
        int thick = 2;
        
        guiGraphics.fill(cx - half, cy - half, cx - half + corner, cy - half + thick, color);
        guiGraphics.fill(cx - half, cy - half, cx - half + thick, cy - half + corner, color);
        guiGraphics.fill(cx + half - corner, cy - half, cx + half, cy - half + thick, color);
        guiGraphics.fill(cx + half - thick, cy - half, cx + half, cy - half + corner, color);
        guiGraphics.fill(cx - half, cy + half - thick, cx - half + corner, cy + half, color);
        guiGraphics.fill(cx - half, cy + half - corner, cx - half + thick, cy + half, color);
        guiGraphics.fill(cx + half - corner, cy + half - thick, cx + half, cy + half, color);
        guiGraphics.fill(cx + half - thick, cy + half - corner, cx + half, cy + half, color);
    }

    private static void renderControlHint(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        
        // Используем кнопку VEHICLE_SEEK из SBW
        String keyName = ModKeyMappings.VEHICLE_SEEK.getKey().getDisplayName().getString();
        String fireKey = ModKeyMappings.FIRE.getKey().getDisplayName().getString();
        String hint = switch (PantsirClientHandler.radarState) {
            case PantsirRadarSyncMessage.STATE_IDLE -> "Radar scanning...";
            case PantsirRadarSyncMessage.STATE_DETECTED -> "[" + keyName + "] Lock target";
            case PantsirRadarSyncMessage.STATE_LOCKING -> "[" + keyName + "] Cancel";
            case PantsirRadarSyncMessage.STATE_LOCKED -> "◆ [" + fireKey + "] FIRE | [" + keyName + "] Release";
            case PantsirRadarSyncMessage.STATE_LOST -> "Target lost";
            default -> "";
        };
        
        int color = switch (PantsirClientHandler.radarState) {
            case PantsirRadarSyncMessage.STATE_LOCKED -> COLOR_LOCKED;
            case PantsirRadarSyncMessage.STATE_DETECTED, PantsirRadarSyncMessage.STATE_LOCKING -> COLOR_TARGET_YELLOW;
            default -> COLOR_RADAR_GREEN;
        };
        
        // Уменьшаем текст в 1.4 раза (scale = 0.71)
        float scale = 0.71f;
        int textWidth = (int)(mc.font.width(hint) * scale);
        int textHeight = (int)(9 * scale);
        int hintX = (screenWidth - textWidth) / 2;
        int hintY = screenHeight - 40;
        
        // Фон для подсказки
        guiGraphics.fill(hintX - 3, hintY - 2, hintX + textWidth + 3, hintY + textHeight + 2, COLOR_PANEL_BG);
        
        poseStack.pushPose();
        poseStack.translate(hintX, hintY, 0);
        poseStack.scale(scale, scale, 1.0f);
        guiGraphics.drawString(mc.font, Component.literal(hint), 0, 0, color, false);
        poseStack.popPose();
    }

    
    // === Drawing helpers ===
    
    private static void drawFilledCircle(PoseStack poseStack, int cx, int cy, int radius, int color) {
        float a = (color >> 24 & 255) / 255.0f;
        float r = (color >> 16 & 255) / 255.0f;
        float g = (color >> 8 & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        
        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, cx, cy, 0).color(r, g, b, a).endVertex();
        
        for (int i = 0; i <= RADAR_SEGMENTS; i++) {
            double angle = 2 * Math.PI * i / RADAR_SEGMENTS;
            float x = cx + (float)(Math.cos(angle) * radius);
            float y = cy + (float)(Math.sin(angle) * radius);
            buffer.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
    }
    
    private static void drawCircleOutline(PoseStack poseStack, int cx, int cy, float radius, int color) {
        float a = (color >> 24 & 255) / 255.0f;
        float r = (color >> 16 & 255) / 255.0f;
        float g = (color >> 8 & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        
        buffer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        
        for (int i = 0; i <= RADAR_SEGMENTS; i++) {
            double angle = 2 * Math.PI * i / RADAR_SEGMENTS;
            float x = cx + (float)(Math.cos(angle) * radius);
            float y = cy + (float)(Math.sin(angle) * radius);
            buffer.vertex(matrix, x, y, 0).color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
    }
    
    private static void drawLine(PoseStack poseStack, int x1, int y1, int x2, int y2, int color, int width) {
        float a = (color >> 24 & 255) / 255.0f;
        float r = (color >> 16 & 255) / 255.0f;
        float g = (color >> 8 & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(width);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }
    
    private static void drawTriangle(PoseStack poseStack, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        float a = (color >> 24 & 255) / 255.0f;
        float r = (color >> 16 & 255) / 255.0f;
        float g = (color >> 8 & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x3, y3, 0).color(r, g, b, a).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }
    
    /**
     * Рисует пунктирную линию
     * @param dashLength длина штриха
     * @param gapLength длина промежутка
     */
    private static void drawDashedLine(PoseStack poseStack, int x1, int y1, int x2, int y2, int color, int dashLength, int gapLength) {
        float a = (color >> 24 & 255) / 255.0f;
        float r = (color >> 16 & 255) / 255.0f;
        float g = (color >> 8 & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        
        if (length < 1) return;
        
        double unitX = dx / length;
        double unitY = dy / length;
        
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(2);
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        Matrix4f matrix = poseStack.last().pose();
        
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        double pos = 0;
        boolean drawing = true;
        
        while (pos < length) {
            if (drawing) {
                double startX = x1 + unitX * pos;
                double startY = y1 + unitY * pos;
                double endPos = Math.min(pos + dashLength, length);
                double endX = x1 + unitX * endPos;
                double endY = y1 + unitY * endPos;
                
                buffer.vertex(matrix, (float)startX, (float)startY, 0).color(r, g, b, a).endVertex();
                buffer.vertex(matrix, (float)endX, (float)endY, 0).color(r, g, b, a).endVertex();
                
                pos = endPos;
            } else {
                pos += gapLength;
            }
            drawing = !drawing;
        }
        
        BufferUploader.drawWithShader(buffer.end());
    }
}
