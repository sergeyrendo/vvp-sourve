package tech.vvp.vvp.client.model;


import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Mi28Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class Mi28Model extends GeoModel<Mi28Entity> {

    @Override
    public ResourceLocation getAnimationResource(Mi28Entity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Mi28Entity entity) {
        Player player = Minecraft.getInstance().player;

        int distance = 0;

        if (player != null) {
            distance = (int) player.position().distanceTo(entity.position());
        }

        if (distance < 32) {
            return VVP.loc("geo/mi28.geo.json");
        } else if (distance < 64) {
            return VVP.loc("geo/mi28.geo.json");
        } else if (distance < 96) {
            return VVP.loc("geo/mi28.geo.json");
        } else {
            return VVP.loc("geo/mi28.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(Mi28Entity animatable) {
        int camoType = animatable.getEntityData().get(Mi28Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/mi28_camo.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/mi28_iraq.png");
            default: return new ResourceLocation("vvp", "textures/entity/mi28_black.png");
        }
    }
}
