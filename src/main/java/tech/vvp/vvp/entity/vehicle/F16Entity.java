package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class F16Entity extends GeoVehicleEntity {

    private float prevGearProgress = 0f;

    public F16Entity(EntityType<F16Entity> type, Level world) {
        super(type, world);
    }

    public float getPrevGearProgress() {
        return prevGearProgress;
    }

    public void setPrevGearProgress(float progress) {
        this.prevGearProgress = progress;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.25f) * damage);
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }
}
