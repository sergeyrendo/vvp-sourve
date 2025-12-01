package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.X25Entity;

public class X25Model extends GeoModel<X25Entity> {

    @Override
    public ResourceLocation getAnimationResource(X25Entity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(X25Entity entity) {
        return VVP.loc("geo/x25.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(X25Entity entity) {
        return VVP.loc("textures/entity/x25.png");
    }
}
