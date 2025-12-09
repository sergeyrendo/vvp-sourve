package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.vehicle.ToyotaEntity;

public class ToyotaModel extends VehicleModel<ToyotaEntity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Override
    public @Nullable TransformContext<ToyotaEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "rul" -> (bone, vehicle, state) -> {
                // Плавная анимация руля на основе поворота машины
                float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                // Ограничиваем угол поворота руля (максимум ±180 градусов для более заметного эффекта)
                steeringAngle = Mth.clamp(steeringAngle, -180f, 180f);
                bone.setRotZ((float) Math.toRadians(steeringAngle * 10)); // Умножаем на 10 для сильного поворота
            };
            default -> super.collectTransform(boneName);
        };
    }
}
