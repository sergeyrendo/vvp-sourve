package tech.vvp.vvp.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M224Entity;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class M224InfoOverlay implements IGuiOverlay {

    public static final String ID = VVP.MOD_ID + "_m224_info";

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Player player = gui.getMinecraft().player;
        Entity lookingEntity = null;
        if (player != null) {
            lookingEntity = TraceTool.findLookingEntity(player, 6);
        }
        if (lookingEntity instanceof M224Entity mortar) {
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.vvp.m224.pitch")
                            .append(Component.literal(FormatTool.format1D(-mortar.getXRot(), "°"))),
                    screenWidth / 2 - 90, screenHeight / 2 - 26, -1, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.vvp.m224.yaw")
                            .append(Component.literal(FormatTool.format1D(mortar.getYRot(), "°"))),
                    screenWidth / 2 - 90, screenHeight / 2 - 16, -1, false);
            guiGraphics.drawString(Minecraft.getInstance().font, Component.translatable("tips.vvp.m224.range")
                            .append(Component.literal(FormatTool.format1D((int) RangeTool.getRange(-mortar.getXRot(), mortar.shootVelocity(), mortar.projectileGravity()), "m"))),
                    screenWidth / 2 - 90, screenHeight / 2 - 6, -1, false);
        }
    }
}
