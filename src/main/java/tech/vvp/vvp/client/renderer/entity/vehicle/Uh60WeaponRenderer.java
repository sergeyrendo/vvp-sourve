package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import tech.vvp.vvp.client.model.Uh60WeaponModel;
import tech.vvp.vvp.entity.vehicle.Uh60WeaponEntity;

public class Uh60WeaponRenderer extends VehicleRenderer<Uh60WeaponEntity> {
    public Uh60WeaponRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Uh60WeaponModel());
    }
}
