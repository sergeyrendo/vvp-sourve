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

/**
 * Круговое меню выбора места в технике
 */
public class RadialSeatScreen extends Screen {
    
    private static final int INNER_RADIUS = 60;
    private static final int OUTER_RADIUS = 130;
    private static final int SEGMENTS = 32; // Сегментов на сектор для гладкости
    
    private final VehicleEntity vehicle;
    private final int currentSeat;
    private final List<SeatInfo> seats = new ArrayList<>();
    private int hoveredSeat = -1;
    
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
        if (index == 0) return "Pilot";
        if (index == 1 && maxSeats > 2) return "Co-Pilot";
        if (index == 1) return "Passenger";
        return "Seat " + (index + 1);
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Полупрозрачный фон
        graphics.fill(0, 0, width, height, 0x90000000);
        
        int centerX = width / 2;
        int centerY = height / 2;
        
        hoveredSeat = getHoveredSeat(mouseX, mouseY, centerX, centerY);
        
        // Тень под кругом (несколько слоёв для мягкости)
        drawFilledCircle(graphics, centerX + 4, centerY + 4, OUTER_RADIUS + 15, 0x40000000);
        drawFilledCircle(graphics, centerX + 3, centerY + 3, OUTER_RADIUS + 12, 0x50000000);
        drawFilledCircle(graphics, centerX + 2, centerY + 2, OUTER_RADIUS + 8, 0x60000000);
        
        // Фон круга
        drawFilledCircle(graphics, centerX, centerY, OUTER_RADIUS + 5, 0xF0181818);
        drawCircleOutline(graphics, centerX, centerY, OUTER_RADIUS + 5, 0xFF444444, 3.0f);
        
        // Рисуем секторы
        renderSectors(graphics, centerX, centerY);
        
        // Центральный круг
        renderCenter(graphics, centerX, centerY);
        
        // Подсказка снизу
        renderTooltip(graphics, centerX, centerY);
        
        // Подсказка закрытия
        String hint = "[X] Close  |  [1-9] Quick Select";
        int hintWidth = font.width(hint);
        graphics.drawString(font, hint, centerX - hintWidth / 2, height - 30, 0x80FFFFFF, false);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    private void renderSectors(GuiGraphics graphics, int centerX, int centerY) {
        if (seats.isEmpty()) return;
        
        int seatCount = seats.size();
        float angleStep = 360f / seatCount;
        float gapAngle = 2f; // Зазор между секторами
        
        for (int i = 0; i < seatCount; i++) {
            SeatInfo seat = seats.get(i);
            float startAngle = i * angleStep - 90 + gapAngle / 2;
            float endAngle = (i + 1) * angleStep - 90 - gapAngle / 2;
            
            // Цвета
            int fillColor;
            int borderColor;
            if (seat.isCurrentPlayer) {
                fillColor = 0xA000AA00;  // Зелёный
                borderColor = 0xFF00FF00;
            } else if (seat.occupied) {
                fillColor = 0xA0AA0000;  // Красный
                borderColor = 0xFFFF0000;
            } else if (i == hoveredSeat) {
                fillColor = 0xA0AAAA00;  // Жёлтый при наведении
                borderColor = 0xFFFFFF00;
            } else {
                fillColor = 0x60333333;  // Серый
                borderColor = 0xFF666666;
            }
            
            // Заливка сектора
            drawFilledArc(graphics, centerX, centerY, INNER_RADIUS, OUTER_RADIUS, startAngle, endAngle, fillColor);
            
            // Контур
            drawArcOutline(graphics, centerX, centerY, INNER_RADIUS, startAngle, endAngle, borderColor);
            drawArcOutline(graphics, centerX, centerY, OUTER_RADIUS, startAngle, endAngle, borderColor);
            
            // Номер места
            float midAngle = (float) Math.toRadians((startAngle + endAngle) / 2);
            int textRadius = (INNER_RADIUS + OUTER_RADIUS) / 2;
            int textX = centerX + (int)(Math.cos(midAngle) * textRadius);
            int textY = centerY + (int)(Math.sin(midAngle) * textRadius);
            
            String num = String.valueOf(i + 1);
            graphics.drawCenteredString(font, num, textX, textY - 4, 0xFFFFFFFF);
        }
    }
    
