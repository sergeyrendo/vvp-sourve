package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.multicamhelmet;

public class multicamhelmetModel extends GeoModel<multicamhelmet> {
    @Override
    public ResourceLocation getModelResource(multicamhelmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/multicamhelmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(multicamhelmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/multicamhelmet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(multicamhelmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 