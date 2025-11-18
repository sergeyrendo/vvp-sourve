package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.entity.projectile.SosnaMissileEntity;

public class SosnaMissileModel extends GeoModel<SosnaMissileEntity> {

    @Override
    public ResourceLocation getModelResource(SosnaMissileEntity animatable) {
        return new ResourceLocation("vvp", "geo/sosna_missile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SosnaMissileEntity animatable) {
        return new ResourceLocation("vvp", "textures/entity/projectile/sosna_missile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SosnaMissileEntity animatable) {
        return new ResourceLocation("vvp", "animations/sosna_missile.animation.json");
    }
}


