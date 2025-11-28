package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Btr4Model;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;

public class btr4Renderer extends VehicleRenderer<Btr4Entity> {
    public btr4Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Btr4Model());
    }
}
