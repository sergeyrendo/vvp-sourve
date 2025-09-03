package tech.vvp.vvp.client.renderer.armor;

import tech.vvp.vvp.item.armor.multicamhelmet;
import tech.vvp.vvp.item.armor.multicamhelmet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class multicamhelmetRenderer extends GeoArmorRenderer<multicamhelmet> {
    public multicamhelmetRenderer() {
        super(new multicamhelmetModel());
    }

    @Override
    public RenderType getRenderType(multicamhelmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
