package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BushmasterEntity;

public class BushmasterModel extends GeoModel<BushmasterEntity> {

    @Override
    public ResourceLocation getAnimationResource(BushmasterEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BushmasterEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bushmaster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BushmasterEntity animatable) {
        int camoType = animatable.getEntityData().get(BushmasterEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/bushmaster_haki.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/bushmaster_camo.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/bushmaster_snow.png");
            default: return new ResourceLocation("vvp", "textures/entity/bushmaster_green.png");
        }
    }
}