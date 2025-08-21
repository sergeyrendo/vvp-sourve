package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;
import tech.vvp.vvp.entity.vehicle.Stryker_1Entity;

public class strykerModel extends GeoModel<StrykerEntity> {

    @Override
    public ResourceLocation getAnimationResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(StrykerEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StrykerEntity animatable) {
        int camoType = animatable.getEntityData().get(StrykerEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/stryker_haki.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/stryker.png");  // Лесной
        }
    }
} 