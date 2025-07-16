package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.TerminatorHakiEntity;

public class terminatorHakiModel extends GeoModel<TerminatorHakiEntity> {

    @Override
    public ResourceLocation getAnimationResource(TerminatorHakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(TerminatorHakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/terminator.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TerminatorHakiEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/terminator_haki.png");
    }
} 