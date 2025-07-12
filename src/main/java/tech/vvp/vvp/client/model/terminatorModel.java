package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.TerminatorEntity;

public class terminatorModel extends GeoModel<TerminatorEntity> {

    @Override
    public ResourceLocation getAnimationResource(TerminatorEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(TerminatorEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/terminator.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TerminatorEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/terminator.png");
    }
} 