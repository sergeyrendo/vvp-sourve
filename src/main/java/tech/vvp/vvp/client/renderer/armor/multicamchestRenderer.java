package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tech.vvp.vvp.item.armor.multicamchest;

public class multicamchestRenderer extends GeoArmorRenderer<multicamchest> {
	public multicamchestRenderer() {
		super(new multicamchestModel());
		this.body = new GeoBone(null, "armorBody", false, (double) 0, false, false);
		this.rightArm = new GeoBone(null, "armorRightArm", false, (double) 0, false, false);
		this.leftArm = new GeoBone(null, "armorLeftArm", false, (double) 0, false, false);
	}

	@Override
	public RenderType getRenderType(multicamchest animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
