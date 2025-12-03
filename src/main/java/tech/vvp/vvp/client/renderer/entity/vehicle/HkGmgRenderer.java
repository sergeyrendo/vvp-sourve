package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.HkGmgModel;
import tech.vvp.vvp.entity.vehicle.HkGmgEntity;

public class HkGmgRenderer extends VehicleRenderer<HkGmgEntity> {

    public HkGmgRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HkGmgModel());
    }
}
