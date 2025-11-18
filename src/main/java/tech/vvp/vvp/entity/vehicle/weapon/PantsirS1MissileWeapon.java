package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.projectile.E6_57Entity;

public class PantsirS1MissileWeapon extends VehicleWeapon {

    private float damage;
    private float explosionDamage;
    private float explosionRadius;

    public PantsirS1MissileWeapon() {
        this.icon = new ResourceLocation("vvp", "textures/screens/vehicle_weapon/e6_57.png");
    }

    public PantsirS1MissileWeapon damage(float damage) {
        this.damage = damage;
        return this;
    }

    public PantsirS1MissileWeapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public PantsirS1MissileWeapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public E6_57Entity create(LivingEntity shooter, int guideType, @Nullable Vec3 targetPos, boolean topAttack) {
        E6_57Entity missile = new E6_57Entity(shooter, shooter.level());
        missile.setDamage(this.damage);
        missile.setExplosionDamage(this.explosionDamage);
        missile.setExplosionRadius(this.explosionRadius);
        return missile;
    }
}
