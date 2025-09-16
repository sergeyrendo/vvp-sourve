package tech.vvp.vvp.entity.vehicle.weapon;


import tech.vvp.vvp.entity.projectile.CannonAtgmShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.weapon.VehicleWeapon;
import net.minecraft.world.entity.LivingEntity;

public class CannonAtgmShellWeapon extends VehicleWeapon {
    public float hitDamage, explosionRadius, explosionDamage, fireProbability, velocity, gravity;
    public int fireTime, durability, spreadAmount, spreadAngle, spreadTime;


    public CannonAtgmShellEntity.Type type;

    public CannonAtgmShellWeapon hitDamage(float hitDamage) {
        this.hitDamage = hitDamage;
        return this;
    }

    public CannonAtgmShellWeapon explosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }

    public CannonAtgmShellWeapon explosionDamage(float explosionDamage) {
        this.explosionDamage = explosionDamage;
        return this;
    }

    public CannonAtgmShellWeapon fireProbability(float fireProbability) {
        this.fireProbability = fireProbability;
        return this;
    }

    public CannonAtgmShellWeapon velocity(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public CannonAtgmShellWeapon fireTime(int fireTime) {
        this.fireTime = fireTime;
        return this;
    }

    public CannonAtgmShellWeapon durability(int durability) {
        this.durability = durability;
        return this;
    }

    public CannonAtgmShellWeapon gravity(float gravity) {
        this.gravity = gravity;
        return this;
    }

    public CannonAtgmShellWeapon type(CannonAtgmShellEntity.Type type) {
        this.type = type;
        return this;
    }

    public CannonAtgmShellWeapon spreadAmount(int spreadAmount) {
        this.spreadAmount = spreadAmount;
        return this;
    }

    public CannonAtgmShellWeapon spreadAngle(int spreadAngle) {
        this.spreadAngle = spreadAngle;
        return this;
    }

    public CannonAtgmShellWeapon spreadTime(int spreadTime) {
        this.spreadTime = spreadTime;
        return this;
    }

    public CannonAtgmShellEntity create(LivingEntity living) {
        return new CannonAtgmShellEntity(living,
                living.level(),
                this.hitDamage,
                this.explosionRadius,
                this.explosionDamage,
                this.fireProbability,
                this.fireTime,
                this.gravity,
                this.type,
                this.spreadAmount,
                this.spreadTime,
                this.spreadAngle
        ).durability(this.durability);
    }
}
