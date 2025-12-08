package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Uh60Model;
import tech.vvp.vvp.entity.vehicle.Uh60Entity;

public class Uh60Renderer extends VehicleRenderer<Uh60Entity> {
    public Uh60Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Uh60Model());
    }
}
