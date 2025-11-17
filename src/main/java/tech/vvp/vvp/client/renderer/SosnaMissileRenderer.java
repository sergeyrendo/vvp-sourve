package tech.vvp.vvp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.vvp.vvp.client.model.SosnaMissileModel;
import tech.vvp.vvp.entity.projectile.SosnaMissileEntity;

public class SosnaMissileRenderer extends GeoEntityRenderer<SosnaMissileEntity> {

    public SosnaMissileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SosnaMissileModel());
    }

    @Override
    public ResourceLocation getTextureLocation(SosnaMissileEntity animatable) {
        return new ResourceLocation("vvp", "textures/entity/projectile/sosna_missile.png");
    }

    @Override
    public void render(SosnaMissileEntity entity, float entityYaw, float partialTick, 
                      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}


