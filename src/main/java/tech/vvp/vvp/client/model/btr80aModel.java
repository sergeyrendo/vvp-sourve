package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.Mod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;
import tech.vvp.vvp.entity.vehicle.Btr80aEntity;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

public class btr80aModel extends GeoModel<Btr80aEntity> {

    @Override
    public ResourceLocation getAnimationResource(Btr80aEntity entity) {
        return new ResourceLocation(Mod.MODID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Btr80aEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/btr80a.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Btr80aEntity animatable) {
        int camoType = animatable.getEntityData().get(Btr80aEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/btr80a_camo.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/btr80a_z.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/btr80a_ukr.png");
            default: return new ResourceLocation("vvp", "textures/entity/btr80a.png");
        }
    }
} 