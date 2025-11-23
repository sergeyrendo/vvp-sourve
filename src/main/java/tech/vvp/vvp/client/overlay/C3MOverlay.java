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
import tech.vvp.vvp.entity.vehicle.C3MEntity;

@OnlyIn(Dist.CLIENT)
public class C3MOverlay implements IGuiOverlay {

    public static final String ID = VVP.MOD_ID + "_2c3m_targeting_hud";

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;

        if (player == null || !(player.getVehicle() instanceof C3MEntity c3m)) {
            return;
        }

        // Проверяем что игрок водитель
        if (c3m.getSeatIndex(player) != 0) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // Получаем данные
        C3MEntity.OperationMode mode = c3m.getOperationMode();
        float pitch = c3m.getTurretXRot();
        float yaw = c3m.getTurretYRot();
        
        // Расчет дальности (упрощенный)
        double range = calculateRange(pitch);

        // Цвета
        int modeColor = mode == C3MEntity.OperationMode.FIRING ? 0x00FF00 : 0xFFA500; // Зеленый или оранжевый
        int textColor = mode == C3MEntity.OperationMode.FIRING ? 0x00FF00 : 0x808080; // Зеленый или серый

        // Позиция оверлея (справа вверху)
        int overlayWidth = 200;
        int x = screenWidth - overlayWidth - 10;
        int y = 10;

        // Рисуем рамку
        guiGraphics.fill(x, y, x + 200, y + 110, 0x80000000); // Полупрозрачный черный фон
        guiGraphics.fill(x, y, x + 200, y + 2, 0xFF00FF00); // Зеленая верхняя граница
        guiGraphics.fill(x, y, x + 2, y + 110, 0xFF00FF00); // Зеленая левая граница
        guiGraphics.fill(x + 198, y, x + 200, y + 110, 0xFF00FF00); // Зеленая правая граница
        guiGraphics.fill(x, y + 108, x + 200, y + 110, 0xFF00FF00); // Зеленая нижняя граница

        // Заголовок
        guiGraphics.drawString(mc.font, "ARTILLERY SYSTEM", x + 10, y + 5, 0xFFFFFF);
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

        // Статус зарядки
        String loadedShell = c3m.getEntityData().get(C3MEntity.LOADED_SHELL);
        boolean isLoaded = !loadedShell.equals("null");
        String ammoText = "Ammo: " + (isLoaded ? "§aREADY" : "§cEMPTY");
        guiGraphics.drawString(mc.font, ammoText, x + 10, y + 77, isLoaded ? 0x00FF00 : 0xFF0000);

        // Время перезарядки
        if (c3m.reloadCoolDown > 0) {
            int reloadSeconds = c3m.reloadCoolDown / 20;
            String reloadText = String.format("Reload: %ds", reloadSeconds);
            guiGraphics.drawString(mc.font, reloadText, x + 10, y + 90, 0xFFFF00);
        }

        // Подсказка управления
        String hint = mode == C3MEntity.OperationMode.FIRING ? "LMB: Fire" : "Toggle Mode";
        int hintColor = mode == C3MEntity.OperationMode.FIRING ? 0x00FF00 : 0xFFFFFF;
        int hintWidth = mc.font.width(hint);
        guiGraphics.drawString(mc.font, hint, x + 100 - hintWidth / 2, y + 103, hintColor);

        RenderSystem.disableBlend();
    }

    // Упрощенный расчет дальности на основе угла подъема
    private double calculateRange(float pitch) {
        // Формула баллистики: R = (v² * sin(2θ)) / g
        // Упрощенная версия для игры
        double velocity = 100.0; // Начальная скорость снаряда
        double gravity = 9.8;
        double angleRad = Math.toRadians(pitch);
        
        // Защита от деления на ноль (pitch отрицательный = вверх, от -60 до 0)
        if (pitch > 0 || pitch < -90) return 0;
        
        // Используем абсолютное значение для расчета
        double range = (velocity * velocity * Math.sin(2 * Math.abs(angleRad))) / gravity;
        // Максимальная дальность 600 блоков
        return Math.max(0, Math.min(range, 600.0));
    }
}

