package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.BradleyEntity;

public class BradleyModel extends GeoModel<BradleyEntity> {

    @Override
    public ResourceLocation getAnimationResource(BradleyEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(BradleyEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bradley.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BradleyEntity animatable) {
        int camoType = animatable.getEntityData().get(BradleyEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/bradley_usa.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/bradley_green.png");  // Лесной
        }
    }
}