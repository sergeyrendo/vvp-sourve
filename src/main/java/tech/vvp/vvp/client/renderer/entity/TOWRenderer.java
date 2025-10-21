package tech.vvp.vvp.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.vvp.vvp.client.model.TOWModel;
import tech.vvp.vvp.entity.projectile.TOWEntity;

public class TOWRenderer extends GeoEntityRenderer<TOWEntity> {
    public TOWRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TOWModel());
    }

    @Override
    public RenderType getRenderType(TOWEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(TOWEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        try {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90));
            poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } finally {
            poseStack.popPose();
        }
    }
    @Override
    protected float getDeathMaxRotation(TOWEntity entityLivingBaseIn) {
        return 0.0F;
    }
}
