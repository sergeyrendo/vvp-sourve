
package tech.vvp.vvp.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.HryzantemaEntity;

public class HryzantemaModel extends GeoModel<HryzantemaEntity> {

    @Override
    public ResourceLocation getAnimationResource(HryzantemaEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(HryzantemaEntity entity) {
        return VVP.loc("geo/hryzantema.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HryzantemaEntity entity) {
        return VVP.loc("textures/entity/mi28_camo.png");
    }
}
