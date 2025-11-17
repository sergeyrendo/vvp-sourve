package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

public class PantsirS1Model extends GeoModel<PantsirS1Entity> {

    @Override
    public ResourceLocation getAnimationResource(PantsirS1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(PantsirS1Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/pantsir_s1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PantsirS1Entity animatable) {
        int camoType = animatable.getEntityData().get(PantsirS1Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/pantsir_s1_haki.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/pantsir_s1.png");  // Лесной
        }
    }
}