package tech.vvp.vvp.client.renderer.entity;

import tech.vvp.vvp.client.model.f35Model;
import tech.vvp.vvp.entity.vehicle.F35Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.atsuishio.superbwarfare.entity.vehicle.A10Entity.LOADED_MISSILE;
import static com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity.GEAR_ROT;

public class f35Renderer extends GeoEntityRenderer<F35Entity> {

    public f35Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new f35Model());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(F35Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, F35Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(F35Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3(0, 2.375, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float) root.x, (float) root.y, (float) root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, F35Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("gear2") || name.equals("gear3")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, animatable.getEntityData().get(GEAR_ROT)) * Mth.DEG_TO_RAD);
        }

        if (name.equals("gear1")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.gearRotO, animatable.getEntityData().get(GEAR_ROT)) * Mth.DEG_TO_RAD);
        }

        // if (name.equals("bomb1")) {
        //     bone.setHidden(animatable.getEntityData().get(LOADED_BOMB) < 1);
        // }
        // if (name.equals("bomb2")) {
        //     bone.setHidden(animatable.getEntityData().get(LOADED_BOMB) < 2);
        // }

        if (name.equals("missile1")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 1);
        }
        if (name.equals("missile2")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 2);
        }
        if (name.equals("missile3")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 3);
        }
        if (name.equals("missile4")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 4);
        }

        if (name.equals("wingLB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2LRotO, animatable.getFlap2LRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("wingRB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2RRotO, animatable.getFlap2RRot()) * Mth.DEG_TO_RAD);
        }
        // if (name.equals("weiyi")) {
        //     bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -5f, 5f) * Mth.DEG_TO_RAD);
        // }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
