package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.M337Entity;

public class M337Model extends GeoModel<M337Entity> {

    @Override
    public ResourceLocation getAnimationResource(M337Entity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M337Entity entity) {
        // Пока используем модель X25, потом можно заменить на m337.geo.json
        return VVP.loc("geo/x25.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M337Entity entity) {
        // Пока используем текстуру X25, потом можно заменить на m337.png
        return VVP.loc("textures/entity/x25.png");
    }
}
