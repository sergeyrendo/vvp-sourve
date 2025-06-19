package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.btr80a_1Entity;

public class btr80a_1Model extends GeoModel<btr80a_1Entity> {

    @Override
    public ResourceLocation getAnimationResource(btr80a_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(btr80a_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/btr80a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(btr80a_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/btr80a_1.png");
    }
} 