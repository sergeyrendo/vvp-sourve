package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.HkGmgEntity;

public class HkGmgModel extends VehicleModel<HkGmgEntity> {

    @Override
    public ResourceLocation getModelResource(HkGmgEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "geo/hk_gmg.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HkGmgEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/hk_gmg.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HkGmgEntity animatable) {
        return null;
    }
}
