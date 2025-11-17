package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.T72B3MEntity;

public class T72B3MModel extends GeoModel<T72B3MEntity> {

    @Override
    public ResourceLocation getAnimationResource(T72B3MEntity entity) {
        return new ResourceLocation(Mod.MODID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T72B3MEntity entity) {
        return VVP.loc("geo/t72b3m.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T72B3MEntity animatable) {
        int camoType = animatable.getEntityData().get(T72B3MEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/t72b3m_camo.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/t72b3m_sandy.png");
            default: return new ResourceLocation("vvp", "textures/entity/t72b3m.png");
        }
    }
}