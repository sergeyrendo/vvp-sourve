package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.projectile.Agm65Entity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.HFireEntity;

public class HFireWeapon extends VehicleWeapon {
    public HFireWeapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/hfire.png");
    }

    public HFireEntity create(LivingEntity entity) {
        return new HFireEntity(entity, entity.level());
    }
}