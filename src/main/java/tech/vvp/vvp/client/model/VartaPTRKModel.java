package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.VartaPTRKEntity;

public class VartaPTRKModel extends VehicleModel<VartaPTRKEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
