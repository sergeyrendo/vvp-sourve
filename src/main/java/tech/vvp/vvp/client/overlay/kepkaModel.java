package tech.vvp.vvp.client.overlay;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.kepka;

public class kepkaModel extends GeoModel<kepka> {
    @Override
    public ResourceLocation getModelResource(kepka object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/kepka.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(kepka object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/kepki.png");
    }

    @Override
    public ResourceLocation getAnimationResource(kepka animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 