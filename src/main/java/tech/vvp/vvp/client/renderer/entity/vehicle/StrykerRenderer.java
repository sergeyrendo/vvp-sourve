package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.StrykerModel;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;

public class StrykerRenderer extends VehicleRenderer<StrykerEntity> {
    public StrykerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StrykerModel());
    }
}
