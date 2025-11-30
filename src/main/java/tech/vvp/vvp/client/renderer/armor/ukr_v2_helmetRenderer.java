package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.ukr_v2_helmet;

public class ukr_v2_helmetRenderer extends GeoArmorRenderer<ukr_v2_helmet> {
    public ukr_v2_helmetRenderer() {
        super(new ukr_v2_helmetModel());
    }

    @Override
    public RenderType getRenderType(ukr_v2_helmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
