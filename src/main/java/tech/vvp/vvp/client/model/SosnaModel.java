package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.SosnaEntity;

public class SosnaModel extends GeoModel<SosnaEntity> {

    @Override
    public ResourceLocation getAnimationResource(SosnaEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(SosnaEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/sosna.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SosnaEntity animatable) {
        int camoType = animatable.getEntityData().get(SosnaEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/sosna_haki.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/sosna.png");  // Лесной
        }
    }
}
