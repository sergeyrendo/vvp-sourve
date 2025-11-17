package tech.vvp.vvp.client.overlay;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.crewhelmet;

public class crewModel extends GeoModel<crewhelmet> {
    @Override
    public ResourceLocation getModelResource(crewhelmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/crew.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(crewhelmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/crew.png");
    }

    @Override
    public ResourceLocation getAnimationResource(crewhelmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 