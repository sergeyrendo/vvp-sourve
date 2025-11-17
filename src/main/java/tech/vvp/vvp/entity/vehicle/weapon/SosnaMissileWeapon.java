package tech.vvp.vvp.entity.vehicle.weapon;

import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.projectile.SosnaMissileEntity;

public class SosnaMissileWeapon extends VehicleWeapon {

    private float damage;
    private float explosionDamage;
    private float explosionRadius;

    public SosnaMissileWeapon() {
        this.icon = new ResourceLocation("vvp", "textures/screens/vehicle_weapon/sosna_missile.png");
    }

    public SosnaMissileWeapon damage(float damage) {
        this.damage = damage;
        return this;
    }

    public SosnaMissileWeapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public SosnaMissileWeapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public SosnaMissileEntity create(LivingEntity shooter, int guideType, @Nullable Vec3 targetPos, boolean topAttack) {
        SosnaMissileEntity missile = new SosnaMissileEntity(
                shooter,
                shooter.level(),
                this.damage,
                this.explosionDamage,
                this.explosionRadius,
                guideType,
                targetPos
        );
        missile.setTopAttack(topAttack);
        return missile;
    }
}

