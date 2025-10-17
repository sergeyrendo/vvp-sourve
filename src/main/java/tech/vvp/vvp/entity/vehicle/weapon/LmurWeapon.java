package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.projectile.Agm65Entity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.LmurEntity;

public class LmurWeapon extends VehicleWeapon {
    public LmurWeapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/lmur.png");
    }

    public LmurEntity create(LivingEntity entity) {
        return new LmurEntity(entity, entity.level());
    }
}