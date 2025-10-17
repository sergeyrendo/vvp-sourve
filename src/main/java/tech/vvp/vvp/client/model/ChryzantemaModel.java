package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.ChryzantemaEntity;

public class ChryzantemaModel extends GeoModel<ChryzantemaEntity> {

    @Override
    public ResourceLocation getAnimationResource(ChryzantemaEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(ChryzantemaEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/chryzantema.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ChryzantemaEntity animatable) {
        int camoType = animatable.getEntityData().get(ChryzantemaEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/brm_black.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/brm_camo.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/brm_snow.png");
            case 4: return new ResourceLocation("vvp", "textures/entity/brm_zvezda.png");
            default: return new ResourceLocation("vvp", "textures/entity/brm_green.png");
        }
    }
}