package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.ToyotaEntity;

public class ToyotaModel extends GeoModel<ToyotaEntity> {

    @Override
    public ResourceLocation getAnimationResource(ToyotaEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/humvee.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(ToyotaEntity object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/toyota.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ToyotaEntity entity) {
        return VVP.loc("textures/entity/toyota.png");
    }
}