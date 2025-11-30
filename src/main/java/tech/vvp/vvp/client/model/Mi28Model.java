package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Mi28Entity;

public class Mi28Model extends VehicleModel<Mi28Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
