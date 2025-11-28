package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.T90MEntity;

public class T90MModel extends VehicleModel<T90MEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
