package tech.vvp.vvp.client.overlay;


import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import com.atsuishio.superbwarfare.entity.vehicle.base.ContainerMobileVehicleEntity;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.MathTool;
import com.atsuishio.superbwarfare.tools.VectorUtil;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
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
import tech.vvp.vvp.entity.vehicle.Mi28Entity;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.HEAT;

@OnlyIn(Dist.CLIENT)
public class Mi28Overlay implements IGuiOverlay {
    public static final String ID = VVP.MOD_ID + "_mi28_hud";

    // 资源路径定义
    private static final ResourceLocation CANNON_CROSSHAIR = new ResourceLocation("vvp", "textures/screens/land/mi28_cross.png");
    private static final ResourceLocation HELI_BASE = new ResourceLocation("superbwarfare", "textures/screens/helicopter/heli_base.png");
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/screens/compass.png");
    private static final ResourceLocation HELI_LINE = new ResourceLocation("superbwarfare", "textures/screens/helicopter/heli_line.png");
    private static final ResourceLocation ROLL_IND = new ResourceLocation("superbwarfare", "textures/screens/helicopter/roll_ind.png");
    private static final ResourceLocation SPEED_FRAME = new ResourceLocation("superbwarfare", "textures/screens/helicopter/speed_frame.png");
    private static final ResourceLocation DRIVER_ANGLE = new ResourceLocation("superbwarfare", "textures/screens/helicopter/heli_driver_angle.png");

    // ВСТАВКА: рамка ТВ
    private static final ResourceLocation FRAME = new ResourceLocation("vvp", "textures/screens/land/tv_frame.png");

    // 动画和插值变量
    private static float scopeScale = 1;

    // HUD颜色 - 绿色主题
    private static final int HUD_COLOR = 0xFF00FF00; // 绿色

    /**
     * 主渲染方法 - 每帧调用
     */
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = gui.getMinecraft();
        Player player = mc.player;

        // 基础检查
        if (player == null) return;
        if (com.atsuishio.superbwarfare.event.ClientEventHandler.isEditing) return;

