package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.A2cm3Entity;

public class A2cm3Model extends GeoModel<A2cm3Entity> {

    @Override
    public ResourceLocation getAnimationResource(A2cm3Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(A2cm3Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/2c3m.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(A2cm3Entity animatable) {
        return new ResourceLocation("vvp", "textures/entity/2c3m_3.png");  // Лесной
    }
}