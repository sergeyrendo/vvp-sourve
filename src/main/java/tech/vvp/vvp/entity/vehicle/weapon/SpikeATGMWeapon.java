package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.projectile.SpikeATGMEntity;

public class SpikeATGMWeapon extends VehicleWeapon {

    public float damage = 350f, explosionDamage = 300f, explosionRadius = 20f;

    public SpikeATGMWeapon() {
        this.icon = VVP.loc("textures/screens/vehicle_weapon/spike.png");
    }

    public SpikeATGMWeapon damage(float damage) {
        this.damage = damage;
        return this;
    }

    public SpikeATGMWeapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public SpikeATGMWeapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public SpikeATGMEntity create(LivingEntity entity) {
        return new SpikeATGMEntity(entity, entity.level(), damage, explosionDamage, explosionRadius);
    }
}
