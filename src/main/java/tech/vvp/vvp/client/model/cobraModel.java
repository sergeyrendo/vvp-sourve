package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.CobraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class cobraModel extends GeoModel<CobraEntity> {

    @Override
    public ResourceLocation getAnimationResource(CobraEntity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(CobraEntity entity) {
        Player player = Minecraft.getInstance().player;

        int distance = 0;

        if (player != null) {
            distance = (int) player.position().distanceTo(entity.position());
        }

        if (distance < 32) {
            return VVP.loc("geo/cobra.geo.json");
        } else if (distance < 64) {
            return VVP.loc("geo/cobra.geo.json");
        } else if (distance < 96) {
            return VVP.loc("geo/cobra.geo.json");
        } else {
            return VVP.loc("geo/cobra.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(CobraEntity entity) {
        return VVP.loc("textures/entity/cobra_basic.png");
    }
}
