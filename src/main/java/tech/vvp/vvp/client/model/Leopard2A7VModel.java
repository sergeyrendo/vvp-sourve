package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Leopard2A7VEntity;

public class Leopard2A7VModel extends VehicleModel<Leopard2A7VEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
