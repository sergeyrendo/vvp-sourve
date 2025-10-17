package tech.vvp.vvp.client.model;


import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.M1A2Entity;
import tech.vvp.vvp.entity.vehicle.Mi8Entity;

public class Mi8Model extends GeoModel<Mi8Entity> {

    @Override
    public ResourceLocation getAnimationResource(Mi8Entity entity) {
        return null;
//        return ModUtils.loc("animations/wheel_chair.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Mi8Entity entity) {
        Player player = Minecraft.getInstance().player;

        int distance = 0;

        if (player != null) {
            distance = (int) player.position().distanceTo(entity.position());
        }

        if (distance < 32) {
            return VVP.loc("geo/mi8_defolt.geo.json");
        } else if (distance < 64) {
            return VVP.loc("geo/mi8_defolt.geo.json");
        } else if (distance < 96) {
            return VVP.loc("geo/mi8_defolt.geo.json");
        } else {
            return VVP.loc("geo/mi8_defolt.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(Mi8Entity animatable) {
        int camoType = animatable.getEntityData().get(Mi8Entity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/mi8_rf.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/mi8_rf2.png");
            case 3: return new ResourceLocation("vvp", "textures/entity/mi8_rf3.png");
            case 4: return new ResourceLocation("vvp", "textures/entity/mi8_rf4.png");
            case 5: return new ResourceLocation("vvp", "textures/entity/mi8_ukr.png");
            case 6: return new ResourceLocation("vvp", "textures/entity/mi8_ukr2.png");
            default: return new ResourceLocation("vvp", "textures/entity/mi8.png");
        }
    }
}
