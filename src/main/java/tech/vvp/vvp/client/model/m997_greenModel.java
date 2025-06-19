package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.m997_greenEntity;

public class m997_greenModel extends GeoModel<m997_greenEntity> {

    @Override
    public ResourceLocation getAnimationResource(m997_greenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/lav.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(m997_greenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/m997.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(m997_greenEntity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/m997_green.png");
    }
} 