package tech.vvp.vvp.client.renderer.entity;

import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;
import tech.vvp.vvp.client.model.PantsirS1Model;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity.YAW;
import static tech.vvp.vvp.entity.vehicle.PantsirS1Entity.LOADED_MISSILE;

public class PantsirS1Renderer extends GeoEntityRenderer<PantsirS1Entity> {

    public PantsirS1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PantsirS1Model());
        // this.addRenderLayer(new Bmp2Layer(this));
    }

    @Override
    public RenderType getRenderType(PantsirS1Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, PantsirS1Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(PantsirS1Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(180)); // Разворот модели на 180°
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, PantsirS1Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        
        // Колёса (WHEE)
        if (name.equals("WHEE")) {
            bone.setRotX(-1.5f * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
        }
        
        // Правые колёса (whell8, whell11, whell14, whell16)
        if (name.equals("whell8") || name.equals("whell11") || 
            name.equals("whell14") || name.equals("whell16")) {
            bone.setRotX(-1.5f * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
        }
        
        // Левые колёса (whell12, whell13, whell15, whell17)
        if (name.equals("whell12") || name.equals("whell13") || 
            name.equals("whell15") || name.equals("whell17")) {
            bone.setRotX(-1.5f * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
        }

        // Башня (BASNIA)
        if (name.equals("BASNIA")) {
            Player player = Minecraft.getInstance().player;
            bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);

            bone.setRotY(Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * Mth.DEG_TO_RAD);
        }

        // Ствол (RULL) - наклон вверх/вниз
        if (name.equals("RULL")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setRotX(
                    -Mth.lerp(partialTick, animatable.turretXRotO, animatable.getTurretXRot()) * Mth.DEG_TO_RAD
                            - r * animatable.getPitch(partialTick) * Mth.DEG_TO_RAD
                            - r2 * animatable.getRoll(partialTick) * Mth.DEG_TO_RAD
            );
        }

        // Отдача стволов (rushag или другие кости)
        if (name.equals("rushag")) {
            if (animatable.getWeaponIndex(0) == 0) {
                int fire = animatable.getEntityData().get(PantsirS1Entity.FIRE_ANIM);
                if (fire > 1) {
                    float maxBack = 0.95f;
                    bone.setPosZ(bone.getPosZ() - maxBack);
                }
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}