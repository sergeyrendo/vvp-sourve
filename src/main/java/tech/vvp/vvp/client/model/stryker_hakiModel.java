package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Stryker_hakiEntity;

public class stryker_hakiModel extends GeoModel<Stryker_hakiEntity> {

    @Override
    public ResourceLocation getAnimationResource(Stryker_hakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Stryker_hakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Stryker_hakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/stryker_haki.png");
    }
} 