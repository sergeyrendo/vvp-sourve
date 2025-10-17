package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.T90MEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class T90MModel extends GeoModel<T90MEntity> {

    @Override
    public ResourceLocation getAnimationResource(T90MEntity entity) {
        return new ResourceLocation(Mod.MODID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T90MEntity entity) {
        return VVP.loc("geo/t90ms.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T90MEntity animatable) {
        int camoType = animatable.getEntityData().get(T90MEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/t90_camo.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/t90_snow.png");
            default: return new ResourceLocation("vvp", "textures/entity/t90_green.png");
        }
    }
}