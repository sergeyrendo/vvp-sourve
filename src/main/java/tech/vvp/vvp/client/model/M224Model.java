package tech.vvp.vvp.client.model;

import tech.vvp.vvp.VVP;
import com.atsuishio.superbwarfare.client.RenderHelper;
import tech.vvp.vvp.entity.vehicle.M224Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class M224Model extends GeoModel<M224Entity> {

    @Override
    public ResourceLocation getAnimationResource(M224Entity entity) {
        return VVP.loc("animations/m224.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(M224Entity entity) {
        if (RenderHelper.isInGui()) {
            return VVP.loc("geo/m224.geo.json");
        }

        Player player = Minecraft.getInstance().player;

        int distance = 0;

        if (player != null) {
            distance = (int) player.position().distanceTo(entity.position());
        }

        if (distance < 48 || player.isScoping()) {
            return VVP.loc("geo/m224.geo.json");
        } else  {
            return VVP.loc("geo/m224.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(M224Entity entity) {
        return VVP.loc("textures/entity/m224.png");
    }

//    @Override
//    public void setCustomAnimations(M224Entity animatable, long instanceId, AnimationState<M224Entity> animationState) {
//        CoreGeoBone head = getAnimationProcessor().getBone("paoguan");
//        CoreGeoBone jiaojia = getAnimationProcessor().getBone("jiaojia");
//        if (head != null) {
//            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//            head.setRotX((entityData.headPitch()) * Mth.DEG_TO_RAD);
//            jiaojia.setRotX(-2 * ((entityData.headPitch() - (10 - entityData.headPitch() * 0.1f)) * Mth.DEG_TO_RAD));
//        }
//    }
}
