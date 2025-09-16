package tech.vvp.vvp.client.model;


import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity;

public class CannonAtgmEntityModel extends GeoModel<CannonAtgmShellEntity> {

    @Override
    public ResourceLocation getAnimationResource(CannonAtgmShellEntity entity) {
        return Mod.loc("animations/cannon_shell.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(CannonAtgmShellEntity entity) {
        return Mod.loc("geo/cannon_shell.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CannonAtgmShellEntity entity) {
        return Mod.loc("textures/entity/cannon_shell.png");
    }

    @Override
    public void setCustomAnimations(CannonAtgmShellEntity animatable, long instanceId, AnimationState animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");
        bone.setHidden(animatable.tickCount <= 1);
    }
}
