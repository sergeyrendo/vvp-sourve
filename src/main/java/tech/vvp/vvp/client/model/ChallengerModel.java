package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import tech.vvp.vvp.entity.vehicle.ChallengerEntity;

public class ChallengerModel extends VehicleModel<ChallengerEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }
}
