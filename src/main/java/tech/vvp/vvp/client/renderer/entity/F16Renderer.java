package tech.vvp.vvp.client.renderer.entity;

import tech.vvp.vvp.client.model.F16Model;
import tech.vvp.vvp.entity.vehicle.F16Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity.GEAR_ROT;

public class F16Renderer extends GeoEntityRenderer<F16Entity> {

    public F16Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F16Model());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(F16Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(F16Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3(0, 0, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float) root.x, (float) root.y, (float) root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, F16Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        
        if (name.equals("wingLR")) {
            bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.flap1LRotO, animatable.getFlap1LRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("wingRR")) {
            bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.flap1RRotO, animatable.getFlap1RRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("wingLR2")) {
            bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.flap1L2RotO, animatable.getFlap1L2Rot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("wingRR2")) {
            bone.setRotX(1.5f * Mth.lerp(partialTick, animatable.flap1R2RotO, animatable.getFlap1R2Rot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("wingLB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2LRotO, animatable.getFlap2LRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("wingRB")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.flap2RRotO, animatable.getFlap2RRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("weiyi")) {
            bone.setRotY(Mth.clamp(Mth.lerp(partialTick, animatable.flap3RotO, animatable.getFlap3Rot()), -20f, 20f) * Mth.DEG_TO_RAD);
        }
        if (name.equals("Chassis") || name.equals("Luk")) {
            // Ручна ротація шасі на основі GEAR_ROT
            float gearRot = Mth.lerp(partialTick, animatable.gearRotO, animatable.getEntityData().get(GEAR_ROT));
            if (name.equals("Chassis")) {
                bone.setRotX(gearRot * 100f / 85f * Mth.DEG_TO_RAD);
            } else if (name.equals("Luk")) {
                bone.setRotZ(gearRot * 90f / 85f * Mth.DEG_TO_RAD);
            }
        }
        if (name.equals("qianzhou") || name.equals("qianzhou2")) {
            bone.setRotZ(Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }
        
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
