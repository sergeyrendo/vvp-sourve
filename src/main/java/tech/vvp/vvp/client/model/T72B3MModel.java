package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.T72B3MEntity;

public class T72B3MModel extends VehicleModel<T72B3MEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
