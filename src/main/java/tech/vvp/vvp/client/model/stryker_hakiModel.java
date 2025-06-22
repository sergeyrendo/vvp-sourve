package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.stryker_hakiEntity;

public class stryker_hakiModel extends GeoModel<stryker_hakiEntity> {

    @Override
    public ResourceLocation getAnimationResource(stryker_hakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(stryker_hakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(stryker_hakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/stryker_haki.png");
    }
} 