package tech.vvp.vvp.client.renderer.entity;

import tech.vvp.vvp.client.model.Uh60ModModel;
import tech.vvp.vvp.entity.vehicle.Uh60ModEntity;
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
import static tech.vvp.vvp.entity.vehicle.Uh60ModEntity.LOADED_MISSILE;

public class Uh60ModRenderer extends GeoEntityRenderer<Uh60ModEntity> {

    public Uh60ModRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Uh60ModModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(Uh60ModEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, Uh60ModEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(Uh60ModEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3(0, 1.45, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float) root.x, (float) root.y, (float) root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Uh60ModEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("propeller")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }
        if (name.equals("tailPropeller")) {
            bone.setRotX(-6 * Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }

        if (name.equals("missile1")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 4);
        }
        if (name.equals("missile2")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 3);
        }
        if (name.equals("missile4")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 2);
        }
        if (name.equals("missile3")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 1);
        }

        if (name.equals("missile5")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 8);
        }
        if (name.equals("missile6")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 7);
        }
        if (name.equals("missile8")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 6);
        }
        if (name.equals("missile7")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 5);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
