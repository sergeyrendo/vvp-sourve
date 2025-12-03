package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.BallisticMissileEntity;

public class BallisticMissileModel extends GeoModel<BallisticMissileEntity> {

    @Override
    public ResourceLocation getModelResource(BallisticMissileEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/himars_missile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BallisticMissileEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/projectile/himars_missile.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BallisticMissileEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/himars_missile.animation.json");
    }
}
