package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.Mk19GrenadeEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class Mk19GrenadeModel extends GeoModel<Mk19GrenadeEntity> {

    @Override
    public ResourceLocation getAnimationResource(Mk19GrenadeEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(Mk19GrenadeEntity entity) {
        return VVP.loc("geo/small_cannon_shell.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Mk19GrenadeEntity entity) {
        return VVP.loc("textures/entity/small_cannon_shell.png");
    }

    @Override
    public void setCustomAnimations(Mk19GrenadeEntity animatable, long instanceId, AnimationState animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");
        bone.setScaleY((float) (1 + 2 * animatable.getDeltaMovement().length()));
    }
}