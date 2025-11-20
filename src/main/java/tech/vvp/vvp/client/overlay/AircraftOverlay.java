package tech.vvp.vvp.client.overlay;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.client.overlay.VehicleHudOverlay;
import tech.vvp.vvp.entity.vehicle.Su25Entity;
import com.atsuishio.superbwarfare.entity.vehicle.base.AircraftEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.WeaponVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.tools.*;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
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
import org.joml.Math;
import tech.vvp.vvp.VVP;

import java.util.List;

import static com.atsuishio.superbwarfare.client.RenderHelper.preciseBlit;
import static com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity.HEAT;
import static com.atsuishio.superbwarfare.event.ClientEventHandler.zoomVehicle;

@OnlyIn(Dist.CLIENT)
public class AircraftOverlay implements IGuiOverlay {
    public static final String ID = VVP.MOD_ID + "_aircraft_hud";
    private static float lerpVy = 1;
    private static float lerpLock = 1;
    private static float lerpG = 1;
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
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        PoseStack poseStack = guiGraphics.pose();

        if (player == null) return;

        if (ClientEventHandler.isEditing)
            return;

        if (player.getVehicle() instanceof AircraftEntity aircraftEntity && aircraftEntity instanceof MobileVehicleEntity mobileVehicle && aircraftEntity.isDriver(player) && player.getVehicle() instanceof WeaponVehicleEntity weaponVehicle) {
            poseStack.pushPose();

            int color = mobileVehicle.getHudColor();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            lerpVy = (float) Mth.lerp(0.021f * partialTick, lerpVy, mobileVehicle.getDeltaMovement().y());
            float diffY = (float) ClientMouseHandler.lerpSpeedX;
            float diffX = (float) ClientMouseHandler.lerpSpeedY;

            Vec3 pos = cameraPos.add(mobileVehicle.getViewVector(partialTick).scale(192));
            Vec3 posCross = aircraftEntity.shootPos(partialTick).add(aircraftEntity.shootVec(partialTick).scale(192));

            Vec3 p = VectorUtil.worldToScreen(pos);
            Vec3 pCross = VectorUtil.worldToScreen(posCross);

            // 投弹准星
            if (mobileVehicle instanceof Su25Entity su25Entity && weaponVehicle.getWeaponIndex(0) == 2 && (zoomVehicle || mc.options.getCameraType() != CameraType.FIRST_PERSON)) {
                Vec3 p0 = su25Entity.bombLandingPosO;
                Vec3 p1 = su25Entity.bombLandingPos;
                if (p0 != null && p1 != null) {
                    Vec3 bombCross = p0.lerp(p1, partialTick);
                    pCross = VectorUtil.worldToScreen(bombCross);

                    if (zoomVehicle && VectorUtil.canSee(bombCross)) {
                        float f = (float) Math.min(screenWidth, screenHeight);
                        float f1 = Math.min((float) screenWidth / f, (float) screenHeight / f);
                        int i = Mth.floor(f * f1);
                        int j = Mth.floor(f * f1);

                        float x = (float) pCross.x;
                        float y = (float) pCross.y;


                        poseStack.pushPose();
                        poseStack.translate(x, y, 0);
                        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("FAB-500 BOMB " + aircraftEntity.getAmmoCount(player)), 25, -11, 1, false);
                        poseStack.popPose();

                        preciseBlit(guiGraphics, Mod.loc("textures/screens/aircraft/bomb_scope.png"), x - 1.5f * i, y - 1.5f * j, 0, 0, 3 * i, 3 * j, 3 * i, 3 * j);

                        poseStack.pushPose();
                        poseStack.rotateAround(Axis.ZP.rotationDegrees(aircraftEntity.getRotZ(partialTick)), x, y, 0);
                        preciseBlit(guiGraphics, Mod.loc("textures/screens/aircraft/bomb_scope_pitch.png"), x - 1.5f * i, y - 1.5f * j - 4 * su25Entity.getPitch(partialTick), 0, 0, 3 * i, 3 * j, 3 * i, 3 * j);
                        renderKillIndicator(guiGraphics, x - 7.5f + (float) (2 * (Math.random() - 0.5f)), y - 7.5f + (float) (2 * (Math.random() - 0.5f)));
                        poseStack.popPose();
                        return;
                    }
                }

            }

