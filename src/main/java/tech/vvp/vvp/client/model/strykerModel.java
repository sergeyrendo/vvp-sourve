package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.strykerEntity;

public class strykerModel extends GeoModel<strykerEntity> {

    @Override
    public ResourceLocation getAnimationResource(strykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(strykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(strykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/stryker.png");
    }
} 