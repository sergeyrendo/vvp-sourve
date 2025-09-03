package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.T90_24Entity;

public class T90_24Model extends GeoModel<T90_24Entity> {

    @Override
    public ResourceLocation getAnimationResource(T90_24Entity entity) {
        return new ResourceLocation(Mod.MODID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T90_24Entity entity) {
        return VVP.loc("geo/t90_pror_2024.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T90_24Entity entity) {
        return VVP.loc("textures/entity/t90_green.png");
    }
}