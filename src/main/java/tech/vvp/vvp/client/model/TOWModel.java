package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.TOWEntity;

public class TOWModel extends GeoModel<TOWEntity> {

    @Override
    public ResourceLocation getAnimationResource(TOWEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(TOWEntity entity) {
        return VVP.loc("geo/tow.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TOWEntity entity) {
        return VVP.loc("textures/entity/tow.png");
    }
}
