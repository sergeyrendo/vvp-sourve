package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

public class FMTVModel extends VehicleModel<FMTVEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
