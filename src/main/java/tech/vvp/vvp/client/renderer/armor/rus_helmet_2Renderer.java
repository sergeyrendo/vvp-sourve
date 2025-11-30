package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.rus_helmet_2;

public class rus_helmet_2Renderer extends GeoArmorRenderer<rus_helmet_2> {
    public rus_helmet_2Renderer() {
        super(new rus_helmet_2Model());
    }

    @Override
    public RenderType getRenderType(rus_helmet_2 animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
