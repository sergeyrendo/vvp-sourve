package tech.vvp.vvp.client.renderer.entity.vehicle;

import tech.vvp.vvp.client.model.TowModel;
import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.entity.vehicle.TowEntity;

public class TowRenderer extends VehicleRenderer<TowEntity> {
    public TowRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TowModel());
    }
}