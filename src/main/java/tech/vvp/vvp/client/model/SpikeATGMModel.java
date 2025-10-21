package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.SpikeATGMEntity;

public class SpikeATGMModel extends GeoModel<SpikeATGMEntity> {

    @Override
    public ResourceLocation getAnimationResource(SpikeATGMEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SpikeATGMEntity entity) {
        return VVP.loc("geo/spike.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpikeATGMEntity entity) {
        return VVP.loc("textures/entity/puma_green.png");
    }
}
