package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.T90M22Entity;

public class T90M22Model extends GeoModel<T90M22Entity> {

    @Override
    public ResourceLocation getAnimationResource(T90M22Entity entity) {
        return new ResourceLocation(Mod.MODID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T90M22Entity entity) {
        return VVP.loc("geo/t90_pror_2022.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T90M22Entity animatable) {
        int camoType = animatable.getEntityData().get(T90M22Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/t90_camo.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/t90_snow.png");
            default: return new ResourceLocation("vvp", "textures/entity/t90_green.png");
        }
    }
}