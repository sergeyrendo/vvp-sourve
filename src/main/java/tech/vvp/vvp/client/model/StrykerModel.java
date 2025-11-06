package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;
public class StrykerModel extends GeoModel<StrykerEntity> {

    @Override
    public ResourceLocation getAnimationResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker_m1128.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StrykerEntity animatable) {
        int camoType = animatable.getEntityData().get(StrykerEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/strykers_haki.png");
            default: return new ResourceLocation("vvp", "textures/entity/strykers_green.png");
        }
    }
}