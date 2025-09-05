package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.Fab250Entity;

public class Fab250Model extends GeoModel<Fab250Entity> {

    @Override
    public ResourceLocation getAnimationResource(Fab250Entity entity) {
        return Mod.loc("animations/mk82.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Fab250Entity entity) {
        return VVP.loc("geo/fab500.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Fab250Entity entity) {
        return VVP.loc("textures/entity/weapon_pack_mi28.png");
    }
}
