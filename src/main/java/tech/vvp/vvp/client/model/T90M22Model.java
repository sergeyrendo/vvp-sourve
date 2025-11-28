package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.T90M22Entity;

public class T90M22Model extends VehicleModel<T90M22Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
