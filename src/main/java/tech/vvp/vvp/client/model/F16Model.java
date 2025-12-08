package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.vehicle.F16Entity;

public class F16Model extends VehicleModel<F16Entity> {

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Override
    public @Nullable TransformContext<F16Entity> collectTransform(String boneName) {
        return switch (boneName) {
            // Переднее шасси - складывается назад
            case "gear" -> (bone, vehicle, state) -> {
                // Плавная анимация: 0 = выпущено (на земле), 1 = убрано (в воздухе)
                float targetProgress = vehicle.onGround() ? 0f : 1f;
                float smoothProgress = Mth.lerp(0.1f, vehicle.getPrevGearProgress(), targetProgress);
                vehicle.setPrevGearProgress(smoothProgress);
                
                // Поворот на -90 градусов (складывается назад)
                bone.setRotX(smoothProgress * (float) Math.toRadians(-90));
            };
            case "wheel1" -> (bone, vehicle, state) -> {
                float smoothProgress = vehicle.getPrevGearProgress();
                // Колесо поворачивается вбок при уборке
                bone.setRotZ(smoothProgress * (float) Math.toRadians(90));
            };
            
            // Левое основное шасси - складывается внутрь
            case "gear2" -> (bone, vehicle, state) -> {
                float smoothProgress = vehicle.getPrevGearProgress();
                // Поворот на 90 градусов (складывается внутрь влево)
                bone.setRotZ(smoothProgress * (float) Math.toRadians(85));
            };
            case "wheel2" -> (bone, vehicle, state) -> {
                float smoothProgress = vehicle.getPrevGearProgress();
                // Колесо поворачивается при уборке
                bone.setRotY(smoothProgress * (float) Math.toRadians(-90));
            };
            
            // Правое основное шасси - складывается внутрь
            case "gear3" -> (bone, vehicle, state) -> {
                float smoothProgress = vehicle.getPrevGearProgress();
                // Поворот на -90 градусов (складывается внутрь вправо)
                bone.setRotZ(smoothProgress * (float) Math.toRadians(-85));
            };
            case "wheel3" -> (bone, vehicle, state) -> {
                float smoothProgress = vehicle.getPrevGearProgress();
                // Колесо поворачивается при уборке
                bone.setRotY(smoothProgress * (float) Math.toRadians(90));
            };
            
            default -> super.collectTransform(boneName);
        };
    }
}
