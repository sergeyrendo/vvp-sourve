package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.PumaEntity;

public class PumaModel extends VehicleModel<PumaEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
