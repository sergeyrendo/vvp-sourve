package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Mi8Model;
import tech.vvp.vvp.entity.vehicle.Mi8Entity;

public class Mi8Renderer extends VehicleRenderer<Mi8Entity> {
    public Mi8Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mi8Model());
    }
}
