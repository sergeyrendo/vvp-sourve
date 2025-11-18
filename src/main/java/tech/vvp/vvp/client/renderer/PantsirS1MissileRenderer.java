package tech.vvp.vvp.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
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
        
        // Получаем вектор движения
        Vec3 motion = entity.getDeltaMovement();
        
        if (motion.lengthSqr() > 0.001) {
            // Нормализуем вектор движения
            Vec3 direction = motion.normalize();
            
            // Вычисляем yaw (горизонтальный угол) - куда смотрит в плоскости XZ
            float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
            
            // Вычисляем pitch (вертикальный угол) - наклон вверх/вниз
            float pitch = (float) Math.toDegrees(Math.asin(-direction.y));
            
            // Применяем ротацию: сначала поворот по горизонтали, потом наклон
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        }
        
        poseStack.scale(0.8f, 0.8f, 0.8f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
