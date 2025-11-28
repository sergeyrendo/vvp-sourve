package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.StrykerEntity;

public class StrykerModel extends VehicleModel<StrykerEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
