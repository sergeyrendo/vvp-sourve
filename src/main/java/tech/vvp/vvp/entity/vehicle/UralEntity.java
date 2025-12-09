package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class UralEntity extends GeoVehicleEntity {

    private float steeringAngle = 0f;
    private float prevSteeringAngle = 0f;

    public UralEntity(EntityType<UralEntity> type, Level world) {
        super(type, world);
    }

    public float getSteeringAngle() {
        return steeringAngle;
    }

    public float getPrevSteeringAngle() {
        return prevSteeringAngle;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        
        // Обновляем угол поворота руля на основе поворота машины
        prevSteeringAngle = steeringAngle;
        float targetAngle = this.getYRot() - this.yRotO;
        steeringAngle = steeringAngle * 0.7f + targetAngle * 0.3f;
    }
}
