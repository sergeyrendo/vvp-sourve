package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.Mod;
import tech.vvp.vvp.entity.projectile.Fab500Entity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;

public class Fab500Weapon extends VehicleWeapon {
    public Fab500Weapon() {
        this.icon = Mod.loc("textures/screens/vehicle_weapon/mk_82.png");
    }

    public Fab500Entity create(LivingEntity entity) {
        return new Fab500Entity(entity, entity.level());
    }
}
