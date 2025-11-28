package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.BradleyModel;
import tech.vvp.vvp.entity.vehicle.BradleyEntity;

public class BradleyRenderer extends VehicleRenderer<BradleyEntity> {
    public BradleyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BradleyModel());
    }
}
