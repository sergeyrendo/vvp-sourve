package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.F16Model;
import tech.vvp.vvp.entity.vehicle.F16Entity;

public class F16Renderer extends VehicleRenderer<F16Entity> {
    public F16Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new F16Model());
    }
}
