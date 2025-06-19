package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.bikegreenEntity;

public class bikegreenModel extends GeoModel<bikegreenEntity> {

    @Override
    public ResourceLocation getAnimationResource(bikegreenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(bikegreenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bikegreen.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(bikegreenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/bikegreen.png");
    }
} 