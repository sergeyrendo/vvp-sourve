package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BikegreenEntity;

public class bikegreenModel extends GeoModel<BikegreenEntity> {

    @Override
    public ResourceLocation getAnimationResource(BikegreenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BikegreenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bikegreen.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BikegreenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/bikegreen.png");
    }
} 