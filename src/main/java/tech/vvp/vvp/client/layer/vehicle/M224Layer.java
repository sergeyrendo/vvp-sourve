package tech.vvp.vvp.client.layer.vehicle;

import com.atsuishio.superbwarfare.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M224Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class M224Layer extends GeoRenderLayer<M224Entity> {

    private static final ResourceLocation LAYER = Mod.loc("textures/entity/mortar_e.png");

    public M224Layer(GeoRenderer<M224Entity> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack poseStack, M224Entity animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        RenderType glowRenderType = RenderType.eyes(LAYER);
        getRenderer().reRender(getDefaultBakedModel(animatable), poseStack, bufferSource, animatable, glowRenderType, bufferSource.getBuffer(glowRenderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
}
