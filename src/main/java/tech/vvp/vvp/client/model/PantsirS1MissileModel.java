package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.entity.projectile.PantsirS1MissileEntity;

public class PantsirS1MissileModel extends GeoModel<PantsirS1MissileEntity> {

    @Override
    public ResourceLocation getModelResource(PantsirS1MissileEntity animatable) {
        return new ResourceLocation("vvp", "geo/pantsir_s1_missile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PantsirS1MissileEntity animatable) {
        return new ResourceLocation("vvp", "textures/entity/projectile/pantsir_s1_missile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PantsirS1MissileEntity animatable) {
        return new ResourceLocation("vvp", "animations/pantsir_s1_missile.animation.json");
    }
}
