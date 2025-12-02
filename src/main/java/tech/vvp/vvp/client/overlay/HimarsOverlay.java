package tech.vvp.vvp.client.overlay;

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
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        if (!(player.getVehicle() instanceof M142HimarsEntity himars)) return;
        if (himars.getFirstPassenger() != player) return;

        var font = mc.font;

        // Режим работы
        boolean firingMode = himars.isFiringMode();
        String modeText = firingMode ? "§c[FIRING MODE]" : "§a[DRIVING MODE]";
        guiGraphics.drawString(font, Component.literal(modeText), 10, 10, 0xFFFFFF, true);
        guiGraphics.drawString(font, Component.literal("§7Press Q to toggle mode"), 10, 22, 0xAAAAAA, false);

        // Ammo
        int ammo = himars.getMissileAmmo();
        guiGraphics.drawString(font, Component.literal("§eAmmo: " + ammo + "/6"), 10, 40, 0xFFFFFF, true);

        if (!firingMode) return;

        // Информация о наведении
        guiGraphics.drawString(font, Component.literal("§7Press G to set target coordinates"), 10, 58, 0xAAAAAA, false);

        if (!himars.hasGuidanceData()) {
            guiGraphics.drawString(font, Component.literal("§cNo target set"), 10, 76, 0xFF5555, true);
            return;
        }

        // Углы наведения
        float requiredYaw = himars.getGuidanceYaw();
        float requiredPitch = -himars.getGuidancePitch();
        float currentYaw = himars.getTurretYRot();
        float currentPitch = -himars.getTurretXRot();

        float yawError = Math.abs(Mth.wrapDegrees(requiredYaw - currentYaw));
        float pitchError = Math.abs(requiredPitch - currentPitch);

        boolean aligned = yawError <= 1.5f && pitchError <= 1.5f;
        boolean canFire = himars.getTurretXRot() <= -18f;

        int diffColor = aligned && canFire ? 0x00FF00 : (aligned ? 0xFFFF00 : 0xFFAA00);

        int startX = screenWidth - 200;
        int startY = 30;

        guiGraphics.drawString(font, Component.literal(String.format("Target: Y %.1f° | P %.1f°", requiredYaw, requiredPitch)), startX, startY, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.literal(String.format("Turret: Y %.1f° | P %.1f°", currentYaw, currentPitch)), startX, startY + 12, 0xFFFFFF, false);
        guiGraphics.drawString(font, Component.literal(String.format("Offset: ΔY %.1f° | ΔP %.1f°", yawError, pitchError)), startX, startY + 24, diffColor, false);

        String statusText;
        if (!canFire) {
            statusText = "§c⚠ MIN PITCH 18°";
        } else if (aligned) {
            statusText = "§a✓ READY TO FIRE";
        } else {
            statusText = "§e⟳ ALIGN TURRET";
        }

        guiGraphics.drawString(font, Component.literal(statusText), startX, startY + 40, 0xFFFFFF, true);
    }
}
