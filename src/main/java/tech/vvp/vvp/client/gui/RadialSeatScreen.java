package tech.vvp.vvp.client.gui;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Matrix4f;
import tech.vvp.vvp.init.ModKeyMappings;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.SeatSwapMessage;

import java.util.ArrayList;
import java.util.List;

public class RadialSeatScreen extends Screen {
    
    private static final int INNER_RADIUS = 45;
    private static final int OUTER_RADIUS = 115;
    
    // Военный зелёный HUD цвет
    private static final int HUD_GREEN = 0xFF40FF40;
    private static final int HUD_GREEN_DIM = 0xFF208020;
    private static final int HUD_GREEN_DARK = 0xFF103010;
    private static final int HUD_RED = 0xFFFF4040;
    private static final int HUD_YELLOW = 0xFFFFFF40;
    
    private final VehicleEntity vehicle;
    private final int currentSeat;
    private final List<SeatInfo> seats = new ArrayList<>();
    private int hoveredSeat = -1;
    private float animProgress = 0f;
    private int tickCount = 0;
    
    public RadialSeatScreen(VehicleEntity vehicle, int currentSeat) {
        super(Component.translatable("gui.vvp.seat_selector"));
        this.vehicle = vehicle;
        this.currentSeat = currentSeat;
        
        int maxSeats = vehicle.getMaxPassengers();
        List<Entity> passengers = vehicle.getOrderedPassengers();
        for (int i = 0; i < maxSeats; i++) {
            Entity passenger = (i < passengers.size()) ? passengers.get(i) : null;
            boolean occupied = passenger != null;
            boolean isCurrentPlayer = passenger == Minecraft.getInstance().player;
            String name = getSeatName(i, maxSeats);
            seats.add(new SeatInfo(i, name, occupied, isCurrentPlayer));
        }
    }
    
    private String getSeatName(int index, int maxSeats) {
        if (index == 0) return "PILOT";
        if (index == 1 && maxSeats > 2) return "CO-PILOT";
        if (index == 1) return "PASSENGER";
        return "SEAT-" + String.format("%02d", index + 1);
    }
    
