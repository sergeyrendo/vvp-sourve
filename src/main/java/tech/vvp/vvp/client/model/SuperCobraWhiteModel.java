package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.SuperCobraWhiteEntity;

public class SuperCobraWhiteModel extends GeoModel<SuperCobraWhiteEntity> {

    @Override
    public ResourceLocation getAnimationResource(SuperCobraWhiteEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SuperCobraWhiteEntity entity) {
        return VVP.loc("geo/cobra_dark.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SuperCobraWhiteEntity animatable) {
        int camoType = animatable.getEntityData().get(SuperCobraWhiteEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/cobra_shark.png");
            default: return new ResourceLocation("vvp", "textures/entity/cobra_white.png");
        }
    }
}
