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
            case "wheel1", "wheel2" -> (bone, vehicle, state) -> {
                // Передние колёса (wheel1 и wheel2 по координатам Z=-37.3) - вращение + поворот
                float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                bone.setRotX((float) Math.toRadians(-wheelRot)); // Инвертируем направление
                
                // Поворот передних колёс
                float steeringAngle = Mth.lerp(state.getPartialTick(), vehicle.getPrevSteeringAngle(), vehicle.getSteeringAngle());
                steeringAngle = Mth.clamp(steeringAngle, -30f, 30f); // Ограничиваем угол поворота колёс
                bone.setRotY((float) Math.toRadians(steeringAngle * 2)); // Поворот по оси Y
            };
            case "wheel3", "wheel4" -> (bone, vehicle, state) -> {
                // Задние колёса (wheel3 и wheel4 по координатам Z=24.8) - только вращение
                float wheelRot = Mth.lerp(state.getPartialTick(), vehicle.getPrevWheelRotation(), vehicle.getWheelRotation());
                bone.setRotX((float) Math.toRadians(-wheelRot)); // Инвертируем направление
            };
            default -> super.collectTransform(boneName);
        };
    }
}
