package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.CentauroEntity;

public class CentauroModel extends VehicleModel<CentauroEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
