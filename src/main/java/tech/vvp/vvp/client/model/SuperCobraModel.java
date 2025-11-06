package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.SuperCobraEntity;
public class SuperCobraModel extends GeoModel<SuperCobraEntity> {

    @Override
    public ResourceLocation getAnimationResource(SuperCobraEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(SuperCobraEntity entity) {
        return VVP.loc("geo/ah1w.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SuperCobraEntity animatable) {
        int camoType = animatable.getEntityData().get(SuperCobraEntity.CAMOUFLAGE_TYPE);
        switch (camoType) {
            case 1: return new ResourceLocation("vvp", "textures/entity/ah1w_shark.png");
            case 2: return new ResourceLocation("vvp", "textures/entity/ah1w_dark.png");
            default: return new ResourceLocation("vvp", "textures/entity/ah1w.png");
        }
    }
}
