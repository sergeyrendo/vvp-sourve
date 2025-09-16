package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.KornetEntity;

public class KornetModel extends GeoModel<KornetEntity> {

    @Override
    public ResourceLocation getAnimationResource(KornetEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(KornetEntity entity) {
        return VVP.loc("geo/kornet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KornetEntity entity) {
        return VVP.loc("textures/entity/kornet.png");
    }
}
