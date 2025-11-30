package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.ukr_helmet;

public class ukr_helmetRenderer extends GeoArmorRenderer<ukr_helmet> {
    public ukr_helmetRenderer() {
        super(new ukr_helmetModel());
    }

    @Override
    public RenderType getRenderType(ukr_helmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