    private void drawFilledArc(GuiGraphics graphics, int cx, int cy, int innerR, int outerR,
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
        
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = (float) Math.toRadians(startAngle + (endAngle - startAngle) * i / SEGMENTS);
            float cos = (float) Math.cos(angle);
            float sin = (float) Math.sin(angle);
            
            buffer.vertex(matrix, cx + cos * innerR, cy + sin * innerR, 0).color(r, g, b, a).endVertex();
            buffer.vertex(matrix, cx + cos * outerR, cy + sin * outerR, 0).color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void drawArcOutline(GuiGraphics graphics, int cx, int cy, int radius,
                                float startAngle, float endAngle, int color) {
        drawArcOutline(graphics, cx, cy, radius, startAngle, endAngle, color, 3.0f);
    }
    
    private void drawArcOutline(GuiGraphics graphics, int cx, int cy, int radius,
                                float startAngle, float endAngle, int color, float lineWidth) {
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
        
        for (int i = 0; i <= SEGMENTS; i++) {
            float angle = (float) Math.toRadians(startAngle + (endAngle - startAngle) * i / SEGMENTS);
            buffer.vertex(matrix, cx + (float)Math.cos(angle) * radius, cy + (float)Math.sin(angle) * radius, 0)
                  .color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void renderCenter(GuiGraphics graphics, int centerX, int centerY) {
        // Тень центрального круга
        drawFilledCircle(graphics, centerX + 2, centerY + 2, INNER_RADIUS - 8, 0x60000000);
        
        // Круглый центр
        drawFilledCircle(graphics, centerX, centerY, INNER_RADIUS - 10, 0xF0151515);
        drawCircleOutline(graphics, centerX, centerY, INNER_RADIUS - 10, 0xFF666666, 3.0f);
        
        // Текст
        String title = "SEAT SELECT";
        graphics.drawCenteredString(font, title, centerX, centerY - 12, 0xFFFFFFFF);
        
        String current = "Current: " + (currentSeat + 1);
        graphics.drawCenteredString(font, current, centerX, centerY + 2, 0xFF88FF88);
    }
    
    private void drawFilledCircle(GuiGraphics graphics, int cx, int cy, int radius, int color) {
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
        
        for (int i = 0; i <= 32; i++) {
            float angle = (float) (i * Math.PI * 2 / 32);
            buffer.vertex(matrix, cx + (float)Math.cos(angle) * radius, cy + (float)Math.sin(angle) * radius, 0)
                  .color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void drawCircleOutline(GuiGraphics graphics, int cx, int cy, int radius, int color) {
        drawCircleOutline(graphics, cx, cy, radius, color, 3.0f);
    }
    
    private void drawCircleOutline(GuiGraphics graphics, int cx, int cy, int radius, int color, float lineWidth) {
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
        
        for (int i = 0; i <= 32; i++) {
            float angle = (float) (i * Math.PI * 2 / 32);
            buffer.vertex(matrix, cx + (float)Math.cos(angle) * radius, cy + (float)Math.sin(angle) * radius, 0)
                  .color(r, g, b, a).endVertex();
        }
        
        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.disableBlend();
    }
    
    private void renderTooltip(GuiGraphics graphics, int centerX, int centerY) {
        if (hoveredSeat >= 0 && hoveredSeat < seats.size()) {
            SeatInfo seat = seats.get(hoveredSeat);
            
            String status;
            int statusColor;
            if (seat.isCurrentPlayer) {
                status = "YOUR SEAT";
                statusColor = 0xFF00FF00;
            } else if (seat.occupied) {
                status = "OCCUPIED";
                statusColor = 0xFFFF4444;
            } else {
                status = "AVAILABLE - Click to switch";
                statusColor = 0xFFFFFF00;
            }
            
            int tooltipY = centerY + OUTER_RADIUS + 25;
            graphics.drawCenteredString(font, seat.name, centerX, tooltipY, 0xFFFFFFFF);
            graphics.drawCenteredString(font, status, centerX, tooltipY + 14, statusColor);
        }
    }
    
    private int getHoveredSeat(int mouseX, int mouseY, int centerX, int centerY) {
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance < INNER_RADIUS || distance > OUTER_RADIUS) {
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
        // Закрытие по X
        if (ModKeyMappings.SEAT_SELECTOR.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        
        // Цифры 1-9
        if (keyCode >= 49 && keyCode <= 57) {
            int seatIndex = keyCode - 49;
            if (seatIndex < seats.size()) {
                SeatInfo seat = seats.get(seatIndex);
                if (!seat.occupied || seat.isCurrentPlayer) {
                    if (!seat.isCurrentPlayer) {
                        VVPNetwork.VVP_HANDLER.send(
                            PacketDistributor.SERVER.noArg(),
                            new SeatSwapMessage(seatIndex)
                        );
                    }
                    onClose();
                    return true;
                }
            }
        }
        
        // 0 для места 10
        if (keyCode == 48 && seats.size() >= 10) {
            SeatInfo seat = seats.get(9);
            if (!seat.occupied || seat.isCurrentPlayer) {
                if (!seat.isCurrentPlayer) {
                    VVPNetwork.VVP_HANDLER.send(
                        PacketDistributor.SERVER.noArg(),
                        new SeatSwapMessage(9)
                    );
                }
                onClose();
                return true;
            }
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
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
