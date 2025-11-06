package tech.vvp.vvp.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.entity.vehicle.base.ArmedVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.atsuishio.superbwarfare.tools.VectorUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Mi28_1Entity;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class Mi28_1Overlay implements IGuiOverlay {
    public static final String ID = VVP.MOD_ID + "_mi28_1_hud";

    // оставляем только то, что нужно для индикации захвата
    private static float lerpLock = 1;
    private static final ResourceLocation FRAME = Mod.loc("textures/screens/aircraft/frame.png");
    private static final ResourceLocation FRAME_TARGET = Mod.loc("textures/screens/aircraft/frame_target.png");
    private static final ResourceLocation FRAME_LOCK = Mod.loc("textures/screens/aircraft/frame_lock.png");
    private static final ResourceLocation IND_1 = Mod.loc("textures/screens/aircraft/locking_ind1.png");
    private static final ResourceLocation IND_2 = Mod.loc("textures/screens/aircraft/locking_ind2.png");
    private static final ResourceLocation IND_3 = Mod.loc("textures/screens/aircraft/locking_ind3.png");
    private static final ResourceLocation IND_4 = Mod.loc("textures/screens/aircraft/locking_ind4.png");

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;
        if (player == null) return;
        if (ClientEventHandler.isEditing) return;

        var veh = player.getVehicle();
        if (!(veh instanceof ArmedVehicleEntity armed) || !armed.isDriver(player)) return;
        if (!(veh instanceof Mi28_1Entity mi28_1)) return;
        if (!(veh instanceof WeaponVehicleEntity weapon) || weapon.getWeaponIndex(0) != 1) return;

        PoseStack poseStack = guiGraphics.pose();

        // GL-состояния для 2D-оверлея
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.setShaderColor(1, 1, 1, 1);

        // Цель и потенциальные цели в секторе
        Entity targetEntity = EntityFindUtil.findEntity(player.level(), mi28_1.getTargetUuid());
        List<Entity> entities = SeekTool.seekCustomSizeEntities(mi28_1, player.level(), 384, 20, 0.9, true);

        for (Entity e : entities) {
            Vec3 pos3 = new Vec3(
                    Mth.lerp(partialTick, e.xo, e.getX()),
                    Mth.lerp(partialTick, e.yo + e.getEyeHeight(), e.getEyeY()),
                    Mth.lerp(partialTick, e.zo, e.getZ())
            );
            if (!VectorUtil.canSee(pos3)) continue;

            Vec3 point = VectorUtil.worldToScreen(pos3);
            boolean nearest = e == targetEntity;
            boolean lockOn = mi28_1.locked && nearest;

            float x = (float) point.x;
            float y = (float) point.y;

            poseStack.pushPose();
            if (lockOn) {
                // Полный лок — рамка LOCK
                RenderHelper.blit(poseStack, FRAME_LOCK, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
            } else if (nearest) {
                // Идёт захват — бегущие индикаторы + рамка TARGET
                lerpLock = Mth.lerp(partialTick, lerpLock, 2 * mi28_1.lockTime);
                float lockTime = Mth.clamp(20 - lerpLock, 0, 20);
                RenderHelper.blit(poseStack, IND_1, x - 12, y - 12 - lockTime, 0, 0, 24, 24, 24, 24, 1f);
                RenderHelper.blit(poseStack, IND_2, x - 12, y - 12 + lockTime, 0, 0, 24, 24, 24, 24, 1f);
                RenderHelper.blit(poseStack, IND_3, x - 12 - lockTime, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                RenderHelper.blit(poseStack, IND_4, x - 12 + lockTime, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                RenderHelper.blit(poseStack, FRAME_TARGET, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
            } else {
                // Прочие цели — простая рамка
                RenderHelper.blit(poseStack, FRAME, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
            }
            poseStack.popPose();
        }
    }
}