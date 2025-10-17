package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.S130Entity;

public class S130Model extends GeoModel<S130Entity> {

    @Override
    public ResourceLocation getAnimationResource(S130Entity entity) {
        return VVP.loc("animations/rpg_rocket.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(S130Entity entity) {
        return VVP.loc("geo/s130.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(S130Entity entity) {
        return VVP.loc("textures/entity/rockets.png");
    }
}
