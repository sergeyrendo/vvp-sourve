package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.ToyotaModel;
import tech.vvp.vvp.entity.vehicle.ToyotaEntity;

public class ToyotaRenderer extends VehicleRenderer<ToyotaEntity> {
    public ToyotaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ToyotaModel());
    }
}
