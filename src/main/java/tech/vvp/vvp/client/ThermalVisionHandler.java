package tech.vvp.vvp.client;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.init.ModKeyMappings;

/**
 * Обработчик thermal vision - включает/выключает thermal шейдер и управляет состоянием
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class ThermalVisionHandler {
    
    private static boolean thermalVisionEnabled = false;
    // Путь к пост-эффекту шейдера
    // Minecraft ищет шейдеры в assets/<modid>/shaders/post/<name>.json
    // Нужно указать полный путь включая shaders/post/ и расширение .json
    private static final ResourceLocation THERMAL_SHADER = new ResourceLocation(VVP.MOD_ID, "shaders/post/thermal.json");
    
    /**
     * Проверяет, включен ли thermal vision
     */
    public static boolean isThermalVisionEnabled() {
        return thermalVisionEnabled;
    }
    
    /**
     * Включает/выключает thermal vision
     */
    public static void toggleThermalVision() {
        thermalVisionEnabled = !thermalVisionEnabled;
        applyThermalShader();
    }
    
    /**
     * Применяет или убирает thermal шейдер
     */
    private static void applyThermalShader() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameRenderer == null) {
            return;
        }
        
        try {
            if (thermalVisionEnabled) {
                // Применяем thermal шейдер
                mc.gameRenderer.loadEffect(THERMAL_SHADER);
                System.out.println("[VVP] Thermal Vision: ВКЛЮЧЕНО, шейдер: " + THERMAL_SHADER);
            } else {
                // Убираем шейдер (загружаем пустой эффект)
                mc.gameRenderer.loadEffect(null);
                System.out.println("[VVP] Thermal Vision: ВЫКЛЮЧЕНО");
            }
        } catch (Exception e) {
            // Если шейдер не загрузился, просто отключаем
            thermalVisionEnabled = false;
            System.err.println("[VVP] Ошибка загрузки thermal шейдера: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean wasKeyPressed = false;
    
    /**
     * Применяем шейдер при загрузке игры или изменении настроек
     * Также проверяем нажатие клавиши
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameRenderer == null || mc.player == null || mc.level == null) {
            return;
        }
        
        // Проверяем, находится ли игрок в технике
        Entity vehicle = mc.player.getRootVehicle();
        boolean isInVehicle = vehicle instanceof VehicleEntity && vehicle != mc.player;
        
        // Проверяем, что камера в режиме первого лица
        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();
        
        // Если игрок вышел из техники или переключился на третье лицо, выключаем thermal vision
        if ((!isInVehicle || !isFirstPerson) && thermalVisionEnabled) {
            thermalVisionEnabled = false;
            applyThermalShader();
            if (!isInVehicle) {
                System.out.println("[VVP] Игрок вышел из техники, Thermal Vision выключен");
            } else {
                System.out.println("[VVP] Переключено на третье лицо, Thermal Vision выключен");
            }
        }
        
        // Проверяем нажатие клавиши thermal vision (только если в технике и от первого лица)
        boolean isKeyPressed = ModKeyMappings.THERMAL_VISION.isDown();
        if (isKeyPressed && !wasKeyPressed) {
            if (isInVehicle && isFirstPerson) {
                // Клавиша только что была нажата, игрок в технике и от первого лица
                System.out.println("[VVP] Нажата клавиша Thermal Vision, текущее состояние: " + thermalVisionEnabled);
                toggleThermalVision();
            } else {
                if (!isInVehicle) {
                    System.out.println("[VVP] Thermal Vision можно включить только в технике!");
                } else {
                    System.out.println("[VVP] Thermal Vision можно включить только от первого лица!");
                }
            }
        }
        wasKeyPressed = isKeyPressed;
        
        // Проверяем, что шейдер применен правильно (только если в технике и от первого лица)
        if (thermalVisionEnabled && isInVehicle && isFirstPerson) {
            // Шейдер уже применен в toggleThermalVision, не нужно применять каждый тик
        }
    }
}

