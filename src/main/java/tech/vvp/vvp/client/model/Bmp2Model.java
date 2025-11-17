package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Bmp2Entity;

public class Bmp2Model extends GeoModel<Bmp2Entity> {

    @Override
    public ResourceLocation getAnimationResource(Bmp2Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Bmp2Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/bmp2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Bmp2Entity animatable) {
        int camoType = animatable.getEntityData().get(Bmp2Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/bmp_2_camo.png");
            default: return new ResourceLocation("vvp", "textures/entity/bmp_2.png");
        }
    }
}