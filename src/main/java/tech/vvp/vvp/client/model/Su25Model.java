package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Su25Entity;

public class Su25Model extends GeoModel<Su25Entity> {

    @Override
    public ResourceLocation getAnimationResource(Su25Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Su25Entity entity) {
        return VVP.loc("geo/su25_ru.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Su25Entity animatable) {
        int camoType = animatable.getEntityData().get(Su25Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/su25_ru2.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/su25_ukr.png");
            default: return new ResourceLocation("vvp", "textures/entity/su25_ru.png");
        }
    }
}
