package tech.vvp.vvp.client.renderer.armor;

import tech.vvp.vvp.item.armor.usahelmet;
import tech.vvp.vvp.client.renderer.armor.usahelmetModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class usahelmetRenderer extends GeoArmorRenderer<usahelmet> {
	public usahelmetRenderer() {
		super(new usahelmetModel());
	}

	@Override
	public RenderType getRenderType(usahelmet animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
