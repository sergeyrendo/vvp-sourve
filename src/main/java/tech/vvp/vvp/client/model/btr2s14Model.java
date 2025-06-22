package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.btr2s14Entity;

public class btr2s14Model extends GeoModel<btr2s14Entity> {

    @Override
    public ResourceLocation getAnimationResource(btr2s14Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(btr2s14Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/2s14.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(btr2s14Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/2s14.png");
    }
} 