package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.FMTVModel;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

public class FMTVRenderer extends VehicleRenderer<FMTVEntity> {
    public FMTVRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FMTVModel());
    }
}
