package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Bmp2MEntity;

public class Bmp2MModel extends VehicleModel<Bmp2MEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
