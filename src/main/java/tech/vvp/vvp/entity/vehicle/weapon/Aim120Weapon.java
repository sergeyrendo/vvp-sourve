package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.Aim120Entity;

public class Aim120Weapon extends VehicleWeapon {
    public Aim120Weapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/aim_120.png");
    }

    public Aim120Entity create(LivingEntity entity) {
        return new Aim120Entity(entity, entity.level());
    }
}
