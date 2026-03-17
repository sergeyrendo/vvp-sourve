package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Btr3Entity;

public class Btr3Model extends VehicleModel<Btr3Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
