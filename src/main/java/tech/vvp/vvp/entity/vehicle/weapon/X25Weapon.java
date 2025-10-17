package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.X25Entity;

public class X25Weapon extends VehicleWeapon {
    public X25Weapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/x_25.png");
    }

    public X25Entity create(LivingEntity entity) {
        return new X25Entity(entity, entity.level());
    }
}