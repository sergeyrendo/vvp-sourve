package tech.vvp.vvp.client.renderer.entity;

import tech.vvp.vvp.client.model.Mk19GrenadeModel;
import tech.vvp.vvp.entity.projectile.Mk19GrenadeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Mk19GrenadeRenderer extends GeoEntityRenderer<Mk19GrenadeEntity> {
    public Mk19GrenadeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mk19GrenadeModel());
//        this.addRenderLayer(new SmallCannonShellLayer(this));
    }

    @Override
    public RenderType getRenderType(Mk19GrenadeEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, Mk19GrenadeEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        if (entity.tickCount > 0) {
            float scale = 1f;
            this.scaleHeight = scale;
            this.scaleWidth = scale;
            super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public void render(Mk19GrenadeEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        if (entityIn.tickCount > 0) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
            super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
            poseStack.popPose();
        }
    }
}
