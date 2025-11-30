package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Uh60WeaponEntity;

public class Uh60WeaponModel extends VehicleModel<Uh60WeaponEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
