package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.mi24ukrEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class mi24ukrModel extends GeoModel<mi24ukrEntity> {

    @Override
    public ResourceLocation getAnimationResource(mi24ukrEntity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(mi24ukrEntity entity) {
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
    public ResourceLocation getTextureResource(mi24ukrEntity entity) {
        return VVP.loc("textures/entity/mi24ukr.png");
    }
}
