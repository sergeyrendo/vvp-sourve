package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Stryker_M1296Entity;

public class Stryker_M1296Model extends GeoModel<Stryker_M1296Entity> {

    @Override
    public ResourceLocation getAnimationResource(Stryker_M1296Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Stryker_M1296Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/stryker_1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Stryker_M1296Entity animatable) {
        int camoType = animatable.getEntityData().get(Stryker_M1296Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/stryker_1_haki.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/stryker_1.png");  // Лесной
        }
    }
}