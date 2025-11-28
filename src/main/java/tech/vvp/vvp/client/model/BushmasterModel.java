package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.BushmasterEntity;

public class BushmasterModel extends VehicleModel<BushmasterEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
