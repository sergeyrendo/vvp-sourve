 package tech.vvp.vvp.client.renderer.entity;

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
import tech.vvp.vvp.client.model.BushmasterModel;
import tech.vvp.vvp.entity.vehicle.BushmasterEntity;

import static com.atsuishio.superbwarfare.entity.vehicle.Yx100Entity.YAW;

public class BushmasterRenderer extends GeoEntityRenderer<BushmasterEntity> {

    public BushmasterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BushmasterModel());
//        this.addRenderLayer(new Yx100GlowLayer(this));
    }

    @Override
    public RenderType getRenderType(BushmasterEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, BushmasterEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(BushmasterEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3(0, entityIn.rotateYOffset(), 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float) root.x, (float) root.y, (float) root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, BushmasterEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("wheel1")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot()));
            bone.setRotX(-1.5f * Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
        }
        if (name.equals("wheel2")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot()));
            bone.setRotX(-1.5f * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
        }
        if (name.equals("wheel3")) {
            bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.rightWheelRotO, animatable.getRightWheelRot()));
        }
        if (name.equals("wheel4")) {
            bone.setRotX(1.5f *  Mth.lerp(partialTick, animatable.leftWheelRotO, animatable.getLeftWheelRot()));
        }



        if (name.equals("base")) {
            float a = animatable.getEntityData().get(YAW);
            float r = (Mth.abs(a) - 90f) / 90f;

            bone.setPosZ(r * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * 1.75f);
            bone.setRotX(r * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * Mth.DEG_TO_RAD);

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

            bone.setPosX(r2 * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * 1f);
            bone.setRotZ(r2 * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * Mth.DEG_TO_RAD * 1.5f);
        }

        if (name.equals("root")) {
            Player player = Minecraft.getInstance().player;
            bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}