package tech.vvp.vvp.mixin;

import com.atsuishio.superbwarfare.client.overlay.weapon.LandVehicleHud;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.vvp.vvp.client.hud.ReticleOverlay;

/**
 * Mixin для отключения стандартного HUD LandVehicleHud
 * когда активен наш кастомный прицел.
 * 
 * Мы рендерим свой HUD с элементами в нужных позициях.
 */
@Mixin(value = LandVehicleHud.class, remap = false)
public abstract class LandVehicleHudMixin {
    
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private static void vvp$onRender(VehicleEntity vehicle, Player player, ForgeGui gui, 
                                      GuiGraphics guiGraphics, float partialTick,
                                      int screenWidth, int screenHeight, CallbackInfo ci) {
        // Блокируем стандартный LandVehicleHud только если есть наш кастомный прицел
        if (ReticleOverlay.hasCustomReticle(vehicle.getClass())) {
            ci.cancel();
        }
    }
}
