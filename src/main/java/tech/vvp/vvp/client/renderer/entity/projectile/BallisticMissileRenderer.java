package tech.vvp.vvp.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.vvp.vvp.client.model.BallisticMissileModel;
import tech.vvp.vvp.entity.projectile.BallisticMissileEntity;

public class BallisticMissileRenderer extends GeoEntityRenderer<BallisticMissileEntity> {

    public BallisticMissileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BallisticMissileModel());
    }

    @Override
    public void preRender(PoseStack poseStack, BallisticMissileEntity animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                          float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.scale(1.0f, 1.0f, 1.0f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
