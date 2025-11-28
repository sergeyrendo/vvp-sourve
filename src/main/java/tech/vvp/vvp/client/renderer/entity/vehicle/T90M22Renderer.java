package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.T90M22Model;
import tech.vvp.vvp.entity.vehicle.T90M22Entity;

public class T90M22Renderer extends VehicleRenderer<T90M22Entity> {
    public T90M22Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T90M22Model());
    }
}
