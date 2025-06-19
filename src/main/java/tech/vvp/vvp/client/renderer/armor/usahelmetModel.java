package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.usahelmet;

public class usahelmetModel extends GeoModel<usahelmet> {
    @Override
    public ResourceLocation getModelResource(usahelmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/usa_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(usahelmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/usa_helmet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(usahelmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 