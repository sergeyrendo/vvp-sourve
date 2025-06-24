package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BikeredEntity;

public class bikeredModel extends GeoModel<BikeredEntity> {

    @Override
    public ResourceLocation getAnimationResource(BikeredEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BikeredEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bikered.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BikeredEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/bikered.png");
    }
} 