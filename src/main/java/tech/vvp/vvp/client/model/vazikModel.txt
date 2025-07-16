package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.VazikEntity;

public class vazikModel extends GeoModel<VazikEntity> {

    @Override
    public ResourceLocation getAnimationResource(VazikEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(VazikEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/vazik.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VazikEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/vazik.png");
    }
} 