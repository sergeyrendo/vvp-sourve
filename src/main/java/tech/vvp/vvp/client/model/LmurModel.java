package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.LmurEntity;

public class LmurModel extends GeoModel<LmurEntity> {

    @Override
    public ResourceLocation getAnimationResource(LmurEntity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(LmurEntity entity) {
        return VVP.loc("geo/lmur.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LmurEntity entity) {
        return VVP.loc("textures/entity/lmur.png");
    }
}
