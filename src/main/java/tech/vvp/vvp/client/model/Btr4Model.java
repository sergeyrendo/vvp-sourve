package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Btr4Entity;

public class Btr4Model extends VehicleModel<Btr4Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
