package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.vazikEntity;

public class vazikModel extends GeoModel<vazikEntity> {

    @Override
    public ResourceLocation getAnimationResource(vazikEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(vazikEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/vazik.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(vazikEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/vazik.png");
    }
} 