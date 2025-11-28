package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.M1A2SepEntity;

public class M1A2SepModel extends VehicleModel<M1A2SepEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
