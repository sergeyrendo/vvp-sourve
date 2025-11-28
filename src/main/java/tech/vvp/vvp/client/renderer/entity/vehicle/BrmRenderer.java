package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.BrmModel;
import tech.vvp.vvp.entity.vehicle.BrmEntity;

public class BrmRenderer extends VehicleRenderer<BrmEntity> {
    public BrmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BrmModel());
    }
}
