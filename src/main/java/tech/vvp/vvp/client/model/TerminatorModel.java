package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.TerminatorEntity;

public class TerminatorModel extends GeoModel<TerminatorEntity> {

    @Override
    public ResourceLocation getAnimationResource(TerminatorEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(TerminatorEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/terminator.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TerminatorEntity animatable) {
        int camoType = animatable.getEntityData().get(TerminatorEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/terminator_haki.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/terminator.png");  // Лесной
        }
    }
}