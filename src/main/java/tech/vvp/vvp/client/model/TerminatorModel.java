package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.TerminatorEntity;

public class TerminatorModel extends VehicleModel<TerminatorEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
