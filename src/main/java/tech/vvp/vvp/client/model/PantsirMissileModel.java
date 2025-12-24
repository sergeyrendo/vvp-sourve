package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.PantsirMissileEntity;

/**
 * Модель ракеты 57Э6 для Pantsir-S1
 */
public class PantsirMissileModel extends GeoModel<PantsirMissileEntity> {

    @Override
    public ResourceLocation getAnimationResource(PantsirMissileEntity entity) {
        return Mod.loc("animations/javelin_missile.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(PantsirMissileEntity entity) {
        // Используем модель igla как базу (можно заменить на свою)
        return Mod.loc("geo/igla_9k38_missile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PantsirMissileEntity entity) {
        return Mod.loc("textures/entity/igla_9k38.png");
    }
}
