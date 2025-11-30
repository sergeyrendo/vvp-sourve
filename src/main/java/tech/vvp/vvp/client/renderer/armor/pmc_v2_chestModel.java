package tech.vvp.vvp.client.renderer.armor;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.item.armor.pmc_v2_chest;

public class pmc_v2_chestModel extends GeoModel<pmc_v2_chest> {
    @Override
    public ResourceLocation getModelResource(pmc_v2_chest object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/armor/pmc_v2_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(pmc_v2_chest object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/armor/pmc.png");
    }

    @Override
    public ResourceLocation getAnimationResource(pmc_v2_chest animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/usachest.animation.json");
    }
} 