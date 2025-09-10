package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.mi28helmet;

public class mi28helmetRenderer extends GeoArmorRenderer<mi28helmet> {
    public mi28helmetRenderer() {
        super(new mi28helmetModel());
    }

    @Override
    public RenderType getRenderType(mi28helmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