        // 检查玩家是否在直升机实体中且是炮手位置
        if (player.getVehicle() instanceof Mi28Entity mi28 &&
                mi28.getNthEntity(1) == player) {

            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();

            // 设置渲染状态
            setupRenderState();

            // 根据相机类型选择不同的渲染方式`
            if (mc.options.getCameraType() == CameraType.FIRST_PERSON) {
                // 第一人称视角渲染 - 始终使用机炮准星
                renderHelicopterHUD(guiGraphics, screenWidth, screenHeight, mi28, player, partialTick, true);
            } else if (mc.options.getCameraType() == CameraType.THIRD_PERSON_BACK) {
                // 第三人称视角渲染 - 根据武器类型使用不同准星
                renderHelicopterHUD(guiGraphics, screenWidth, screenHeight, mi28, player, partialTick, false);
            }

            poseStack.popPose();
            // 恢复渲染状态
            restoreRenderState();
        } else {
            // 不在直升机内时重置准星缩放
            scopeScale = 0.7f;
        }
    }

    /**
     * Рисуем только «рамку ТВ»
     */
    private static void renderFrameOnly(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();

        // Соотношение сторон как float
        float aspect = screenWidth / (float) screenHeight;
        float addW = 48f * aspect;
        float addH = 27f * aspect;

        // Рисуем чуть больше экрана, чтобы перекрывать края
        preciseBlit(guiGraphics, FRAME,
                -addW / 2f, -addH / 2f,  // x, y
                10,                      // zLevel/blitOffset
                0f, 0f,                  // u, v
                screenWidth + addW,      // drawWidth
                screenHeight + addH,     // drawHeight
                screenWidth + addW,      // texWidth
                screenHeight + addH      // texHeight
        );

        pose.popPose();
    }

    /**
     * 渲染直升机HUD（第一人称和第三人称通用）
     * @param isFirstPerson 是否为第一人称视角
     */
    private void renderHelicopterHUD(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                     Mi28Entity mi28, Player player, float partialTick,
                                     boolean isFirstPerson) {
        PoseStack poseStack = guiGraphics.pose();

        // ВСТАВКА: рисуем рамку в 1-м лице или при зуме
        if (!isFirstPerson || com.atsuishio.superbwarfare.event.ClientEventHandler.zoomVehicle) {
            renderFrameOnly(guiGraphics, screenWidth, screenHeight);
        }

        // 计算准星缩放（平滑插值）
        scopeScale = Mth.lerp(partialTick, scopeScale, 1F);
        float minDimension = (float) Math.min(screenWidth, screenHeight);
        float adjustedSize = Math.min((float) screenWidth / minDimension, (float) screenHeight / minDimension) * scopeScale;
        int renderSize = Mth.floor(minDimension * adjustedSize);
        int xPos = (screenWidth - renderSize) / 2;
        int yPos = (screenHeight - renderSize) / 2;

        // 1. 渲染准星（根据视角和武器类型选择）
        ResourceLocation crosshairTexture = getCrosshairTexture(mi28, isFirstPerson);
        preciseBlit(guiGraphics, crosshairTexture, xPos, yPos, 0, 0.0F, renderSize, renderSize, renderSize, renderSize);

        // 2. 渲染武器状态信息
        renderWeaponStatus(guiGraphics, screenWidth, screenHeight, mi28, player);

        // 3. 渲染击杀指示器
        renderKillIndicator(guiGraphics, screenWidth / 2f - 7.5f, screenHeight / 2f - 7.5f);

        // 4. 渲染罗盘（显示直升机朝向）
//        renderCompass(guiGraphics, screenWidth, mi28);

        // 5. 渲染炮塔角度指示器
        renderGunnerAngle(guiGraphics, player, mi28, xPos, yPos, renderSize, renderSize, partialTick);

        // 6. 渲染功率和速度信息
        renderPowerAndSpeedInfo(guiGraphics, screenWidth, screenHeight, mi28, partialTick);

    }


    /**
     * 根据视角和武器类型获取准星纹理
     */
    private ResourceLocation getCrosshairTexture(Mi28Entity mi28, boolean isFirstPerson) {
        if (isFirstPerson) {
            // 第一人称始终使用机炮准星
            return CANNON_CROSSHAIR;
        } else {
            // 第三人称根据武器类型选择准星
            int weaponIndex = mi28.getWeaponIndex(1); // 获取炮手位当前武器索引
            return (weaponIndex == 1) ? CANNON_CROSSHAIR : CANNON_CROSSHAIR;
        }
    }


    /**
     * 渲染武器状态信息
     */
    private void renderWeaponStatus(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                    Mi28Entity mi28, Player player) {
        // 获取武器热量值
        int heat = mi28.getEntityData().get(HEAT);

        // 渲染热量条
        if (heat > 0) {
            float heatPercent = heat / 100.0F;
            renderVerticalHeatBar(guiGraphics, screenWidth, screenHeight, heatPercent);
        }
    }

    /**
     * 渲染垂直热量条
     */
    private void renderVerticalHeatBar(GuiGraphics guiGraphics, int screenWidth, int screenHeight, float heatPercent) {
        int barWidth = 3;
        int barHeight = 80;
        int margin = 20;

        int x = screenWidth - margin - barWidth;
        int y = screenHeight / 2 - barHeight / 2;

        // 渲染背景（绿色半透明）
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFFFFFFFF);

        // 计算热量填充高度和颜色（从绿色到黄色）
        int heatHeight = (int) (barHeight * heatPercent);
        int heatColor = Mth.hsvToRgb(0F, heatPercent, 1.0F);

        // 渲染热量填充
        guiGraphics.fill(x, y + barHeight - heatHeight, x + barWidth, y + barHeight, heatColor | 0xFF000000);
    }


