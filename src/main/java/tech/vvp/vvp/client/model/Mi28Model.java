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
            return VVP.loc("geo/mi28_ver1.geo.json");
        } else if (distance < 64) {
            return VVP.loc("geo/mi28_ver1.geo.json");
        } else if (distance < 96) {
            return VVP.loc("geo/mi28_ver1.geo.json");
        } else {
            return VVP.loc("geo/mi28_ver1.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(Mi28Entity object) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/mi28_ver1.png");
    }
}
