package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.rus_helmet;

public class rus_helmetRenderer extends GeoArmorRenderer<rus_helmet> {
    public rus_helmetRenderer() {
        super(new rus_helmetModel());
    }

    @Override
    public RenderType getRenderType(rus_helmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
