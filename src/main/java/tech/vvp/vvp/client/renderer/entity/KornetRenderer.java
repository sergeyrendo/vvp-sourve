package tech.vvp.vvp.client.renderer.entity;

import tech.vvp.vvp.client.model.KornetModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tech.vvp.vvp.entity.vehicle.KornetEntity;

public class KornetRenderer extends GeoEntityRenderer<KornetEntity> {

    public KornetRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new KornetModel());
        this.shadowRadius = 1.5f;
    }

    @Override
    public RenderType getRenderType(KornetEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, KornetEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();

        if (name.equals("root")) {
            Player player = Minecraft.getInstance().player;
            bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);
        }

        if (name.equals("turret")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) * Mth.DEG_TO_RAD);
        }

        if (name.equals("barrel")) {
            bone.setRotX(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot()) * Mth.DEG_TO_RAD);
        }


        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
