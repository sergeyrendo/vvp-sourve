package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.PumaModel;
import tech.vvp.vvp.entity.vehicle.PumaEntity;

public class PumaRenderer extends VehicleRenderer<PumaEntity> {
    public PumaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PumaModel());
    }
}
