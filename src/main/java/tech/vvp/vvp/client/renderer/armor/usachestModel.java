package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.usachest;

public class usachestModel extends GeoModel<usachest> {
    @Override
    public ResourceLocation getModelResource(usachest object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/usa_chest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(usachest object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/usa_chest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(usachest animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
} 