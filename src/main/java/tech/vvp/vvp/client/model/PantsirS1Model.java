package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

public class PantsirS1Model extends VehicleModel<PantsirS1Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
