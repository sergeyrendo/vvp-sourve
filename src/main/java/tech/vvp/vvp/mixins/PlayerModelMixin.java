package tech.vvp.vvp.mixins;

import tech.vvp.vvp.entity.vehicle.TowEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch, CallbackInfo ci) {
        if (pEntity instanceof Player player) {
            var model = (PlayerModel) (Object) this;

            // Tow
            if (player.getVehicle() instanceof TowEntity) {
                model.head.xRot = 0;
                model.hat.xRot = 0;

                model.leftArm.yRot = 45 * Mth.DEG_TO_RAD;
                model.leftArm.xRot = -115 * Mth.DEG_TO_RAD;
                model.leftSleeve.yRot = 45 * Mth.DEG_TO_RAD;
                model.leftSleeve.xRot = -115 * Mth.DEG_TO_RAD;

                model.rightArm.yRot = 25 * Mth.DEG_TO_RAD;
                model.rightArm.xRot = -115 * Mth.DEG_TO_RAD;
                model.rightSleeve.yRot = 25 * Mth.DEG_TO_RAD;
                model.rightSleeve.xRot = -115 * Mth.DEG_TO_RAD;
            }
        }
    }
}
