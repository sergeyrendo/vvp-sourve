package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.TerminatorEntity;
import tech.vvp.vvp.entity.vehicle.VartaEntity;

public class VartaModel extends VehicleModel<VartaEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
