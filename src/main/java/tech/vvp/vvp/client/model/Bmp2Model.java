package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Bmp2Entity;

public class Bmp2Model extends VehicleModel<Bmp2Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
