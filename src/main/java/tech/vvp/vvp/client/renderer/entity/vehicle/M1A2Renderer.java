package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.M1A2Model;
import tech.vvp.vvp.entity.vehicle.M1A2Entity;

public class M1A2Renderer extends VehicleRenderer<M1A2Entity> {
    public M1A2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new M1A2Model());
    }
}
