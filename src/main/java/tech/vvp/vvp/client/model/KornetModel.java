package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.KornetEntity;

public class KornetModel extends VehicleModel<KornetEntity> {

    @Override
    public ResourceLocation getModelResource(KornetEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "geo/kornet.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KornetEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/kornet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KornetEntity animatable) {
        return null;
    }
}
