package tech.vvp.vvp.entity.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;

public class BallisticMissileWeapon extends VehicleWeapon {
    public BallisticMissileWeapon() {
        this.icon = new ResourceLocation(VVP.MOD_ID, "textures/screens/vehicle_weapon/himars_missile.png");
    }

    public BallisticMissileEntity create(LivingEntity entity) {
        return new BallisticMissileEntity(entity, entity.level());
    }
}
