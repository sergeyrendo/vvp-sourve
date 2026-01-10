package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimationState;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.D30Entity;

public class D30Model extends VehicleModel<D30Entity> {

    // Накопленное вращение вентилей
    private float vertelYawRotation = 0f;
    private float vertelPitchRotation = 0f;
    
    // Предыдущие значения для расчёта дельты
    private float prevTurretYaw = 0f;
    private float prevTurretPitch = 0f;

    @Override
    public ResourceLocation getModelResource(D30Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/d30.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(D30Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "textures/entity/d30.png");
    }

    @Override
    public ResourceLocation getAnimationResource(D30Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "animations/d30.animation.json");
    }

    @Override
    public void setCustomAnimations(D30Entity vehicle, long instanceId, AnimationState<D30Entity> animationState) {
        super.setCustomAnimations(vehicle, instanceId, animationState);
        
        // Обновляем вращение вентилей после базовых анимаций
        float partialTick = animationState.getPartialTick();
        
        // Текущие углы башни
        float currentYaw = Mth.lerp(partialTick, vehicle.turretYRotO, vehicle.getTurretYRot());
        float currentPitch = Mth.lerp(partialTick, vehicle.turretXRotO, vehicle.getTurretXRot());
        
        // Дельта для yaw
        float deltaYaw = currentYaw - prevTurretYaw;
        while (deltaYaw > 180) deltaYaw -= 360;
        while (deltaYaw < -180) deltaYaw += 360;
        vertelYawRotation += deltaYaw * 0.5f;
        prevTurretYaw = currentYaw;
        
        // Дельта для pitch
        float deltaPitch = currentPitch - prevTurretPitch;
        vertelPitchRotation += deltaPitch * 0.5f;
        prevTurretPitch = currentPitch;
        
        // Применяем к костям
        var boneYaw = getAnimationProcessor().getBone("vertelkanekrutoi");
        if (boneYaw != null) {
            boneYaw.setRotZ(vertelYawRotation * Mth.DEG_TO_RAD);
        }
        
        var bonePitch = getAnimationProcessor().getBone("vertelkakrytai");
        if (bonePitch != null) {
            bonePitch.setRotX(vertelPitchRotation * Mth.DEG_TO_RAD);
        }
    }
}
