package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.m997Entity;

public class m997Model extends GeoModel<m997Entity> {

    @Override
    public ResourceLocation getAnimationResource(m997Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(m997Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m997.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(m997Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/m997.png");
    }
} 