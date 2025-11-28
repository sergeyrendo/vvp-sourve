package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.BushmasterModel;
import tech.vvp.vvp.entity.vehicle.BushmasterEntity;

public class BushmasterRenderer extends VehicleRenderer<BushmasterEntity> {
    public BushmasterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BushmasterModel());
    }
}
