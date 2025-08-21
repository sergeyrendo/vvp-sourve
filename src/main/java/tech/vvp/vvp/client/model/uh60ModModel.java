package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Uh60Entity;
import tech.vvp.vvp.entity.vehicle.Uh60ModEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;

public class uh60ModModel extends GeoModel<Uh60ModEntity> {

    @Override
    public ResourceLocation getAnimationResource(Uh60ModEntity entity) {
       return VVP.loc("animations/uh60mod.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(Uh60ModEntity entity) {
        Player player = Minecraft.getInstance().player;

        int distance = 0;

        if (player != null) {
            distance = (int) player.position().distanceTo(entity.position());
        }

        if (distance < 32) {
            return VVP.loc("geo/uh60mod.geo.json");
        } else if (distance < 64) {
            return VVP.loc("geo/uh60mod.geo.json");
        } else if (distance < 96) {
            return VVP.loc("geo/uh60mod.geo.json");
        } else {
            return VVP.loc("geo/uh60mod.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(Uh60ModEntity animatable) {
        int camoType = animatable.getEntityData().get(Uh60ModEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/uh60mod.png");  // Песчаный
            case 2: return new ResourceLocation("vvp", "textures/entity/uh60med.png");  // Песчаный
            default: return new ResourceLocation("vvp", "textures/entity/uh60black.png");  // Лесной
        }
    }
}
