package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Su25Model;
import tech.vvp.vvp.entity.vehicle.Su25Entity;

public class Su25Renderer extends VehicleRenderer<Su25Entity> {
    public Su25Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Su25Model());
    }
}
