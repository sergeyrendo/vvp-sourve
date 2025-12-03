package tech.vvp.vvp.client.input;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.screen.CoordinateInputScreen;
import tech.vvp.vvp.entity.vehicle.M142HimarsEntity;
import tech.vvp.vvp.init.CoordinateTargetVehicle;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.C2SHimarsToggleModePacket;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class VVPClientInputHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (VVPKeyMappings.TOGGLE_FIRING_MODE.consumeClick()) {
            var vehicle = mc.player.getVehicle();
            if (vehicle instanceof M142HimarsEntity && vehicle.getFirstPassenger() == mc.player) {
                VVPNetwork.VVP_HANDLER.sendToServer(new C2SHimarsToggleModePacket());
            }
        }

        while (VVPKeyMappings.OPEN_COORDINATE_INPUT.consumeClick()) {
            var coordVehicle = mc.player.getVehicle();
            if (coordVehicle instanceof CoordinateTargetVehicle coordinateVehicle && coordVehicle.getFirstPassenger() == mc.player) {
                mc.setScreen(new CoordinateInputScreen(coordinateVehicle));
            }
        }
    }

    // Блокируем выброс предмета (Q) когда игрок в HIMARS
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null) return;

        // Проверяем что нажата Q и игрок в HIMARS
        if (event.getKey() == GLFW.GLFW_KEY_Q && event.getAction() == GLFW.GLFW_PRESS) {
            var vehicle = mc.player.getVehicle();
            if (vehicle instanceof M142HimarsEntity && vehicle.getFirstPassenger() == mc.player) {
                // Отменяем стандартное действие выброса предмета
                if (mc.options.keyDrop.matches(event.getKey(), event.getScanCode())) {
                    // Сбрасываем клавишу drop чтобы предмет не выбросился
                    while (mc.options.keyDrop.consumeClick()) {
                        // Просто потребляем клик
                    }
                }
            }
        }
    }
}
