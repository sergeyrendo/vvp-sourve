package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BrmEntity;

public class BrmModel extends GeoModel<BrmEntity> {

    @Override
    public ResourceLocation getAnimationResource(BrmEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BrmEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/brm.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BrmEntity animatable) {
        int camoType = animatable.getEntityData().get(BrmEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/brm_black.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/brm_camo.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/brm_snow.png");
            case 4: return new ResourceLocation("vvp", "textures/entity/brm_zvezda.png");
            default: return new ResourceLocation("vvp", "textures/entity/brm_green.png");
        }
    }
}