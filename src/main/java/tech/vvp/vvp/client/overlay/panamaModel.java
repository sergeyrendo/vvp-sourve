package tech.vvp.vvp.client.overlay;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.panama;

public class panamaModel extends GeoModel<panama> {
    @Override
    public ResourceLocation getModelResource(panama object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/panama.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(panama object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/kepki.png");
    }

    @Override
    public ResourceLocation getAnimationResource(panama animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 