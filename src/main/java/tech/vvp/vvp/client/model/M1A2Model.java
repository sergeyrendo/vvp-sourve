package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.M1A2Entity;

public class M1A2Model extends VehicleModel<M1A2Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
