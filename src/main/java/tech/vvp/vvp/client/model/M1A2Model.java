package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BradleyUkrEntity;
import tech.vvp.vvp.entity.vehicle.M1A2Entity;

public class M1A2Model extends GeoModel<M1A2Entity> {

    @Override
    public ResourceLocation getAnimationResource(M1A2Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M1A2Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m1a2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M1A2Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/m1a2_camo.png");
    }
}