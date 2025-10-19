package tech.vvp.vvp.client.renderer.entity;

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
import tech.vvp.vvp.client.model.SuperCobraWhiteModel;
import tech.vvp.vvp.entity.vehicle.SuperCobraWhiteEntity;

import static tech.vvp.vvp.entity.vehicle.Su25Entity.LOADED_MISSILE;

public class SuperCobraWhiteRenderer extends GeoEntityRenderer<SuperCobraWhiteEntity> {

    public SuperCobraWhiteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SuperCobraWhiteModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(SuperCobraWhiteEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(SuperCobraWhiteEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3(0, 1.45, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float) root.x, (float) root.y, (float) root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SuperCobraWhiteEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("propeller")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }
        if (name.equals("tailPropeller")) {
            bone.setRotX(-6 * Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }


        // правая
        if (name.equals("raketa")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 1);
        }
        if (name.equals("raketa1")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 3);
        }
        if (name.equals("raketa2")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 5);
        }
        if (name.equals("raketa3")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 7);
        }


        // левая
        if (name.equals("raketa4")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 2);
        }
        if (name.equals("raketa5")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 4);
        }
        if (name.equals("raketa6")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 6);
        }
        if (name.equals("raketa7")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 8);
        }


        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
