package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.bikeredEntity;

public class bikeredModel extends GeoModel<bikeredEntity> {

    @Override
    public ResourceLocation getAnimationResource(bikeredEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(bikeredEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bikered.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(bikeredEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/bikered.png");
    }
} 