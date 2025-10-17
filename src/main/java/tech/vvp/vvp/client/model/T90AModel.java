package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.T90AEntity;

public class T90AModel extends GeoModel<T90AEntity> {

    @Override
    public ResourceLocation getAnimationResource(T90AEntity entity) {
        return new ResourceLocation(Mod.MODID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T90AEntity entity) {
        return VVP.loc("geo/t90a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T90AEntity animatable) {
        int camoType = animatable.getEntityData().get(T90AEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/t90a_camo.png");
            default: return new ResourceLocation("vvp", "textures/entity/t90a_green.png");
        }
    }
}