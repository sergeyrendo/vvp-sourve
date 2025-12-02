package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;

public class M142HimarsModel extends VehicleModel<M142HimarsEntity> {

    @Override
    public ResourceLocation getModelResource(M142HimarsEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m142_himars.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(M142HimarsEntity animatable) {
        int camoType = animatable.getEntityData().get(M142HimarsEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation(VVP.MOD_ID, "textures/entity/m142_himars_green_2.png");
            case 2: return new ResourceLocation(VVP.MOD_ID, "textures/entity/m142_himars_sandy.png");
            default: return new ResourceLocation(VVP.MOD_ID, "textures/entity/m142_himars_green.png");
        }
    }

    @Override
    public ResourceLocation getAnimationResource(M142HimarsEntity animatable) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
