package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Su25Entity;

public class Su25Model extends VehicleModel<Su25Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
