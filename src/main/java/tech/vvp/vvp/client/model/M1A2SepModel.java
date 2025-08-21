package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M1A2SepEntity;

public class M1A2SepModel extends GeoModel<M1A2SepEntity> {

    @Override
    public ResourceLocation getAnimationResource(M1A2SepEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M1A2SepEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m1a2_sep.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M1A2SepEntity animatable) {
        int camoType = animatable.getEntityData().get(M1A2SepEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/m1a2_iraq.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/m1a2_camo.png");  // Лесной
        }
    }
}