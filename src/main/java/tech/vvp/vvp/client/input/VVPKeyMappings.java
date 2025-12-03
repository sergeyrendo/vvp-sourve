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

    public static final KeyMapping TOGGLE_FIRING_MODE = new KeyMapping(
            "key.vvp.toggle_firing_mode",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Q,
            "key.categories.vvp"
    );

    public static final KeyMapping OPEN_COORDINATE_INPUT = new KeyMapping(
            "key.vvp.open_coordinate_input",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.vvp"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_FIRING_MODE);
        event.register(OPEN_COORDINATE_INPUT);
    }
}
