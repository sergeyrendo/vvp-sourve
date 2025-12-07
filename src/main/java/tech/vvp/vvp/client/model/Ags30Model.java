package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Ags30Entity;

public class Ags30Model extends VehicleModel<Ags30Entity> {

    @Override
    public ResourceLocation getModelResource(Ags30Entity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "geo/ags_30.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Ags30Entity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/ags_30.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Ags30Entity animatable) {
        return null;
    }
}