    @Override
    public void tick() {
        super.tick();
        tickCount++;
        if (animProgress < 1f) {
            animProgress = Math.min(1f, animProgress + 0.15f);
        }
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        float anim = easeOutQuad(animProgress);
        
        // Тёмный фон с лёгким зелёным оттенком
        graphics.fill(0, 0, width, height, 0xCC001005);
        
        // Сканлайны эффект
        renderScanlines(graphics);
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        hoveredSeat = getHoveredSeat(mouseX, mouseY, centerX, centerY);
        
        int outerR = (int)(OUTER_RADIUS * anim);
        int innerR = (int)(INNER_RADIUS * anim);
        
        // Внешние декоративные кольца
        drawCircleOutline(graphics, centerX, centerY, outerR + 20, HUD_GREEN_DARK, 1f);
        drawCircleOutline(graphics, centerX, centerY, outerR + 15, HUD_GREEN_DIM, 1f);
        
        // Угловые маркеры
        renderCornerMarkers(graphics, centerX, centerY, outerR + 25);
        
        // Основное кольцо
        drawCircleOutline(graphics, centerX, centerY, outerR, HUD_GREEN, 2f);
        drawCircleOutline(graphics, centerX, centerY, innerR, HUD_GREEN, 2f);
        
        // Секторы
        if (anim > 0.2f) {
            renderSectors(graphics, centerX, centerY, innerR, outerR);
        }
        
        // Центральный дисплей
        renderCenterDisplay(graphics, centerX, centerY, innerR, anim);
        
        // Tooltip
        if (hoveredSeat >= 0 && hoveredSeat < seats.size() && anim > 0.5f) {
            renderTooltip(graphics, centerX, centerY, outerR);
        }
        
        // Нижняя панель с инструкциями
        renderBottomPanel(graphics, centerX);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    private float easeOutQuad(float x) {
        return 1 - (1 - x) * (1 - x);
    }
    
    private void renderScanlines(GuiGraphics graphics) {
        // Горизонтальные линии для эффекта старого монитора
        for (int y = 0; y < height; y += 4) {
            graphics.fill(0, y, width, y + 1, 0x10FFFFFF);
        }
    }
    
    private void renderCornerMarkers(GuiGraphics graphics, int cx, int cy, int radius) {
        // Маркеры по углам как на военных дисплеях
        int markerLen = 15;
        int[] angles = {45, 135, 225, 315};
        
        for (int angle : angles) {
            double rad = Math.toRadians(angle);
            int x = cx + (int)(Math.cos(rad) * radius);
            int y = cy + (int)(Math.sin(rad) * radius);
            
            // Маленький крестик
            drawLine(graphics, x - 5, y, x + 5, y, HUD_GREEN_DIM);
            drawLine(graphics, x, y - 5, x, y + 5, HUD_GREEN_DIM);
        }
    }
    
    private void renderSectors(GuiGraphics graphics, int centerX, int centerY, int innerR, int outerR) {
        if (seats.isEmpty()) return;
        
        int seatCount = seats.size();
        float angleStep = 360f / seatCount;
        
        for (int i = 0; i < seatCount; i++) {
            SeatInfo seat = seats.get(i);
            float startAngle = i * angleStep - 90;
            float endAngle = (i + 1) * angleStep - 90;
            float midAngle = (startAngle + endAngle) / 2;
            
            // Разделительные линии
            float lineAngle = (float) Math.toRadians(startAngle);
            int x1 = centerX + (int)(Math.cos(lineAngle) * innerR);
            int y1 = centerY + (int)(Math.sin(lineAngle) * innerR);
            int x2 = centerX + (int)(Math.cos(lineAngle) * outerR);
            int y2 = centerY + (int)(Math.sin(lineAngle) * outerR);
            drawLine(graphics, x1, y1, x2, y2, HUD_GREEN_DIM);
            
            // Подсветка сектора
            int sectorColor = 0;
            if (seat.isCurrentPlayer) {
                sectorColor = 0x40208020; // Зелёная подсветка
            } else if (seat.occupied) {
                sectorColor = 0x40802020; // Красная подсветка
            } else if (i == hoveredSeat) {
                sectorColor = 0x40404020; // Жёлтая подсветка
            }
            
            if (sectorColor != 0) {
                drawSector(graphics, centerX, centerY, innerR + 2, outerR - 2, startAngle + 1, endAngle - 1, sectorColor);
            }
            
            // Номер места
            float textAngle = (float) Math.toRadians(midAngle);
            int textR = (innerR + outerR) / 2;
            int tx = centerX + (int)(Math.cos(textAngle) * textR);
            int ty = centerY + (int)(Math.sin(textAngle) * textR);
            
            int textColor;
            String prefix = "";
            if (seat.isCurrentPlayer) {
                textColor = HUD_GREEN;
                prefix = ">";
            } else if (seat.occupied) {
                textColor = HUD_RED;
                prefix = "X";
            } else if (i == hoveredSeat) {
                textColor = HUD_YELLOW;
                prefix = "";
            } else {
                textColor = HUD_GREEN_DIM;
                prefix = "";
            }
            
            String text = prefix.isEmpty() ? String.valueOf(i + 1) : prefix + (i + 1);
            graphics.drawCenteredString(font, text, tx, ty - 4, textColor);
            
            // Статус индикатор на внешнем крае
            int indicatorR = outerR - 8;
            int ix = centerX + (int)(Math.cos(textAngle) * indicatorR);
            int iy = centerY + (int)(Math.sin(textAngle) * indicatorR);
            
            if (seat.isCurrentPlayer) {
                graphics.drawCenteredString(font, "■", ix, iy - 4, HUD_GREEN);
            } else if (seat.occupied) {
                graphics.drawCenteredString(font, "■", ix, iy - 4, HUD_RED);
            } else {
                graphics.drawCenteredString(font, "□", ix, iy - 4, HUD_GREEN_DIM);
            }
        }
    }
    
    private void renderCenterDisplay(GuiGraphics graphics, int cx, int cy, int innerR, float anim) {
        // Фон центра
        drawFilledCircle(graphics, cx, cy, innerR - 5, 0xE0001005);
        
        if (anim > 0.4f) {
            // Заголовок
            graphics.drawCenteredString(font, "§l[SEAT SELECT]", cx, cy - 16, HUD_GREEN);
            
            // Текущее место
            String current = "POS: " + String.format("%02d", currentSeat + 1);
            graphics.drawCenteredString(font, current, cx, cy, HUD_GREEN_DIM);
            
            // Мигающий курсор
            if ((tickCount / 10) % 2 == 0) {
                graphics.drawCenteredString(font, "_", cx + 20, cy, HUD_GREEN);
            }
        }
    }
    
    private void renderTooltip(GuiGraphics graphics, int cx, int cy, int outerR) {
        SeatInfo seat = seats.get(hoveredSeat);
        
        int boxY = cy + outerR + 30;
        int boxW = 120;
        int boxH = 35;
        int boxX = cx - boxW / 2;
        
        // Рамка в стиле HUD
        graphics.fill(boxX, boxY, boxX + boxW, boxY + boxH, 0xE0001005);
        drawRect(graphics, boxX, boxY, boxW, boxH, HUD_GREEN_DIM);
        
        // Уголки
        int cornerSize = 5;
        // Верхний левый
        drawLine(graphics, boxX, boxY, boxX + cornerSize, boxY, HUD_GREEN);
        drawLine(graphics, boxX, boxY, boxX, boxY + cornerSize, HUD_GREEN);
        // Верхний правый
        drawLine(graphics, boxX + boxW - cornerSize, boxY, boxX + boxW, boxY, HUD_GREEN);
        drawLine(graphics, boxX + boxW, boxY, boxX + boxW, boxY + cornerSize, HUD_GREEN);
        // Нижний левый
        drawLine(graphics, boxX, boxY + boxH - cornerSize, boxX, boxY + boxH, HUD_GREEN);
        drawLine(graphics, boxX, boxY + boxH, boxX + cornerSize, boxY + boxH, HUD_GREEN);
        // Нижний правый
        drawLine(graphics, boxX + boxW, boxY + boxH - cornerSize, boxX + boxW, boxY + boxH, HUD_GREEN);
        drawLine(graphics, boxX + boxW - cornerSize, boxY + boxH, boxX + boxW, boxY + boxH, HUD_GREEN);
        
        // Текст
        graphics.drawCenteredString(font, seat.name, cx, boxY + 6, HUD_GREEN);
        
        String status;
        int statusColor;
        if (seat.isCurrentPlayer) {
            status = "[CURRENT]";
            statusColor = HUD_GREEN;
        } else if (seat.occupied) {
            status = "[OCCUPIED]";
            statusColor = HUD_RED;
        } else {
            status = "[AVAILABLE]";
            statusColor = HUD_YELLOW;
        }
        graphics.drawCenteredString(font, status, cx, boxY + 20, statusColor);
    }
    
    private void renderBottomPanel(GuiGraphics graphics, int cx) {
        int y = height - 30;
        String hint = "[X] CLOSE  |  [1-9] SELECT  |  [LMB] CONFIRM";
        graphics.drawCenteredString(font, hint, cx, y, HUD_GREEN_DIM);
    }
    
    private void drawFilledCircle(GuiGraphics graphics, int cx, int cy, int radius, int color) {
        if (radius <= 0) return;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        
        Matrix4f matrix = graphics.pose().last().pose();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        
        buffer.vertex(matrix, cx, cy, 0).color(r, g, b, a).endVertex();
        for (int i = 0; i <= 60; i++) {
            float angle = (float)(i * Math.PI * 2 / 60);
            buffer.vertex(matrix, cx + (float)Math.cos(angle) * radius, cy + (float)Math.sin(angle) * radius, 0)
                  .color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void drawCircleOutline(GuiGraphics graphics, int cx, int cy, int radius, int color, float lineWidth) {
        if (radius <= 0) return;
        
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(lineWidth);
        
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        
        Matrix4f matrix = graphics.pose().last().pose();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        
        for (int i = 0; i <= 60; i++) {
            float angle = (float)(i * Math.PI * 2 / 60);
            buffer.vertex(matrix, cx + (float)Math.cos(angle) * radius, cy + (float)Math.sin(angle) * radius, 0)
                  .color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void drawSector(GuiGraphics graphics, int cx, int cy, int innerR, int outerR,
                           float startAngle, float endAngle, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        
        Matrix4f matrix = graphics.pose().last().pose();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        
        int segments = 20;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) Math.toRadians(startAngle + (endAngle - startAngle) * i / segments);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            buffer.vertex(matrix, cx + cos * innerR, cy + sin * innerR, 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, cx + cos * outerR, cy + sin * outerR, 0).color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void drawLine(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(1.5f);
        
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        
        Matrix4f matrix = graphics.pose().last().pose();
        float a = ((color >> 24) & 0xFF) / 255f;
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a).endVertex();
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void drawRect(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        drawLine(graphics, x, y, x + w, y, color);
        drawLine(graphics, x + w, y, x + w, y + h, color);
        drawLine(graphics, x + w, y + h, x, y + h, color);
        drawLine(graphics, x, y + h, x, y, color);
    }
    
    private int getHoveredSeat(int mouseX, int mouseY, int centerX, int centerY) {
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        int innerR = (int)(INNER_RADIUS * animProgress);
        int outerR = (int)(OUTER_RADIUS * animProgress);
        
        if (distance < innerR || distance > outerR) {
            return -1;
        }
        
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        
        float angleStep = 360f / seats.size();
        return (int)(angle / angleStep) % seats.size();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredSeat >= 0 && hoveredSeat < seats.size()) {
            SeatInfo seat = seats.get(hoveredSeat);
            if (!seat.occupied || seat.isCurrentPlayer) {
                if (!seat.isCurrentPlayer) {
                    VVPNetwork.VVP_HANDLER.send(
                        PacketDistributor.SERVER.noArg(),
                        new SeatSwapMessage(hoveredSeat)
                    );
                }
                onClose();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (ModKeyMappings.SEAT_SELECTOR.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        
        if (keyCode >= 49 && keyCode <= 57) {
            trySelectSeat(keyCode - 49);
            return true;
        }
        
        if (keyCode == 48 && seats.size() >= 10) {
            trySelectSeat(9);
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void trySelectSeat(int seatIndex) {
        if (seatIndex >= 0 && seatIndex < seats.size()) {
            SeatInfo seat = seats.get(seatIndex);
            if (!seat.occupied || seat.isCurrentPlayer) {
                if (!seat.isCurrentPlayer) {
                    VVPNetwork.VVP_HANDLER.send(
                        PacketDistributor.SERVER.noArg(),
                        new SeatSwapMessage(seatIndex)
                    );
                }
                onClose();
            }
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        
        Entity vehicle = player.getVehicle();
        if (!(vehicle instanceof VehicleEntity vehicleEntity)) return;
        
        int currentSeat = vehicleEntity.getSeatIndex(player);
        mc.setScreen(new RadialSeatScreen(vehicleEntity, currentSeat));
    }
    
    private record SeatInfo(int index, String name, boolean occupied, boolean isCurrentPlayer) {}
}
