package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.TOWEntity;

public class TOWWeapon extends VehicleWeapon {

    public float damage = 350f, explosionDamage = 300f, explosionRadius = 20f;

    public TOWWeapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/tow.png");
    }

    public TOWWeapon damage(float damage) {
        this.damage = damage;
        return this;
    }

    public TOWWeapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public TOWWeapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public TOWEntity create(LivingEntity entity) {
        return new TOWEntity(entity, entity.level(), damage, explosionDamage, explosionRadius);
    }
}
