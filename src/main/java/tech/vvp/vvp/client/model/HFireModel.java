package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.HFireEntity;

public class HFireModel extends GeoModel<HFireEntity> {

    @Override
    public ResourceLocation getAnimationResource(HFireEntity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(HFireEntity entity) {
        return VVP.loc("geo/hfire.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HFireEntity entity) {
        return VVP.loc("textures/entity/hfire.png");
    }
}
