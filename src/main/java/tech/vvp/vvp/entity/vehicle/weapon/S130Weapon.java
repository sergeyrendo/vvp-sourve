package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.entity.projectile.SmallRocketEntity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;

public class S130Weapon extends VehicleWeapon {

    public float damage = 300, explosionDamage = 80, explosionRadius = 8;

    public S130Weapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/s_130.png");
    }

    public S130Weapon damage(float damage) {
        this.damage = damage;
        return this;
    }

    public S130Weapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public S130Weapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public SmallRocketEntity create(LivingEntity entity) {
        return new SmallRocketEntity(entity, entity.level(), damage, explosionDamage, explosionRadius);
    }
}
