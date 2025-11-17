package tech.vvp.vvp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.vvp.vvp.client.model.PantsirS1MissileModel;
import tech.vvp.vvp.entity.projectile.PantsirS1MissileEntity;

public class PantsirS1MissileRenderer extends GeoEntityRenderer<PantsirS1MissileEntity> {

    public PantsirS1MissileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PantsirS1MissileModel());
    }

    @Override
    public ResourceLocation getTextureLocation(PantsirS1MissileEntity animatable) {
        return new ResourceLocation("vvp", "textures/entity/projectile/pantsir_s1_missile.png");
    }

    @Override
    public void render(PantsirS1MissileEntity entity, float entityYaw, float partialTick, 
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
