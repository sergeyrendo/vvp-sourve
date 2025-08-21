package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M60Entity;

public class M60Model extends GeoModel<M60Entity> {

    @Override
    public ResourceLocation getAnimationResource(M60Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M60Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m60.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M60Entity animatable) {
        int camoType = animatable.getEntityData().get(M60Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/m60_camo.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/m60_green.png");  // Лесной
        }
    }
}