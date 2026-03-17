package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;

import tech.vvp.vvp.entity.vehicle.PautinaEntity;

public class PautinaModel extends VehicleModel<PautinaEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

}
