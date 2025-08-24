package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import tech.vvp.vvp.entity.projectile.Mk19GrenadeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;

/**
 * Оружие для гранатомёта MK19 (40×53 мм).
 * Полная замена: создаёт Mk19GrenadeEntity, поддерживает тюнинг урона/радиуса/гравитации/взаимодействия с блоками.
 */
public class Mk19GrenadeWeapon extends VehicleWeapon {

    private float damage = 18f;
    private float explosionDamage = 28f;
    private float explosionRadius = 3.2f;
    private float gravity = 0.06f;
    private Explosion.BlockInteraction blockInteraction = null;

    public Mk19GrenadeWeapon damage(float v) { this.damage = v; return this; }
    public Mk19GrenadeWeapon explosionDamage(float v) { this.explosionDamage = v; return this; }
    public Mk19GrenadeWeapon explosionRadius(float v) { this.explosionRadius = v; return this; }
    public Mk19GrenadeWeapon gravity(float v) { this.gravity = v; return this; }
    public Mk19GrenadeWeapon blockInteraction(Explosion.BlockInteraction v) { this.blockInteraction = v; return this; }

    public Mk19GrenadeEntity create(LivingEntity shooter) {
        Mk19GrenadeEntity e = new Mk19GrenadeEntity(shooter, shooter.level(), damage, explosionDamage, explosionRadius);
        e.setGravity(gravity);
        if (blockInteraction != null) e.setBlockInteraction(blockInteraction);
        return e;
    }
}
