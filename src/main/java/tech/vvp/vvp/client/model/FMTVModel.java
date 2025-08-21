package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

public class FMTVModel extends GeoModel<FMTVEntity> {

    @Override
    public ResourceLocation getAnimationResource(FMTVEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(FMTVEntity object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/fmtv.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FMTVEntity animatable) {
        int camoType = animatable.getEntityData().get(FMTVEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/fmtv_iraq.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/fmtv_green.png");  // Лесной
        }
    }
}