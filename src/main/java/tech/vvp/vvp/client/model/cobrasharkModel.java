package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.CobrasharkEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class cobrasharkModel extends GeoModel<CobrasharkEntity> {

    @Override
    public ResourceLocation getAnimationResource(CobrasharkEntity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(CobrasharkEntity entity) {
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
    public ResourceLocation getTextureResource(CobrasharkEntity entity) {
        return VVP.loc("textures/entity/cobra_shark.png");
    }
}
