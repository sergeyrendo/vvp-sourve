package tech.vvp.vvp.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;

@OnlyIn(Dist.CLIENT)
public class HimarsOverlay implements IGuiOverlay {

    public static final String ID = VVP.MOD_ID + "_himars_targeting_hud";

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;

        if (player == null || !(player.getVehicle() instanceof M142HimarsEntity himars)) {
            return;
        }

        // Проверяем что игрок водитель
        if (himars.getSeatIndex(player) != 0) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // Получаем данные
        M142HimarsEntity.OperationMode mode = himars.getOperationMode();
        float pitch = himars.getTurretXRot();
        float yaw = himars.getTurretYRot();
        
        // Расчет дальности (упрощенный)
        double range = calculateRange(pitch);

        // Цвета
        int modeColor = mode == M142HimarsEntity.OperationMode.FIRING ? 0x00FF00 : 0xFFA500; // Зеленый или оранжевый
        int textColor = mode == M142HimarsEntity.OperationMode.FIRING ? 0x00FF00 : 0x808080; // Зеленый или серый

        // Позиция оверлея (справа вверху)
        int overlayWidth = 200;
        int x = screenWidth - overlayWidth - 10;
        int y = 10;

        // Рисуем рамку
        guiGraphics.fill(x, y, x + 200, y + 90, 0x80000000); // Полупрозрачный черный фон
        guiGraphics.fill(x, y, x + 200, y + 2, 0xFF00FF00); // Зеленая верхняя граница
        guiGraphics.fill(x, y, x + 2, y + 90, 0xFF00FF00); // Зеленая левая граница
        guiGraphics.fill(x + 198, y, x + 200, y + 90, 0xFF00FF00); // Зеленая правая граница
        guiGraphics.fill(x, y + 88, x + 200, y + 90, 0xFF00FF00); // Зеленая нижняя граница

        // Заголовок
        guiGraphics.drawString(mc.font, "TARGETING SYSTEM", x + 10, y + 5, 0xFFFFFF);
        guiGraphics.fill(x + 5, y + 18, x + 195, y + 19, 0xFF00FF00); // Разделитель

        // Режим
        String modeText = "MODE: " + mode.name();
        guiGraphics.drawString(mc.font, modeText, x + 10, y + 25, modeColor);

        // Pitch (показываем абсолютное значение, т.к. отрицательный = вверх)
        String pitchText = String.format("Pitch: %6.1f°", Math.abs(pitch));
        guiGraphics.drawString(mc.font, pitchText, x + 10, y + 38, textColor);

        // Yaw
        String yawText = String.format("Yaw:   %6.1f°", Mth.wrapDegrees(yaw));
        guiGraphics.drawString(mc.font, yawText, x + 10, y + 51, textColor);

        // Range
        String rangeText = String.format("Range: %4.0fm", range);
        guiGraphics.drawString(mc.font, rangeText, x + 10, y + 64, textColor);

        // Подсказка управления
        String hint = mode == M142HimarsEntity.OperationMode.FIRING ? "LMB: Fire" : "Toggle Mode";
        int hintColor = mode == M142HimarsEntity.OperationMode.FIRING ? 0x00FF00 : 0xFFFFFF;
        int hintWidth = mc.font.width(hint);
        guiGraphics.drawString(mc.font, hint, x + 100 - hintWidth / 2, y + 77, hintColor);

        RenderSystem.disableBlend();
    }

    // Упрощенный расчет дальности на основе угла подъема
    private double calculateRange(float pitch) {
        // Формула баллистики: R = (v² * sin(2θ)) / g
        // Упрощенная версия для игры
        double velocity = 100.0; // Начальная скорость ракеты
        double gravity = 9.8;
        double angleRad = Math.toRadians(pitch);
        
        // Защита от деления на ноль (pitch отрицательный = вверх, от -60 до 0)
        if (pitch > 0 || pitch < -90) return 0;
        
        // Используем абсолютное значение для расчета
        double range = (velocity * velocity * Math.sin(2 * Math.abs(angleRad))) / gravity;
        return Math.max(0, Math.min(range, 9999)); // Ограничиваем 0-9999м
    }
}
