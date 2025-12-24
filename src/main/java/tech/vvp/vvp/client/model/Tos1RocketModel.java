package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.entity.projectile.Tos1RocketEntity;

/**
 * Модель ракеты ТОС-1
 */
public class Tos1RocketModel extends GeoModel<Tos1RocketEntity> {

    @Override
    public ResourceLocation getAnimationResource(Tos1RocketEntity entity) {
        return Mod.loc("animations/rpg_rocket.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Tos1RocketEntity entity) {
        return Mod.loc("geo/small_rocket.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Tos1RocketEntity entity) {
        return Mod.loc("textures/entity/rpg_rocket.png");
    }
}
