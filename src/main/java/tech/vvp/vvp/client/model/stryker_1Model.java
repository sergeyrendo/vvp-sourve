package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Stryker_1Entity;

public class stryker_1Model extends GeoModel<Stryker_1Entity> {

    @Override
    public ResourceLocation getAnimationResource(Stryker_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Stryker_1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker_1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Stryker_1Entity animatable) {
        int camoType = animatable.getEntityData().get(Stryker_1Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/stryker_1_haki.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/stryker_1.png");  // Лесной
        }
    }
} 