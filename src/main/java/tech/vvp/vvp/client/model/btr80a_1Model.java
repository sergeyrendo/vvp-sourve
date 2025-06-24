package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Btr80a_1Entity;

public class btr80a_1Model extends GeoModel<Btr80a_1Entity> {

    @Override
    public ResourceLocation getAnimationResource(Btr80a_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Btr80a_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/btr80a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Btr80a_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/btr80a_1.png");
    }
} 