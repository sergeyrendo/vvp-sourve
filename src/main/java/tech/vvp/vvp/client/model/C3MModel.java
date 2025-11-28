package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.C3MEntity;

public class C3MModel extends VehicleModel<C3MEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
