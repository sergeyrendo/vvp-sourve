package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.btr80aEntity;

public class btr80aModel extends GeoModel<btr80aEntity> {

    @Override
    public ResourceLocation getAnimationResource(btr80aEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(btr80aEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/btr80a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(btr80aEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/btr80a.png");
    }
} 