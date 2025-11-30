package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.pmc_helmet;

public class pmc_helmetModel extends GeoModel<pmc_helmet> {
    @Override
    public ResourceLocation getModelResource(pmc_helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/armor/pmc_helmet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(pmc_helmet object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/pmc.png");
    }

    @Override
    public ResourceLocation getAnimationResource(pmc_helmet animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
} 