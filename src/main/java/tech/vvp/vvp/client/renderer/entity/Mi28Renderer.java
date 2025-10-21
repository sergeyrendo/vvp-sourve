package tech.vvp.vvp.client.renderer.entity;

import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import tech.vvp.vvp.client.model.Mi28Model;
import tech.vvp.vvp.entity.vehicle.Mi28Entity;
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

import static tech.vvp.vvp.entity.vehicle.Mi28Entity.LOADED_MISSILE;

public class Mi28Renderer extends GeoEntityRenderer<Mi28Entity> {

    public Mi28Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mi28Model());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderType getRenderType(Mi28Entity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, Mi28Entity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(Mi28Entity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        Vec3 root = new Vec3(0, 1.45, 0);
        poseStack.rotateAround(Axis.YP.rotationDegrees(-entityYaw), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())), (float) root.x, (float) root.y, (float) root.z);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entityIn.prevRoll, entityIn.getRoll())), (float) root.x, (float) root.y, (float) root.z);
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Mi28Entity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("propeller")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }
        if (name.equals("tailPropeller")) {
            bone.setRotX(-10 * -Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }
        if (name.equals("tailPropeller2")) {
            bone.setRotX(-10 * Mth.lerp(partialTick, animatable.propellerRotO, animatable.getPropellerRot()));
        }

        if (name.equals("missle1")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 2);
        }
        if (name.equals("missle2")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 1);
        }

        if (name.equals("missle3")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 3);
        }
        if (name.equals("missle4")) {
            bone.setHidden(animatable.getEntityData().get(LOADED_MISSILE) < 4);
        }

        if (name.equals("cannon")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.gunYRotO, animatable.getGunYRot()) * Mth.DEG_TO_RAD - Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("barrel")) {
            float a = animatable.getTurretYaw(partialTick);
            float r = (Mth.abs(a) - 90f) / 90f;

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setRotX(Mth.clamp(
                    -Mth.lerp(partialTick, animatable.gunXRotO, animatable.getGunXRot()) * Mth.DEG_TO_RAD
                            - r * animatable.getPitch(partialTick) * Mth.DEG_TO_RAD
                            - r2 * animatable.getRoll(partialTick) * Mth.DEG_TO_RAD,
                    -10 * Mth.DEG_TO_RAD, 60 * Mth.DEG_TO_RAD)
            );
        }

        bone.setHidden(name.equals("weapon_set_2"));
        bone.setHidden(name.equals("weapon_set_3"));


        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
