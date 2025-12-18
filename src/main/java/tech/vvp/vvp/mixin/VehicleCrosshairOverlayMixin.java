package tech.vvp.vvp.mixin;

import com.atsuishio.superbwarfare.client.overlay.VehicleCrosshairOverlay;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.vvp.vvp.client.hud.ReticleOverlay;

/**
 * Mixin для перехвата рендеринга прицела техники из Superb Warfare.
 * Позволяет заменить стандартный прицел на кастомный для определенной техники (Абрамс).
 */
@Mixin(value = VehicleCrosshairOverlay.class, remap = false)
public abstract class VehicleCrosshairOverlayMixin {
    
    /**
     * Перехватываем метод render из IGuiOverlay для отмены стандартного прицела
     * когда игрок находится в нашей технике с кастомным прицелом.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void vvp$onRender(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, 
                               int screenWidth, int screenHeight, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null) return;
        
        if (player.getVehicle() instanceof VehicleEntity vehicleEntity) {
            // Проверяем, является ли техника нашей с кастомным прицелом
            if (ReticleOverlay.hasCustomReticle(vehicleEntity.getClass())) {
                // Отменяем стандартный рендеринг прицела SBW
                ci.cancel();
            }
        }
    }
}
