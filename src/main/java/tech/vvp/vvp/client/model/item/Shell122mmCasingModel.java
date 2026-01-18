package tech.vvp.vvp.client.model.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.item.Shell122mmCasingItem;

public class Shell122mmCasingModel extends GeoModel<Shell122mmCasingItem> {
    @Override
    public ResourceLocation getModelResource(Shell122mmCasingItem animatable) {
        return new ResourceLocation("vvp", "geo/shell_122mm_casing.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Shell122mmCasingItem animatable) {
        return new ResourceLocation("vvp", "textures/item/shell_122mm_casing.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Shell122mmCasingItem animatable) {
        return null;
    }
}
