package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.R73Entity;

public class X25Model extends GeoModel<R73Entity> {

    @Override
    public ResourceLocation getAnimationResource(R73Entity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(R73Entity entity) {
        return VVP.loc("geo/x25.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(R73Entity entity) {
        return VVP.loc("textures/entity/x25.png");
    }
}
