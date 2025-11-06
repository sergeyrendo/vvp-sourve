package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.TU22M3Entity;

public class TU22M3Model extends GeoModel<TU22M3Entity> {

    @Override
    public ResourceLocation getAnimationResource(TU22M3Entity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(TU22M3Entity entity) {
        return VVP.loc("geo/tu22m3.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TU22M3Entity animatable) {
        return new ResourceLocation("vvp", "textures/entity/tu22m3.png");
    }
}
