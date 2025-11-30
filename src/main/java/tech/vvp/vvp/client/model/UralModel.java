package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.UralEntity;

public class UralModel extends VehicleModel<UralEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
