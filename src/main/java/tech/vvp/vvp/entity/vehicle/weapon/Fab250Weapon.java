package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.entity.projectile.Fab250Entity;

public class Fab250Weapon extends VehicleWeapon {
    public Fab250Weapon() {
        this.icon = Mod.loc("textures/screens/vehicle_weapon/mk_82.png");
    }

    public Fab250Entity create(LivingEntity entity) {
        return new Fab250Entity(entity, entity.level());
    }
}
