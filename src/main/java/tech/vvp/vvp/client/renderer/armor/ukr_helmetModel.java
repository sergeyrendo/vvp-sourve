package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.ukr_helmet;

public class ukr_helmetModel extends GeoModel<ukr_helmet> {
    @Override
    public ResourceLocation getModelResource(ukr_helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/armor/ukr_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ukr_helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/ukr.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ukr_helmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
} 