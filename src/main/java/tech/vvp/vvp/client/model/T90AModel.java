package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.T90AEntity;

public class T90AModel extends VehicleModel<T90AEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
