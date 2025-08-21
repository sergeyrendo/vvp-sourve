package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.CobraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

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
    public ResourceLocation getTextureResource(CobraEntity animatable) {
        int camoType = animatable.getEntityData().get(CobraEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/cobra_shark.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/cobra_basic.png");  // Лесной
        }
    }
}
