package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Mi28Model;
import tech.vvp.vvp.entity.vehicle.Mi28Entity;

public class Mi28Renderer extends VehicleRenderer<Mi28Entity> {
    public Mi28Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Mi28Model());
    }
}
