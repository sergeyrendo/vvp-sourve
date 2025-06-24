package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M997Entity;

public class m997Model extends GeoModel<M997Entity> {

    @Override
    public ResourceLocation getAnimationResource(M997Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M997Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m997.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M997Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/m997.png");
    }
} 