package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.BrmEntity;

public class BrmModel extends VehicleModel<BrmEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
