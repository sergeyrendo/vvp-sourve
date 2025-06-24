package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;

public class strykerModel extends GeoModel<StrykerEntity> {

    @Override
    public ResourceLocation getAnimationResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/stryker.png");
    }
} 