//    /**
//     * 渲染罗盘
//     */
//    private void renderCompass(GuiGraphics guiGraphics, int screenWidth, ZHI10MEEntity zhi10mea) {
//        PoseStack poseStack = guiGraphics.pose();
//
//        RenderHelper.blit(poseStack, COMPASS,
//                (float) screenWidth / 2 - 128, 6,
//                128 + ((float) 64 / 45 * zhi10mea.getYRot()), 0,
//                256, 16, 512, 16, HUD_COLOR);
//    }

    /**
     * 渲染炮手角度指示器（从废案中恢复）
     * 渲染驾驶员视角角度指示器
     * @param k 水平位置
     * @param l 垂直位置
     * @param i 渲染宽度
     * @param j 渲染高度
     */
    private void renderGunnerAngle(GuiGraphics guiGraphics, Player player, Entity heli,
                                   float k, float l, float i, float j, float ticks) {
        PoseStack poseStack = guiGraphics.pose();

        // 计算玩家头部与直升机方向的差异
        float diffY = Mth.wrapDegrees(Mth.lerp(ticks, player.yHeadRotO, player.getYHeadRot()) - Mth.lerp(ticks, heli.yRotO, heli.getYRot())) * 0.35f;
        float diffX = Mth.wrapDegrees(Mth.lerp(ticks, player.xRotO, player.getXRot()) - Mth.lerp(ticks, heli.xRotO, heli.getXRot())) * 0.072f;

        // 如果是炮塔载具，渲染炮塔角度
        if(heli instanceof ContainerMobileVehicleEntity helic) {
            float gundiffY = Mth.wrapDegrees(Mth.lerp(ticks, helic.gunYRotO, helic.getGunYRot())) * -0.35f;
            float gundiffX = Mth.wrapDegrees(Mth.lerp(ticks, helic.gunXRotO, helic.getGunXRot()) - Mth.lerp(ticks, heli.xRotO, heli.getXRot())) * 0.072f;

            RenderHelper.blit(poseStack, DRIVER_ANGLE,
                    k + gundiffY, l + gundiffX, 0, 0.0F, i, j, i, j, HUD_COLOR);
        }

        // 渲染驾驶员头部角度指示器
        RenderHelper.blit(poseStack, DRIVER_ANGLE,
                k + diffY, l + diffX, 0, 0.0F, i, j, i, j,0xFFFF0000);
    }

    /**
     * 渲染功率和速度信息
     */
    private void renderPowerAndSpeedInfo(GuiGraphics guiGraphics, int screenWidth, int screenHeight,
                                         Mi28Entity mi28, float partialTick) {
        PoseStack poseStack = guiGraphics.pose();

        // 显示高度坐标（绿色）
        guiGraphics.drawString(Minecraft.getInstance().font,
                Component.literal(String.valueOf((int)mi28.getY())),
                screenWidth / 2 + 104, screenHeight / 2, HUD_COLOR, false);

        // 渲染速度框架和显示（绿色）
        RenderHelper.blit(poseStack, SPEED_FRAME,
                (float) screenWidth / 2 - 144, (float) screenHeight / 2 - 6,
                0, 0, 50, 18, 50, 18, HUD_COLOR);

        double speed = Math.sqrt(
                mi28.getDeltaMovement().x * mi28.getDeltaMovement().x +
                        mi28.getDeltaMovement().y * mi28.getDeltaMovement().y +
                        mi28.getDeltaMovement().z * mi28.getDeltaMovement().z
        ) * 72;

        guiGraphics.drawString(Minecraft.getInstance().font,
                Component.literal(String.format("%.0f km/h", speed)),
                screenWidth / 2 - 140, screenHeight / 2, HUD_COLOR, false);
    }



    /**
     * 渲染击杀指示器
     */
    private static void renderKillIndicator(GuiGraphics guiGraphics, float posX, float posY) {
        VehicleHudOverlay.renderKillIndicator3P(guiGraphics, posX, posY);
    }

    /**
     * 设置渲染状态
     */
    private void setupRenderState() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    /**
     * 恢复渲染状态
     */
    private void restoreRenderState() {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
    }
}