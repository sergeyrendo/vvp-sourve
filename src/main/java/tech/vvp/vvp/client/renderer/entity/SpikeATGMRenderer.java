package tech.vvp.vvp.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.vvp.vvp.client.model.SpikeATGMModel;
import tech.vvp.vvp.entity.projectile.SpikeATGMEntity;

public class SpikeATGMRenderer extends GeoEntityRenderer<SpikeATGMEntity> {
    public SpikeATGMRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpikeATGMModel());
    }

    @Override
    public RenderType getRenderType(SpikeATGMEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(SpikeATGMEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
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
    protected float getDeathMaxRotation(SpikeATGMEntity entityLivingBaseIn) {
        return 0.0F;
    }
}
