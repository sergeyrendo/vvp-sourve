package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.TerminatorModel;
import tech.vvp.vvp.entity.vehicle.TerminatorEntity;

public class TerminatorRenderer extends VehicleRenderer<TerminatorEntity> {
    public TerminatorRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TerminatorModel());
    }
}
