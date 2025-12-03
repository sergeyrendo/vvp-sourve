package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Mi24Model;
import tech.vvp.vvp.entity.vehicle.Mi24Entity;

public class Mi24Renderer extends VehicleRenderer<Mi24Entity> {
    public Mi24Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mi24Model());
    }
}
