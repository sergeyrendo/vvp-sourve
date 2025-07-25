package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BradleyUkrEntity;

public class BradleyUkrModel extends GeoModel<BradleyUkrEntity> {

    @Override
    public ResourceLocation getAnimationResource(BradleyUkrEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BradleyUkrEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bradley_ukr.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BradleyUkrEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/bradley_ukr.png");
    }
}