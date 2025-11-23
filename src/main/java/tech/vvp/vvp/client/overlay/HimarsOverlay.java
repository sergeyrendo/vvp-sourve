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
        // HUD removed as per request
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
