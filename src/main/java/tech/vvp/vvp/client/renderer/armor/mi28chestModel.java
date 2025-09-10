package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.mi28chest;

public class mi28chestModel extends GeoModel<mi28chest> {
    @Override
    public ResourceLocation getModelResource(mi28chest object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/mi28_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(mi28chest object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/mi28_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(mi28chest animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
} 