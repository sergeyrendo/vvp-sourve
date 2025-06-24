package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Mi24polEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class mi24polModel extends GeoModel<Mi24polEntity> {

    @Override
    public ResourceLocation getAnimationResource(Mi24polEntity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Mi24polEntity entity) {
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
    public ResourceLocation getTextureResource(Mi24polEntity entity) {
        return VVP.loc("textures/entity/mi24pl.png");
    }
}
