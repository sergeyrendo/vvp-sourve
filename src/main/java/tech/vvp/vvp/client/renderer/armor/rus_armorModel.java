package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.rus_armor;

public class rus_armorModel extends GeoModel<rus_armor> {
    @Override
    public ResourceLocation getModelResource(rus_armor object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/armor/rus_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(rus_armor object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/rus_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(rus_armor animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
}
