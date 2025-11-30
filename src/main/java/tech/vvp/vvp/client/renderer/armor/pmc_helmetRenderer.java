package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.pmc_helmet;

public class pmc_helmetRenderer extends GeoArmorRenderer<pmc_helmet> {
    public pmc_helmetRenderer() {
        super(new pmc_helmetModel());
    }

    @Override
    public RenderType getRenderType(pmc_helmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
