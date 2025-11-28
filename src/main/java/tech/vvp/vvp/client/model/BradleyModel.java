package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.BradleyEntity;

public class BradleyModel extends VehicleModel<BradleyEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
