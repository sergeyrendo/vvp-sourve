package tech.vvp.vvp.client;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.gui.RadialSeatScreen;
import tech.vvp.vvp.init.ModKeyMappings;

/**
 * Обработчик нажатия клавиши для открытия меню выбора места
 */
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class SeatSelectorHandler {
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        
        // Проверяем что игра активна и нет открытого экрана
        if (mc.player == null || mc.screen != null) return;
        
        // Проверяем нажатие клавиши
        if (ModKeyMappings.SEAT_SELECTOR.consumeClick()) {
            Player player = mc.player;
            Entity vehicle = player.getVehicle();
            
            // Открываем меню только если игрок в технике SuperbWarfare
            if (vehicle instanceof VehicleEntity) {
                RadialSeatScreen.open();
            }
        }
    }
}
