package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;

public class btr4Model extends GeoModel<Btr4Entity> {

    @Override
    public ResourceLocation getAnimationResource(Btr4Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Btr4Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/btr4.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Btr4Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/btr4.png");
    }
} 