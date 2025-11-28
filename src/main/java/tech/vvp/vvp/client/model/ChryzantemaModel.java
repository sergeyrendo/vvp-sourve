package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.ChryzantemaEntity;

public class ChryzantemaModel extends VehicleModel<ChryzantemaEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
