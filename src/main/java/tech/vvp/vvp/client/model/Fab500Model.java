package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.Fab500Entity;

public class Fab500Model extends GeoModel<Fab500Entity> {

    @Override
    public ResourceLocation getAnimationResource(Fab500Entity entity) {
        return Mod.loc("animations/mk82.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Fab500Entity entity) {
        return VVP.loc("geo/fab500.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Fab500Entity entity) {
        return VVP.loc("textures/entity/weapon_pack_mi28.png");
    }
}
