package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Uh60ModModel;
import tech.vvp.vvp.entity.vehicle.Uh60ModEntity;

public class Uh60ModRenderer extends VehicleRenderer<Uh60ModEntity> {
    public Uh60ModRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Uh60ModModel());
    }
}
