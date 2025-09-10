package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.mi28helmet;

public class mi28helmetModel extends GeoModel<mi28helmet> {
    @Override
    public ResourceLocation getModelResource(mi28helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/mi28_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(mi28helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/mi28_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(mi28helmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usahelmet.animation.json");
    }
} 