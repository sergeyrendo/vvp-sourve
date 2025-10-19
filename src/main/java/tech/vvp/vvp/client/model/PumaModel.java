package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.PumaEntity;

public class PumaModel extends GeoModel<PumaEntity> {

    @Override
    public ResourceLocation getAnimationResource(PumaEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(PumaEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/puma.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PumaEntity animatable) {
        int camoType = animatable.getEntityData().get(PumaEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/puma_haki.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/puma_snow.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/puma_pink.png");
            default: return new ResourceLocation("vvp", "textures/entity/puma_green.png");
        }
    }
}