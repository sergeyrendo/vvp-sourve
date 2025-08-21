package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.HumveeEntity;

public class HumveeModel extends GeoModel<HumveeEntity> {

    @Override
    public ResourceLocation getAnimationResource(HumveeEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(HumveeEntity object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/humvee.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HumveeEntity animatable) {
        int camoType = animatable.getEntityData().get(HumveeEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/humvee_iraq.png");  // Песчаный
            case 2: return new ResourceLocation("vvp", "textures/entity/humvee_ukr.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/humvee_green.png");  // Лесной
        }
    }
}