            {
                poseStack.pushPose();
                float x = (float) p.x;
                float y = (float) p.y;

                if (mc.options.getCameraType() == CameraType.FIRST_PERSON && VectorUtil.canSee(pos)) {

                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.enableBlend();
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    RenderSystem.setShaderColor(1, 1, 1, 1);

                    if (mobileVehicle instanceof Su25Entity && weaponVehicle.getWeaponIndex(0) == 3) {
                        RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/hud_base_missile.png"), x - 160, y - 160, 0, 0, 320, 320, 320, 320, color);
                    } else {
                        RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/hud_base.png"), x - 160, y - 160, 0, 0, 320, 320, 320, 320, color);
                    }

                    //指南针
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/compass.png"), x - 128, y - 122, 128 + ((float) 64 / 45 * mobileVehicle.getYRot()), 0, 256, 16, 512, 16, color);
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/compass_ind.png"), x - 4, y - 130, 0, 0, 8, 8, 8, 8, color);

                    //滚转指示
                    poseStack.pushPose();
                    poseStack.rotateAround(Axis.ZP.rotationDegrees(aircraftEntity.getRotZ(partialTick)), x, y + 48, 0);
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/helicopter/roll_ind.png"), x - 4, y + 144, 0, 0, 8, 8, 8, 8, color);
                    poseStack.popPose();

                    //时速
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.format0D(mobileVehicle.getDeltaMovement().dot(mobileVehicle.getViewVector(1)) * 72)),
                            (int) x - 105, (int) y - 61, color, false);
                    //高度
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.format0D(mobileVehicle.getY())),
                            (int) x + 111 - 36, (int) y - 61, color, false);
                    //框
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/helicopter/speed_frame.png"), x - 108, y - 64, 0, 0, 36, 12, 36, 12, color);
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/helicopter/speed_frame.png"), x + 108 - 36, y - 64, 0, 0, 36, 12, 36, 12, color);
                    //垂直速度
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.DECIMAL_FORMAT_1ZZ.format(lerpVy * 20)), (int) x - 96, (int) y + 60, color, false);
                    //加速度
                    lerpG = (float) Mth.lerp(0.1f * partialTick, lerpG, mobileVehicle.acceleration / 9.8);
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("M"), (int) x - 105, (int) y + 70, color, false);
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("0.2"), (int) x - 96, (int) y + 70, color, false);
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("G"), (int) x - 105, (int) y + 78, color, false);
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(FormatTool.DECIMAL_FORMAT_1ZZ.format(lerpG)), (int) x - 96, (int) y + 78, color, false);

                    // 热诱弹
                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("IR FLARES " + aircraftEntity.getDecoy()), (int) x + 72, (int) y, color, false);

                    guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("TGT"), (int) x + 76, (int) y + 78, color, false);

                    if (mobileVehicle instanceof Su25Entity su25Entity) {
                        if (weaponVehicle.getWeaponIndex(0) == 0) {
                            int heat = su25Entity.getEntityData().get(HEAT);
                            String name = "30MM CANNON";
                            int width = Minecraft.getInstance().font.width(name);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(name), (int) x - width / 2, (int) y + 67, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);

                            String count = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : String.valueOf(aircraftEntity.getAmmoCount(player));
                            int width2 = Minecraft.getInstance().font.width(count);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(count), (int) x - width2 / 2, (int) y + 76, MathTool.getGradientColor(color, 0xFF0000, heat, 2), false);
                        } else if (weaponVehicle.getWeaponIndex(0) == 1) {
                            String name = "70MM ROCKET";
                            int width = Minecraft.getInstance().font.width(name);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(name), (int) x - width / 2, (int) y + 67, color, false);

                            String count = String.valueOf(aircraftEntity.getAmmoCount(player));
                            int width2 = Minecraft.getInstance().font.width(count);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(count), (int) x - width2 / 2, (int) y + 76, color, false);
                        } else if (weaponVehicle.getWeaponIndex(0) == 2) {
                            String name = "FAB-500 BOMB";
                            int width = Minecraft.getInstance().font.width(name);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(name), (int) x - width / 2, (int) y + 67, color, false);

                            String count = String.valueOf(aircraftEntity.getAmmoCount(player));
                            int width2 = Minecraft.getInstance().font.width(count);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(count), (int) x - width2 / 2, (int) y + 76, color, false);
                        } else if (weaponVehicle.getWeaponIndex(0) == 3) {
                            String name = "X-25";
                            int width = Minecraft.getInstance().font.width(name);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(name), (int) x - width / 2, (int) y + 67, color, false);

                            String count = String.valueOf(aircraftEntity.getAmmoCount(player));
                            int width2 = Minecraft.getInstance().font.width(count);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(count), (int) x - width2 / 2, (int) y + 76, color, false);
                        }
                    } else if (mobileVehicle instanceof tech.vvp.vvp.entity.vehicle.F16Entity f16Entity) {
                        if (weaponVehicle.getWeaponIndex(0) == 0) {
                            String name = "M61A1 VULCAN";
                            int width = Minecraft.getInstance().font.width(name);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(name), (int) x - width / 2, (int) y + 67, color, false);

                            String count = InventoryTool.hasCreativeAmmoBox(player) ? "∞" : String.valueOf(aircraftEntity.getAmmoCount(player));
                            int width2 = Minecraft.getInstance().font.width(count);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(count), (int) x - width2 / 2, (int) y + 76, color, false);
                        } else if (weaponVehicle.getWeaponIndex(0) == 1) {
                            String name = "AIM-120 AMRAAM";
                            int width = Minecraft.getInstance().font.width(name);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(name), (int) x - width / 2, (int) y + 67, color, false);

                            String count = String.valueOf(aircraftEntity.getAmmoCount(player));
                            int width2 = Minecraft.getInstance().font.width(count);
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(count), (int) x - width2 / 2, (int) y + 76, color, false);
                        }
                    }

                    //角度
                    poseStack.pushPose();

                    RenderSystem.disableDepthTest();
                    RenderSystem.depthMask(false);
                    RenderSystem.enableBlend();
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    RenderSystem.setShaderColor(1, 1, 1, 1);

                    poseStack.rotateAround(Axis.ZP.rotationDegrees(-aircraftEntity.getRotZ(partialTick)), x, y, 0);
                    float pitch = aircraftEntity.getRotX(partialTick);
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/hud_line.png"), x - 96 + diffY, y - 128, 0, 448 + 4.10625f * pitch, 192, 256, 192, 1152, color);
                    RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/hud_ind.png"), x - 18 + diffY, y - 12, 0, 0, 36, 24, 36, 24, color);
                    poseStack.popPose();

                    // 能量警告
                    if (mobileVehicle.hasEnergyStorage()) {
                        if (mobileVehicle.getEnergy() < 0.02 * mobileVehicle.getMaxEnergy()) {
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("NO POWER!"),
                                    (int) x - 144, (int) y + 14, -65536, false);
                        } else if (mobileVehicle.getEnergy() < 0.2 * mobileVehicle.getMaxEnergy()) {
                            guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("LOW POWER"),
                                    (int) x - 144, (int) y + 14, 0xFF6B00, false);
                        }
                    }
                }
            }

            // 准星
            {
                poseStack.pushPose();
                float x = (float) pCross.x;
                float y = (float) pCross.y;

                if (VectorUtil.canSee(posCross)) {
                    if (mc.options.getCameraType() == CameraType.FIRST_PERSON && !(mobileVehicle instanceof Su25Entity su25Entity && su25Entity.getWeaponIndex(0) == 3)) {
                        RenderSystem.disableDepthTest();
                        RenderSystem.depthMask(false);
                        RenderSystem.enableBlend();
                        RenderSystem.setShader(GameRenderer::getPositionTexShader);
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                        RenderSystem.setShaderColor(1, 1, 1, 1);

                        RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/hud_base2.png"), x - 72 + diffY, y - 72 + diffX, 0, 0, 144, 144, 144, 144, color);

                        RenderHelper.blit(poseStack, Mod.loc("textures/screens/aircraft/crosshair_ind.png"), x - 16, y - 16, 0, 0, 32, 32, 32, 32, color);
                        renderKillIndicator(guiGraphics, x - 7.5f + (float) (2 * (Math.random() - 0.5f)), y - 7.5f + (float) (2 * (Math.random() - 0.5f)));
                    } else if (mc.options.getCameraType() == CameraType.THIRD_PERSON_BACK) {
                        poseStack.pushPose();
                        poseStack.rotateAround(Axis.ZP.rotationDegrees(aircraftEntity.getRotZ(partialTick)), x, y, 0);
                        poseStack.pushPose();
                        poseStack.translate(x, y, 0);
                        poseStack.scale(0.75f, 0.75f, 1);

                        ResourceLocation cross = Mod.loc("textures/screens/drone.png");
                        float size = 16;

                        if (mobileVehicle instanceof Su25Entity su25Entity) {
                            if (weaponVehicle.getWeaponIndex(0) == 0) {
                                double heat = su25Entity.getEntityData().get(HEAT) / 100.0F;
                                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("30MM CANNON " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : aircraftEntity.getAmmoCount(player))), 25, -9, Mth.hsvToRgb(0F, (float) heat, 1.0F), false);
                            } else if (weaponVehicle.getWeaponIndex(0) == 1) {
                                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("70MM ROCKET " + aircraftEntity.getAmmoCount(player)), 25, -9, -1, false);
                            } else if (weaponVehicle.getWeaponIndex(0) == 2) {
                                cross = Mod.loc("textures/screens/shotgun_hud.png");
                                size = 24;
                                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("FAB-500 BOMB " + aircraftEntity.getAmmoCount(player)), 25, -9, -1, false);
                            } else if (weaponVehicle.getWeaponIndex(0) == 3) {
                                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("X-25 " + aircraftEntity.getAmmoCount(player)), 25, -9, -1, false);
                            }
                        } else if (mobileVehicle instanceof tech.vvp.vvp.entity.vehicle.F16Entity f16Entity) {
                            if (weaponVehicle.getWeaponIndex(0) == 0) {
                                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("M61A1 VULCAN " + (InventoryTool.hasCreativeAmmoBox(player) ? "∞" : aircraftEntity.getAmmoCount(player))), 25, -9, -1, false);
                            } else if (weaponVehicle.getWeaponIndex(0) == 1) {
                                guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("AIM-120 AMRAAM " + aircraftEntity.getAmmoCount(player)), 25, -9, -1, false);
                            }
                        }

                        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("IR FLARES " + aircraftEntity.getDecoy()), 25, 1, -1, false);
                        poseStack.popPose();
                        preciseBlit(guiGraphics, cross, x - 0.5f * size, y - 0.5f * size, 0, 0, size, size, size, size);
                        renderKillIndicator(guiGraphics, x - 7.5f + (float) (2 * (Math.random() - 0.5f)), y - 7.5f + (float) (2 * (Math.random() - 0.5f)));
                        poseStack.popPose();
                    }
                }

                poseStack.popPose();
            }

            // A-10的导弹锁定
            if ((mobileVehicle instanceof Su25Entity su25Entity && su25Entity.getWeaponIndex(0) == 3) || 
                (mobileVehicle instanceof tech.vvp.vvp.entity.vehicle.F16Entity f16Entity && f16Entity.getWeaponIndex(0) == 1)) {
                
                String targetUuid = mobileVehicle instanceof Su25Entity su25 ? su25.getTargetUuid() : ((tech.vvp.vvp.entity.vehicle.F16Entity)mobileVehicle).getTargetUuid();
                boolean locked = mobileVehicle instanceof Su25Entity su25 ? su25.locked : ((tech.vvp.vvp.entity.vehicle.F16Entity)mobileVehicle).locked;
                int lockTime = mobileVehicle instanceof Su25Entity su25 ? su25.lockTime : ((tech.vvp.vvp.entity.vehicle.F16Entity)mobileVehicle).lockTime;
                
                Entity targetEntity = EntityFindUtil.findEntity(player.level(), targetUuid);
                // Для F-16 не проверяем onGround, чтобы видеть летающие цели
                boolean checkOnGround = !(mobileVehicle instanceof tech.vvp.vvp.entity.vehicle.F16Entity);
                List<Entity> entities = SeekTool.seekCustomSizeEntities(mobileVehicle, player.level(), 384, 20, 0.9, checkOnGround);

                for (var e : entities) {
                    // Для F-16 показываем только воздушные цели
                    if (mobileVehicle instanceof tech.vvp.vvp.entity.vehicle.F16Entity && !isAirborneTargetClient(e)) {
                        continue;
                    }
                    
                    Vec3 pos3 = new Vec3(Mth.lerp(partialTick, e.xo, e.getX()), Mth.lerp(partialTick, e.yo + e.getEyeHeight(), e.getEyeY()), Mth.lerp(partialTick, e.zo, e.getZ()));
                    if (VectorUtil.canSee(pos3)) {
                        Vec3 point = VectorUtil.worldToScreen(pos3);
                        boolean nearest = e == targetEntity;
                        boolean lockOn = locked && nearest;

                        poseStack.pushPose();
                        float x = (float) point.x;
                        float y = (float) point.y;

                        if (lockOn) {
                            RenderHelper.blit(poseStack, FRAME_LOCK, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                        } else if (nearest) {
                            lerpLock = Mth.lerp(partialTick, lerpLock, 2 * lockTime);
                            float lockTimeOffset = Mth.clamp(20 - lerpLock, 0, 20);
                            RenderHelper.blit(poseStack, IND_1, x - 12, y - 12 - lockTimeOffset, 0, 0, 24, 24, 24, 24, 1f);
                            RenderHelper.blit(poseStack, IND_2, x - 12, y - 12 + lockTimeOffset, 0, 0, 24, 24, 24, 24, 1f);
                            RenderHelper.blit(poseStack, IND_3, x - 12 - lockTimeOffset, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                            RenderHelper.blit(poseStack, IND_4, x - 12 + lockTimeOffset, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                            RenderHelper.blit(poseStack, FRAME_TARGET, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                        } else {
                            RenderHelper.blit(poseStack, FRAME, x - 12, y - 12, 0, 0, 24, 24, 24, 24, 1f);
                        }
                        poseStack.popPose();
                    }
                }
            }

            poseStack.popPose();
        }
    }

    private static void renderKillIndicator(GuiGraphics guiGraphics, float posX, float posY) {
        VehicleHudOverlay.renderKillIndicator3P(guiGraphics, posX, posY);
    }

    private static boolean isAirborneTargetClient(Entity target) {
        // Проверка 1: Это летающая техника?
        if (target instanceof com.atsuishio.superbwarfare.entity.vehicle.base.AircraftEntity || 
            target instanceof com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity) {
            return true;
        }
        
        // Проверка 2: Не на земле И достаточно высоко?
        if (!target.onGround()) {
            net.minecraft.core.BlockPos groundPos = target.level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, target.blockPosition());
            double heightAboveGround = target.getY() - groundPos.getY();
            return heightAboveGround > 3.0; // Минимум 3 блока в воздухе
        }
        
        return false;
    }

    public static double length(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
