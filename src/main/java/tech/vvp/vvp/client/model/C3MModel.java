package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.C3MEntity;

public class C3MModel extends GeoModel<C3MEntity> {

    @Override
    public ResourceLocation getAnimationResource(C3MEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(C3MEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/2c3m.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(C3MEntity animatable) {
        int camoType = animatable.getEntityData().get(C3MEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation(VVP.MOD_ID, "textures/entity/2c3m_2.png");
            case 2: return new ResourceLocation(VVP.MOD_ID, "textures/entity/2c3m_3.png");
            case 3: return new ResourceLocation(VVP.MOD_ID, "textures/entity/2c3m_4.png");
            default: return new ResourceLocation(VVP.MOD_ID, "textures/entity/2c3m_1.png");
        }
    }
}

