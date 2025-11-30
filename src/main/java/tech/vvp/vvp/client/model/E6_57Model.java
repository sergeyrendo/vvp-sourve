package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.E6_57Entity;

public class E6_57Model extends GeoModel<E6_57Entity> {

    @Override
    public ResourceLocation getAnimationResource(E6_57Entity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(E6_57Entity entity) {
        return VVP.loc("geo/x25.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(E6_57Entity entity) {
        return VVP.loc("textures/entity/x25.png");
    }
}
