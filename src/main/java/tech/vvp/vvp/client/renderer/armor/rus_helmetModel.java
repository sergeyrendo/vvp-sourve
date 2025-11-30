package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.rus_helmet;

public class rus_helmetModel extends GeoModel<rus_helmet> {
    @Override
    public ResourceLocation getModelResource(rus_helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/armor/rus_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(rus_helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/rus_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(rus_helmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
}
