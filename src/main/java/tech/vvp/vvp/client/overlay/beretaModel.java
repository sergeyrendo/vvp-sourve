package tech.vvp.vvp.client.overlay;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.bereta;

public class beretaModel extends GeoModel<bereta> {
    @Override
    public ResourceLocation getModelResource(bereta object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bereta.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(bereta object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/kepki.png");
    }

    @Override
    public ResourceLocation getAnimationResource(bereta animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 