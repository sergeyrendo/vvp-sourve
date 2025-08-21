package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M997_greenEntity;
import tech.vvp.vvp.entity.vehicle.Uh60Entity;

public class m997_greenModel extends GeoModel<M997_greenEntity> {

    @Override
    public ResourceLocation getAnimationResource(M997_greenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M997_greenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m997.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M997_greenEntity animatable) {
        int camoType = animatable.getEntityData().get(M997_greenEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/m997.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/m997_green.png");  // Лесной
        }
    }
} 