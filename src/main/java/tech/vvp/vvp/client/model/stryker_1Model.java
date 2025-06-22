package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.stryker_1Entity;

public class stryker_1Model extends GeoModel<stryker_1Entity> {

    @Override
    public ResourceLocation getAnimationResource(stryker_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(stryker_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker_1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(stryker_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/stryker_1.png");
    }
} 