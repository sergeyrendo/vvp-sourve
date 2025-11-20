package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.F16Entity;

public class F16Model extends GeoModel<F16Entity> {

    @Override
    public ResourceLocation getAnimationResource(F16Entity entity) {
        return VVP.loc("animations/f-16gear.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(F16Entity entity) {
        return VVP.loc("geo/f-16.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(F16Entity animatable) {
        int camoType = animatable.getEntityData().get(F16Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/f-16_camo1.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/f-16_camo2.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/f-16_camo3.png");
            case 4: return new ResourceLocation("vvp", "textures/entity/f-16_camo4.png");
            case 5: return new ResourceLocation("vvp", "textures/entity/f-16_camo5.png");
            default: return new ResourceLocation("vvp", "textures/entity/f-16.png");
        }
    }
}
