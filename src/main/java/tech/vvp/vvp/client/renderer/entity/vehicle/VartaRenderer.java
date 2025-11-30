package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.VartaModel;
import tech.vvp.vvp.entity.vehicle.VartaEntity;

public class VartaRenderer extends VehicleRenderer<VartaEntity> {
    public VartaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VartaModel());
    }
}
