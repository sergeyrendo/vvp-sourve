package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.multicamchest;

public class multicamchestModel extends GeoModel<multicamchest> {
    @Override
    public ResourceLocation getModelResource(multicamchest object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/multicamchest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(multicamchest object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/multicamhelmet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(multicamchest animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
} 