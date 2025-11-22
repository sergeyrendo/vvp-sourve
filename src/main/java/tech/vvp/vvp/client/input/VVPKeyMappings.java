package tech.vvp.vvp.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import tech.vvp.vvp.VVP;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class VVPKeyMappings {
    public static final KeyMapping TOGGLE_RADAR = new KeyMapping(
            "key.vvp.toggle_radar",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.vvp"
    );

    // Переместить радар по позициям (6 точек)
    public static final KeyMapping MOVE_RADAR_POS = new KeyMapping(
            "key.vvp.move_radar_pos",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M, // по умолчанию M
            "key.categories.vvp"
    );

    public static final KeyMapping TOGGLE_FIRING_MODE = new KeyMapping(
            "key.vvp.toggle_firing_mode",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Q,
            "key.categories.vvp"
    );

    public static final KeyMapping TOGGLE_SUPPORTS = new KeyMapping(
            "key.vvp.toggle_supports",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.vvp"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_RADAR);
        event.register(MOVE_RADAR_POS);
        event.register(TOGGLE_FIRING_MODE);
        event.register(TOGGLE_SUPPORTS);
    }


}
