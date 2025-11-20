package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.Aim120Entity;

public class Aim120Model extends GeoModel<Aim120Entity> {
    @Override
    public ResourceLocation getModelResource(Aim120Entity animatable) {
        return VVP.loc("geo/entity/aim_120.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Aim120Entity animatable) {
        return VVP.loc("textures/entity/f-16.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Aim120Entity animatable) {
        return VVP.loc("animations/aim_120.animation.json");
    }
}
