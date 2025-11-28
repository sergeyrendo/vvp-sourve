package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.Bmp3Entity;

public class Bmp3Model extends VehicleModel<Bmp3Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
