package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Mi24Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class mi24Model extends GeoModel<Mi24Entity> {

    @Override
    public ResourceLocation getAnimationResource(Mi24Entity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Mi24Entity entity) {
        Player player = Minecraft.getInstance().player;

        int distance = 0;

        if (player != null) {
            distance = (int) player.position().distanceTo(entity.position());
        }

        if (distance < 32) {
            return VVP.loc("geo/mi24.geo.json");
        } else if (distance < 64) {
            return VVP.loc("geo/mi24.geo.json");
        } else if (distance < 96) {
            return VVP.loc("geo/mi24.geo.json");
        } else {
            return VVP.loc("geo/mi24.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(Mi24Entity animatable) {
        int camoType = animatable.getEntityData().get(Mi24Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/mi24ukr.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/mi24rf.png");  // Лесной
        }
    }
}
