package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.F35Entity;

public class f35Model extends GeoModel<F35Entity> {

    @Override
    public ResourceLocation getAnimationResource(F35Entity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/f35.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(F35Entity object) {
        return new ResourceLocation(VVP.MOD_ID, "geo/f35.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(F35Entity object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/f35.png");
    }
} 