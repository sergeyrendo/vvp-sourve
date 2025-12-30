package tech.vvp.vvp.client.hud;

import com.atsuishio.superbwarfare.client.RenderHelper;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.ThermalVisionHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Универсальный класс для кастомных прицелов техники с эффектом шума при выстреле
 * и собственным HUD
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class ReticleOverlay {

    // Реестр конфигураций прицелов для разных типов техники
    // Map<Class, Map<seatIndex, ReticleConfig>> - позволяет иметь разные конфигурации для разных сидушек
    private static final Map<Class<? extends VehicleEntity>, Map<Integer, ReticleConfig>> RETICLE_CONFIGS = new HashMap<>();
    
    // Текстура шума (общая для всех)
    private static final ResourceLocation NOISE_TEXTURE = new ResourceLocation("vvp", "textures/reticles/noise1.png");
    
    // Текстуры для HUD
    private static final ResourceLocation COMPASS = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/base/compass.png");
    private static final ResourceLocation ROLL_IND = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/helicopter/roll_ind.png");
    private static final ResourceLocation BARREL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/line.png");
    private static final ResourceLocation BODY = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/body.png");
    private static final ResourceLocation LEFT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/left_wheel.png");
    private static final ResourceLocation RIGHT_WHEEL = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/right_wheel.png");
    private static final ResourceLocation ENGINE = new ResourceLocation("superbwarfare", "textures/overlay/vehicle/land/engine.png");
    // Текстура для digital scope (вместо шейдера)
    private static final ResourceLocation TV_FRAME = new ResourceLocation("vvp", "textures/overlay/tv_frame.png");
    
    // Текстуры для выбора боеприпасов
    private static final ResourceLocation CHOSEN = new ResourceLocation("superbwarfare", "textures/gui/attachment/chosen.png");
    private static final ResourceLocation NOT_CHOSEN = new ResourceLocation("superbwarfare", "textures/gui/attachment/not_chosen.png");
    
    // Состояние эффекта шума
    private static int noiseTimer = 0;
    private static int noiseDuration = 12;
    private static float noiseOffsetX = 0;
    private static float noiseOffsetY = 0;
    private static final Random random = new Random();
    
    // Отслеживание состояния
    private static boolean wasInVehicle = false;
    private static double lastRecoilShake = 0;
    // Map для отслеживания последнего значения shootAnimationTimer для каждого оружия
    // Ключ: "seatIndex_weaponIndex", значение: последний таймер
    private static final Map<String, Integer> lastShootAnimTimers = new HashMap<>();
    
    // Состояние digital sight (теперь работает через текстуру tv_frame.png вместо шейдера)
    private static boolean digitalSightActive = false;
    
    // HUD цвет (зеленый как в SBW)
    private static final int HUD_COLOR = 0x66FF00;
    private static final int HUD_COLOR_RED = 0xFF0000;

    static {
        // Регистрируем конфигурации прицелов для техники
        registerReticleConfigs();
    }

    /**
     * Регистрация конфигураций прицелов для разных типов техники
     */
    private static void registerReticleConfigs() {
        // M1A2 Abrams - сидушка 0 (наводчик)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.M1A2Entity.class,
            0,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_digital-4x3.png")
                .setReticle("vvp:textures/reticles/abrams_main.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(1.5f)
                .setNoiseEnabled(true)
                .setDigital(true)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(0)
                .setHudRightSide(false)
        );
        
        // M1A2 Abrams - сидушка 1 (командир RWS)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.M1A2Entity.class,
            1,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_stryker-rws.png")
                .setReticle("vvp:textures/reticles/pn-b.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(1.5f)
                .setNoiseEnabled(true)
                .setDigital(true)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(1)
                .setHudRightSide(true)
        );
        
        // M1A2 SEP - сидушка 0 (наводчик)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.M1A2SepEntity.class,
            0,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_digital-4x3.png")
                .setReticle("vvp:textures/reticles/abrams_main.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(1.5f)
                .setNoiseEnabled(true)
                .setDigital(true)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(0)
                .setHudRightSide(false)
        );
        
        // M1A2 SEP - сидушка 1 (командир RWS)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.M1A2SepEntity.class,
            1,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_stryker-rws.png")
                .setReticle("vvp:textures/reticles/pn-b.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(1.5f)
                .setDigital(true)
                .setNoiseEnabled(true)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(1)
                .setHudRightSide(true)
        );
        
        // BTR-4 - сидушка 0 (наводчик)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.Btr4Entity.class,
            0,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_sosna-u-thermal.png")
                .setReticle("vvp:textures/reticles/sosna-u_4x.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(3.0f) // 12x zoom
                .setNoiseEnabled(true)
                .setDigital(true)
                .setZoomScale(1.5f)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(0)
                .setHudRightSide(false)
                .setThermalReticle4x("vvp:textures/reticles/sosna-u-thermal_4x.png")
                .setThermalReticle12x("vvp:textures/reticles/sosna-u-thermal_12x.png")
                .setThermalOutline("vvp:textures/reticles/outline_sosna-u-thermal.png")
        );
        
        // BMP-2 - сидушка 0 (наводчик)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.Bmp2Entity.class,
            0,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_kord-rws.png")
                .setReticle("vvp:textures/reticles/bmp2_2a42_zoom.png") // По умолчанию для пушки
                .setWeaponReticle("Cannon", "vvp:textures/reticles/bmp2_2a42_zoom.png")
                .setWeaponReticle("Missile", "vvp:textures/reticles/bmp2_atgm_zoom.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(1.5f)
                .setThermalVisionAllowed(false)
                .setNoiseEnabled(false)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(0)
                .setHudRightSide(false)
                .setFullscreenReticle(false) // Сохраняем пропорции для круглого прицела
        );
        
        // BMP-2M - сидушка 0 (наводчик)
        registerConfig(
            tech.vvp.vvp.entity.vehicle.Bmp2MEntity.class,
            0,
            new ReticleConfig()
                .setOutline("vvp:textures/reticles/outline_kord-rws.png")
                .setReticle("vvp:textures/reticles/bmp2_2a42_zoom.png") // По умолчанию для пушки
                .setWeaponReticle("Cannon", "vvp:textures/reticles/bmp2_2a42_zoom.png")
                .setWeaponReticle("Missile", "vvp:textures/reticles/bmp2_atgm_zoom.png")
                .setReticleColor(1.0f, 1.0f, 1.0f, 1.0f)
                .setReticleScale(1.0f)
                .setZoomScale(1.5f)
                .setThermalVisionAllowed(true)
                .setNoiseEnabled(true) 
                .setDigital(true)
                .setNoiseDuration(15)
                .setNoiseAlpha(0.3f)
                .setSeatIndex(0)
                .setHudRightSide(false)
        );
    }

    /**
     * Регистрирует конфигурацию прицела для типа техники и конкретной сидушки
     */
    public static void registerConfig(Class<? extends VehicleEntity> vehicleClass, int seatIndex, ReticleConfig config) {
        RETICLE_CONFIGS.computeIfAbsent(vehicleClass, k -> new HashMap<>()).put(seatIndex, config);
    }

    /**
     * Рендерим прицел и рамку ПЕРЕД стандартными SBW оверлеями
     * чтобы SBW интерфейс с выбором оружия был ПОВЕРХ нашего прицела
     */
    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null || mc.options.hideGui) {
            return;
        }
        
        // Кастомный прицел только от 1 лица или при зуме!
        // От 3 лица используется стандартный SBW HUD
        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();
        boolean isZooming = ClientEventHandler.zoomVehicle;
        
        if (!isFirstPerson && !isZooming) {
            return; // От 3 лица без зума - пусть SBW рендерит свой HUD
        }

        Entity vehicle = player.getVehicle();
        
        if (!(vehicle instanceof VehicleEntity vehicleEntity)) {
            return;
        }
        
        int seatIndex = vehicleEntity.getSeatIndex(player);
        if (seatIndex < 0) {
            return;
        }
        
        // Получаем конфигурацию прицела для данного типа техники и сидушки
        ReticleConfig config = getConfigForVehicle(vehicleEntity, seatIndex);
        if (config == null) {
            return;
        }
        
        // Проверяем что игрок на нужном месте
        if (seatIndex != config.seatIndex) {
            return;
        }
        
        // Рендерим кастомный прицел (от 1 лица или при зуме)
        // SBW оверлеи (выбор оружия, энергия) будут рендериться ПОВЕРХ
        renderReticle(event.getGuiGraphics(), config, isZooming, vehicleEntity, player, event.getPartialTick());
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        // Обновляем эффект шума
        if (noiseTimer > 0) {
            noiseTimer--;
        }
        
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null) {
            resetState();
            return;
        }
        
        Entity vehicle = player.getVehicle();
        
        if (!(vehicle instanceof VehicleEntity vehicleEntity)) {
            if (wasInVehicle) {
                resetState();
            }
            wasInVehicle = false;
            return;
        }
        
        int seatIndex = vehicleEntity.getSeatIndex(player);
        if (seatIndex < 0) {
            wasInVehicle = false;
            return;
        }
        
        ReticleConfig config = getConfigForVehicle(vehicleEntity, seatIndex);
        if (config == null) {
            wasInVehicle = false;
            // Убираем digital sight шейдер, если вышли из техники
            if (digitalSightActive) {
                applyDigitalSightShader(false);
            }
            return;
        }
        
        wasInVehicle = true;
        
        if (seatIndex != config.seatIndex) {
            // Убираем digital sight шейдер, если не на нужном месте
            if (digitalSightActive) {
                applyDigitalSightShader(false);
            }
            return;
        }
        
        // Применяем или убираем digital sight шейдер
        // Только если thermal vision не активен (thermal vision имеет приоритет)
        boolean shouldBeDigital = config.isDigital && !ThermalVisionHandler.isThermalVisionEnabled();
        if (shouldBeDigital != digitalSightActive) {
            applyDigitalSightShader(shouldBeDigital);
        } else if (ThermalVisionHandler.isThermalVisionEnabled() && digitalSightActive) {
            // Если thermal vision включен, а digital sight активен - отключаем digital sight
            applyDigitalSightShader(false);
        }
        
        // Отслеживаем выстрелы через recoilShake (как в SBW)
        // Noise эффекты только для digital прицелов
        if (config.noiseEnabled && config.isDigital) {
            detectShot(vehicleEntity, player, config);
        }
    }
    
    private static void detectShot(VehicleEntity vehicle, Player player, ReticleConfig config) {
        try {
            // Получаем реальный seatIndex игрока
            int seatIndex = vehicle.getSeatIndex(player);
            if (seatIndex < 0) {
                return;
            }
            
            // Получаем текущее выбранное оружие
            var gunData = vehicle.getGunData(player);
            if (gunData == null) {
                return;
            }
            
            // Используем shootAnimationTimer для детектирования выстрела
            int currentShootAnimTimer = gunData.shootAnimationTimer.get();
            String key = seatIndex + "_current";
            Integer lastTimer = lastShootAnimTimers.get(key);
            
            // Получаем максимальное значение таймера (shootAnimationTime)
            int maxTimer = gunData.compute().shootAnimationTime;
            
            // Если таймер увеличился (новый выстрел) или достиг максимума (только что выстрелили)
            // При автоматической стрельбе таймер сбрасывается на максимум при каждом выстреле
            if (currentShootAnimTimer > 0) {
                if (lastTimer == null) {
                    // Первый раз - инициализируем
                    lastShootAnimTimers.put(key, currentShootAnimTimer);
                } else if (currentShootAnimTimer > lastTimer || 
                          (currentShootAnimTimer == maxTimer && lastTimer < maxTimer)) {
                    // Таймер увеличился или достиг максимума - новый выстрел!
                    triggerNoiseEffect(config.noiseDuration);
                    lastShootAnimTimers.put(key, currentShootAnimTimer);
                } else {
                    // Таймер уменьшается (анимация идет) - просто обновляем значение
                    lastShootAnimTimers.put(key, currentShootAnimTimer);
                }
            } else {
                // Таймер вернулся в 0 - сбрасываем
                lastShootAnimTimers.put(key, 0);
            }
            
            // Также проверяем recoilShake как запасной вариант для быстрой стрельбы
            double currentRecoil = vehicle.getRecoilShake();
            if (currentRecoil > lastRecoilShake + 0.3 && currentRecoil > 0.5) {
                // Если откат увеличился значительно - возможно был выстрел
                // Но только если таймер не активен (для избежания дублирования)
                if (currentShootAnimTimer == 0) {
                    triggerNoiseEffect(config.noiseDuration);
                }
            }
            lastRecoilShake = currentRecoil;
        } catch (Exception ignored) {
        }
    }

    private static void renderReticle(GuiGraphics guiGraphics, ReticleConfig config, boolean isZooming, 
                                       VehicleEntity vehicle, Player player, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        PoseStack poseStack = guiGraphics.pose();
        
        // === СЛОЙ 1: Эффект шума при выстреле - ПОД прицелом ===
        // Noise эффекты только для digital прицелов
        if (config.noiseEnabled && config.isDigital && noiseTimer > 0) {
            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            renderNoiseEffect(guiGraphics, screenWidth, screenHeight, poseStack, config);
            poseStack.popPose();
        }
        
        // === СЛОЙ 2: Сетка прицела (reticle) - ПОВЕРХ шума, ПОД outline ===
        // Используем thermal прицел, если thermal vision включен
        if (config.reticleTexture != null || (ThermalVisionHandler.isThermalVisionEnabled() && config.hasThermalReticle()) || !config.weaponReticles.isEmpty() || !config.thermalWeaponReticles.isEmpty()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            renderMainReticle(guiGraphics, config, screenWidth, screenHeight, isZooming, vehicle, player);
        }
        
        // === СЛОЙ 2.5: Digital scope frame (tv_frame.png) - ПОД outline, ПОВЕРХ прицела ===
        if (config.isDigital && digitalSightActive && !ThermalVisionHandler.isThermalVisionEnabled()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            renderDigitalScopeFrame(guiGraphics, screenWidth, screenHeight);
        }
        
        // === СЛОЙ 3: Рамка прицела (outline) - ПОВЕРХ всего ===
        // Используем thermal outline, если thermal vision включен
        ResourceLocation outlineToUse = (ThermalVisionHandler.isThermalVisionEnabled() && config.thermalOutlineTexture != null) 
            ? config.thermalOutlineTexture 
            : config.outlineTexture;
        
        if (outlineToUse != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            renderOutline(guiGraphics, outlineToUse, screenWidth, screenHeight, config.outlineScale);
        }
        
        // === СЛОЙ 4: Наш кастомный HUD (скорость, HP, smoke) ===
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.pushPose();
        renderCustomHud(guiGraphics, poseStack, vehicle, player, screenWidth, screenHeight, partialTick, config);
        poseStack.popPose();
        
        // Сброс состояния рендеринга
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
    
    /**
     * Рендерит кастомный HUD
     * HP и SmokeReady - ВЛЕВО ВНИЗ (или ВПРАВО если hudRightSide = true)
     * Скорость - ВПРАВО ВНИЗ (или ВЛЕВО если hudRightSide = true)
     */
    private static void renderCustomHud(GuiGraphics guiGraphics, PoseStack poseStack, 
                                         VehicleEntity vehicle, Player player, 
                                         int screenWidth, int screenHeight, float partialTick, ReticleConfig config) {
        Minecraft mc = Minecraft.getInstance();
        
        boolean hudRightSide = config != null && config.hudRightSide;
        
        // Позиции для HUD (подняты выше)
        // Если hudRightSide = true, то все элементы справа
        int leftX = hudRightSide ? screenWidth - 200 : 10;                          // Левый край (или правый)
        int rightX = hudRightSide ? screenWidth - 10 : screenWidth - 80;           // Правый край (или левый)
        int bottomLeftY = screenHeight - 135;    // Левый - подняли выше
        int bottomRightY = screenHeight - 70;    // Правый - выше
        int centerX = screenWidth / 2;
        
        // === Компас (вверху по центру) ===
        renderCompass(guiGraphics, poseStack, player, screenWidth);
        
        // === HP, SmokeReady, NO POWER ===
        // Если hudRightSide = true, то справа внизу, иначе слева внизу
        int hpX = hudRightSide ? rightX - 100 : leftX;
        int hpY = bottomLeftY;
        
        // Здоровье
        int healthPercent = (int) ((vehicle.getHealth() / vehicle.getMaxHealth()) * 100);
        int healthColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, 100 - healthPercent, 2);
        String hpText = "HP: " + healthPercent;
        int hpTextWidth = mc.font.width(hpText);
        guiGraphics.drawString(mc.font, Component.literal(hpText), hudRightSide ? hpX - hpTextWidth : hpX, hpY, healthColor, false);
        
        // Дымовая завеса
        try {
            if (vehicle.hasDecoy() && player == vehicle.getFirstPassenger()) {
                String smokeKey = ModKeyMappings.RELEASE_DECOY.getKey().getDisplayName().getString();
                boolean decoyReady = getEntityDataBoolean(vehicle, "DECOY_READY");
                String smokeText = decoyReady ? ("SMOKE READY [" + smokeKey + "]") : "SMOKE RELOADING";
                int smokeColor = decoyReady ? HUD_COLOR : HUD_COLOR_RED;
                int smokeTextWidth = mc.font.width(smokeText);
                guiGraphics.drawString(mc.font, Component.literal(smokeText), hudRightSide ? hpX - smokeTextWidth : hpX, hpY + 12, smokeColor, false);
            }
        } catch (Exception ignored) {}
        
        // Энергия/мощность
        try {
            boolean canConsume = (boolean) vehicle.getClass().getMethod("canConsume", int.class).invoke(vehicle, 1);
            if (!canConsume) {
                String powerText = "NO POWER!";
                int powerTextWidth = mc.font.width(powerText);
                guiGraphics.drawString(mc.font, Component.literal(powerText), hudRightSide ? hpX - powerTextWidth : hpX, hpY + 24, HUD_COLOR_RED, false);
            }
        } catch (Exception ignored) {}
        
        // === ТПВ (Thermal Vision) ===
        if (ThermalVisionHandler.isThermalVisionEnabled()) {
            String tpvText = "ТПВ";
            int tpvTextWidth = mc.font.width(tpvText);
            // Размещаем рядом с HP, немного выше
            guiGraphics.drawString(mc.font, Component.literal(tpvText), hudRightSide ? hpX - tpvTextWidth : hpX, hpY - 12, HUD_COLOR, false);
        }
        
        // === Скорость ===
        // Если hudRightSide = true, то слева внизу, иначе справа внизу
        double speed = vehicle.getDeltaMovement().dot(vehicle.getViewVector(partialTick)) * 72;
        String speedText = String.format("%.0f km/h", Math.abs(speed));
        int speedWidth = mc.font.width(speedText);
        int speedX = hudRightSide ? leftX : (rightX - speedWidth);
        guiGraphics.drawString(mc.font, Component.literal(speedText), speedX, bottomRightY, HUD_COLOR, false);
        
        // === ЦЕНТР ВНИЗУ: Дистанция ===
        double distance = calculateDistance(player);
        String distanceText;
        if (distance > 500) {
            distanceText = "---m";
        } else {
            distanceText = String.format("%.0f m", distance);
        }
        int distWidth = mc.font.width(distanceText);
        guiGraphics.drawString(mc.font, Component.literal(distanceText), centerX - distWidth / 2, screenHeight - 50, HUD_COLOR, false);
        
        // === Иконка техники с состоянием частей ===
        renderVehicleStatus(guiGraphics, poseStack, vehicle, screenWidth, screenHeight, partialTick, config);
        
        // === Информация об оружии (по центру) ===
        renderWeaponInfo(guiGraphics, vehicle, player, screenWidth, screenHeight, mc);
        
        // === Выбор боеприпасов в верхнем правом углу outline ===
        renderAmmoSelection(guiGraphics, vehicle, player, screenWidth, screenHeight, mc);
    }
    
    /**
     * Рендерит компас
     */
    private static void renderCompass(GuiGraphics guiGraphics, PoseStack poseStack, Player player, int screenWidth) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        float yaw = player.getYRot();
        int u = (int) (128 + (64f / 45f * yaw));
        
        // Компас в верхней части экрана
        int compassX = screenWidth / 2 - 128;
        int compassY = 10;
        
        RenderSystem.setShaderTexture(0, COMPASS);
        guiGraphics.blit(COMPASS, compassX, compassY, u, 0, 256, 16, 512, 16);
        
        // Индикатор направления
        RenderSystem.setShaderTexture(0, ROLL_IND);
        guiGraphics.blit(ROLL_IND, screenWidth / 2 - 8, 30, 0, 0, 16, 16, 16, 16);
    }
    
    /**
     * Рендерит статус техники (иконка с колёсами, корпусом, двигателем) с поворотом башни
     */
    private static void renderVehicleStatus(GuiGraphics guiGraphics, PoseStack poseStack, 
                                            VehicleEntity vehicle, int screenWidth, int screenHeight, float partialTick, ReticleConfig config) {
        boolean hudRightSide = config != null && config.hudRightSide;
        
        // Иконка состояния техники - СЛЕВА или СПРАВА, выше HP (как Smoke)
        int statusX = hudRightSide ? (screenWidth - 42) : 10;
        int statusY = screenHeight - 190;  // Подняли еще выше
        
        // Центр иконки для вращения
        float centerX = statusX + 16f;
        float centerY = statusY + 16f;
        
        // Получаем угол поворота башни
        float turretYaw = getTurretYaw(vehicle, partialTick);
        
        // === СТВОЛ (статичный, смотрит вверх) ===
        // Рассчитываем повреждение башни для цвета ствола
        int turretDamage = 0;
        try {
            float turretH = getEntityDataFloat(vehicle, "TURRET_HEALTH");
            float turretMaxH = vehicle.getTurretMaxHealth();
            turretDamage = (int) ((1 - turretH / turretMaxH) * 100);
        } catch (Exception ignored) {}
        int barrelColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, turretDamage, 2);
        setShaderColorFromInt(barrelColor);
        // Ствол - вертикальная линия сверху от центра
        RenderHelper.preciseBlit(guiGraphics, BARREL, centerX - 0.5f, statusY - 3f, 0f, 0f, 1f, 16f, 1f, 16f);
        
        // === КОРПУС (вращается по углу башни) ===
        poseStack.pushPose();
        
        // Поворачиваем вокруг центра иконки на угол башни
        poseStack.translate(centerX, centerY, 0);
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(turretYaw));
        poseStack.translate(-centerX, -centerY, 0);
        
        // Корпус
        float bodyHealthPercent = vehicle.getHealth() / vehicle.getMaxHealth();
        int bodyDamage = (int) ((1 - bodyHealthPercent) * 100);
        int bodyColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, bodyDamage, 2);
        setShaderColorFromInt(bodyColor);
        guiGraphics.blit(BODY, statusX, statusY, 0, 0, 32, 32, 32, 32);
        
        // Левое колесо
        try {
            float leftWheelH = getEntityDataFloat(vehicle, "L_WHEEL_HEALTH");
            float wheelMaxH = vehicle.getWheelMaxHealth();
            int leftWheelDamage = (int) ((1 - leftWheelH / wheelMaxH) * 100);
            int leftColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, leftWheelDamage, 2);
            setShaderColorFromInt(leftColor);
            guiGraphics.blit(LEFT_WHEEL, statusX, statusY, 0, 0, 32, 32, 32, 32);
        } catch (Exception e) {
            setShaderColorFromInt(HUD_COLOR);
            guiGraphics.blit(LEFT_WHEEL, statusX, statusY, 0, 0, 32, 32, 32, 32);
        }
        
        // Правое колесо
        try {
            float rightWheelH = getEntityDataFloat(vehicle, "R_WHEEL_HEALTH");
            float wheelMaxH = vehicle.getWheelMaxHealth();
            int rightWheelDamage = (int) ((1 - rightWheelH / wheelMaxH) * 100);
            int rightColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, rightWheelDamage, 2);
            setShaderColorFromInt(rightColor);
            guiGraphics.blit(RIGHT_WHEEL, statusX, statusY, 0, 0, 32, 32, 32, 32);
        } catch (Exception e) {
            setShaderColorFromInt(HUD_COLOR);
            guiGraphics.blit(RIGHT_WHEEL, statusX, statusY, 0, 0, 32, 32, 32, 32);
        }
        
        // Двигатель
        try {
            float engineH = getEntityDataFloat(vehicle, "MAIN_ENGINE_HEALTH");
            float engineMaxH = vehicle.getEngineMaxHealth();
            int engineDamage = (int) ((1 - engineH / engineMaxH) * 100);
            int engineColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, engineDamage, 2);
            setShaderColorFromInt(engineColor);
            guiGraphics.blit(ENGINE, statusX, statusY, 0, 0, 32, 32, 32, 32);
        } catch (Exception e) {
            setShaderColorFromInt(HUD_COLOR);
            guiGraphics.blit(ENGINE, statusX, statusY, 0, 0, 32, 32, 32, 32);
        }
        
        poseStack.popPose();
        
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**
     * Получает интерполированный угол поворота башни
     */
    private static float getTurretYaw(VehicleEntity vehicle, float partialTick) {
        try {
            // Получаем turretYRot и turretYRotO через рефлексию (Kotlin поля)
            java.lang.reflect.Field turretYRotField = VehicleEntity.class.getDeclaredField("turretYRot");
            java.lang.reflect.Field turretYRotOField = VehicleEntity.class.getDeclaredField("turretYRotO");
            turretYRotField.setAccessible(true);
            turretYRotOField.setAccessible(true);
            
            float turretYRot = turretYRotField.getFloat(vehicle);
            float turretYRotO = turretYRotOField.getFloat(vehicle);
            
            // Интерполяция между старым и новым углом
            return Mth.lerp(partialTick, turretYRotO, turretYRot);
        } catch (Exception e) {
            return 0f;
        }
    }
    
    /**
     * Получает float значение из entityData по имени статического поля-акцессора
     */
    @SuppressWarnings("unchecked")
    private static float getEntityDataFloat(VehicleEntity vehicle, String accessorFieldName) {
        try {
            // Получаем статическое поле EntityDataAccessor из класса VehicleEntity
            java.lang.reflect.Field field = VehicleEntity.class.getDeclaredField(accessorFieldName);
            field.setAccessible(true);
            var accessor = (net.minecraft.network.syncher.EntityDataAccessor<Float>) field.get(null);
            return vehicle.getEntityData().get(accessor);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Получает boolean значение из entityData по имени статического поля-акцессора
     */
    @SuppressWarnings("unchecked")
    private static boolean getEntityDataBoolean(VehicleEntity vehicle, String accessorFieldName) {
        try {
            java.lang.reflect.Field field = VehicleEntity.class.getDeclaredField(accessorFieldName);
            field.setAccessible(true);
            var accessor = (net.minecraft.network.syncher.EntityDataAccessor<Boolean>) field.get(null);
            return vehicle.getEntityData().get(accessor);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Получает float значение из поля объекта через reflection
     */
    private static float getFieldFloat(Object obj, String fieldName) {
        try {
            java.lang.reflect.Field field = obj.getClass().getField(fieldName);
            return field.getFloat(obj);
        } catch (NoSuchFieldException e) {
            // Попробуем в суперклассе
            try {
                java.lang.reflect.Field field = obj.getClass().getSuperclass().getField(fieldName);
                return field.getFloat(obj);
            } catch (Exception ex) {
                return 0f;
            }
        } catch (Exception e) {
            return 0f;
        }
    }
    
    /**
     * Рендерит информацию об оружии (как в SBW - через firstPersonAmmoComponent)
     */
    private static void renderWeaponInfo(GuiGraphics guiGraphics, VehicleEntity vehicle, Player player, 
                                          int screenWidth, int screenHeight, Minecraft mc) {
        try {
            // Получаем данные оружия
            GunData gunData = vehicle.getGunData(player);
            if (gunData != null) {
                // Используем метод SBW для получения компонента с именем и патронами
                Component weaponComponent = vehicle.firstPersonAmmoComponent(gunData, player);
                
                int heat = vehicle.getWeaponHeat(player);
                int weaponColor = getGradientColor(HUD_COLOR, HUD_COLOR_RED, heat, 2);
                
                // Отображаем по центру снизу
                int textWidth = mc.font.width(weaponComponent);
                guiGraphics.drawString(mc.font, weaponComponent, 
                    (screenWidth - textWidth) / 2, screenHeight - 65, weaponColor, false);
            }
        } catch (Exception ignored) {}
    }
    
    /**
     * Рендерит выбор боеприпасов в верхнем правом углу outline
     */
    private static void renderAmmoSelection(GuiGraphics guiGraphics, VehicleEntity vehicle, Player player, 
                                           int screenWidth, int screenHeight, Minecraft mc) {
        try {
            GunData gunData = vehicle.getGunData(player);
            if (gunData == null) {
                return;
            }
            
            var data = gunData.compute();
            var ammoConsumers = data.getAmmoConsumers();
            int size = ammoConsumers.size();
            
            // Показываем только если есть больше одного типа боеприпасов
            if (size <= 1) {
                return;
            }
            
            int selectedIndex = gunData.selectedAmmoType.get();
            
            // Позиция в верхнем правом углу outline (с небольшим отступом)
            int startX = screenWidth - 60;  // Отступ от правого края
            int startY = 30;  // Отступ от верхнего края
            
            // Рендерим индикаторы выбора боеприпасов (как в SBW)
            for (int i = 0; i < size; i++) {
                ResourceLocation indicator = (i == selectedIndex) ? CHOSEN : NOT_CHOSEN;
                int x = startX + (i * 6);  // 6 пикселей между индикаторами
                int y = startY;
                
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.blit(indicator, x, y, 0, 0, 4, 4, 4, 4);
            }
            
            // Показываем подсказку с клавишей переключения
            String switchKey = ModKeyMappings.FIRE_MODE.getKey().getDisplayName().getString();
            String switchText = "[" + switchKey + "]";
            int textWidth = mc.font.width(switchText);
            guiGraphics.drawString(mc.font, Component.literal(switchText), 
                startX - textWidth / 2, startY + 8, 0xFFFFFF, false);
        } catch (Exception ignored) {}
    }
    
    /**
     * Вычисляет дистанцию до объекта
     */
    private static double calculateDistance(Player player) {
        try {
            var result = player.level().clip(
                new ClipContext(
                    player.getEyePosition(),
                    player.getEyePosition().add(player.getViewVector(1f).scale(512.0)),
                    ClipContext.Block.OUTLINE,
                    ClipContext.Fluid.NONE,
                    player
                )
            );
            return player.getEyePosition(1f).distanceTo(result.getLocation());
        } catch (Exception e) {
            return 999;
        }
    }
    
    /**
     * Интерполяция цвета (как в SBW)
     */
    private static int getGradientColor(int startColor, int endColor, int percent, int power) {
        percent = Math.max(0, Math.min(100, percent));
        double ratio = Math.pow(percent / 100.0, power);
        
        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;
        
        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;
        
        int r = (int) (startR + (endR - startR) * ratio);
        int g = (int) (startG + (endG - startG) * ratio);
        int b = (int) (startB + (endB - startB) * ratio);
        
        return (r << 16) | (g << 8) | b;
    }
    
    private static void setShaderColorFromInt(int color) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        RenderSystem.setShaderColor(r, g, b, 1.0f);
    }
    
    private static void renderOutline(GuiGraphics guiGraphics, ResourceLocation outlineTexture, int screenWidth, int screenHeight, float outlineScale) {
        // Растягиваем текстуру ровно на весь экран (как в BTR-4)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        
        // Всегда растягиваем на весь экран без выхода за границы
        // Используем preciseBlit для точного рендера
        RenderHelper.preciseBlit(
            guiGraphics,
            outlineTexture,
            0f,                    // x - начинаем с 0
            0f,                    // y - начинаем с 0
            0f,                    // uOffset
            0f,                    // vOffset
            screenWidth,           // width - ровно на весь экран
            screenHeight,          // height - ровно на весь экран
            screenWidth,           // textureWidth
            screenHeight           // textureHeight
        );
    }
    
    /**
     * Рендерит tv_frame.png для digital scope
     * Использует стандартный blit для лучшей совместимости
     */
    private static void renderDigitalScopeFrame(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        // Размер текстуры tv_frame.png
        int textureWidth = 1920;
        int textureHeight = 1080;
        
        // Рендерим на весь экран используя стандартный blit
        // blit(texture, x, y, width, height, u, v, uWidth, vHeight, textureWidth, textureHeight)
        guiGraphics.blit(
            TV_FRAME,
            0, 0,                          // x, y на экране
            screenWidth, screenHeight,     // размер на экране
            0, 0,                          // u, v в текстуре
            textureWidth, textureHeight,   // размер области в текстуре
            textureWidth, textureHeight    // полный размер текстуры
        );
    }
    
    private static void renderMainReticle(GuiGraphics guiGraphics, ReticleConfig config, int screenWidth, int screenHeight, boolean isZooming, VehicleEntity vehicle, Player player) {
        RenderSystem.setShaderColor(config.reticleColorR, config.reticleColorG, config.reticleColorB, config.reticleColorA);
        
        // Выбираем прицел: сначала проверяем thermal прицелы для оружий, потом обычные прицелы для оружий, потом thermal общие, потом обычный
        ResourceLocation reticleToUse = null;
        boolean isThermalVision = ThermalVisionHandler.isThermalVisionEnabled();
        
        // Получаем имя текущего оружия
        String weaponName = null;
        if (vehicle != null && player != null) {
            try {
                weaponName = vehicle.getGunName(vehicle.getSeatIndex(player));
            } catch (Exception ignored) {}
        }
        
        // Приоритет 1: Проверяем thermal прицел для текущего оружия (если ТПВ включен)
        if (isThermalVision && weaponName != null && !config.thermalWeaponReticles.isEmpty() && config.thermalWeaponReticles.containsKey(weaponName)) {
            reticleToUse = config.thermalWeaponReticles.get(weaponName);
        }
        
        // Приоритет 2: Проверяем обычный прицел для текущего оружия (если ТПВ выключен или нет thermal прицела для этого оружия)
        if (reticleToUse == null && weaponName != null && !config.weaponReticles.isEmpty() && config.weaponReticles.containsKey(weaponName)) {
            reticleToUse = config.weaponReticles.get(weaponName);
        }
        
        // Приоритет 3: Проверяем общие thermal прицелы (4x или 12x в зависимости от зума)
        if (reticleToUse == null && isThermalVision && config.hasThermalReticle()) {
            if (isZooming && config.thermalReticleTexture12x != null) {
                reticleToUse = config.thermalReticleTexture12x;
            } else if (config.thermalReticleTexture4x != null) {
                reticleToUse = config.thermalReticleTexture4x;
            }
        }
        
        // Приоритет 4: Используем обычный прицел по умолчанию
        if (reticleToUse == null) {
            reticleToUse = config.reticleTexture;
        }
        
        if (reticleToUse != null) {
            // Растягиваем прицел на весь экран (как в BTR-4)
            // Используем preciseBlit для правильного растягивания
            RenderHelper.preciseBlit(
                guiGraphics,
                reticleToUse,
                0f,                    // x - начинаем с 0
                0f,                    // y - начинаем с 0
                0f,                    // uOffset
                0f,                    // vOffset
                screenWidth,           // width - на весь экран
                screenHeight,          // height - на весь экран
                screenWidth,           // textureWidth
                screenHeight           // textureHeight
            );
        }
    }
    
    private static void renderNoiseEffect(GuiGraphics guiGraphics, int screenWidth, int screenHeight, PoseStack poseStack, ReticleConfig config) {
        // Вычисляем альфу с затуханием
        float alpha = ((float) noiseTimer / noiseDuration) * config.noiseAlpha;
        
        // Обновляем смещение для эффекта "белого шума"
        if (noiseTimer % 2 == 0) {
            noiseOffsetX = random.nextFloat() * 256;
            noiseOffsetY = random.nextFloat() * 256;
        }
        
        poseStack.pushPose();
        
        // Дрожание экрана
        float shakeX = (random.nextFloat() - 0.5f) * 4;
        float shakeY = (random.nextFloat() - 0.5f) * 4;
        poseStack.translate(shakeX, shakeY, 0);
        
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.setShaderTexture(0, NOISE_TEXTURE);
        
        // Тайлим текстуру шума
        int tileSize = 256;
        for (int tx = 0; tx < screenWidth + tileSize; tx += tileSize) {
            for (int ty = 0; ty < screenHeight + tileSize; ty += tileSize) {
                int offsetX = (int) ((tx + noiseOffsetX) % tileSize);
                int offsetY = (int) ((ty + noiseOffsetY) % tileSize);
                guiGraphics.blit(NOISE_TEXTURE, tx - offsetX, ty - offsetY, 0, 0, tileSize, tileSize, tileSize, tileSize);
            }
        }
        
        poseStack.popPose();
    }
    
    private static ReticleConfig getConfigForVehicle(VehicleEntity vehicle, int seatIndex) {
        // Ищем точное совпадение класса
        Map<Integer, ReticleConfig> seatConfigs = RETICLE_CONFIGS.get(vehicle.getClass());
        if (seatConfigs != null) {
            ReticleConfig config = seatConfigs.get(seatIndex);
            if (config != null) {
                return config;
            }
        }
        
        // Ищем по родительским классам
        for (Map.Entry<Class<? extends VehicleEntity>, Map<Integer, ReticleConfig>> entry : RETICLE_CONFIGS.entrySet()) {
            if (entry.getKey().isInstance(vehicle)) {
                ReticleConfig config = entry.getValue().get(seatIndex);
                if (config != null) {
                    return config;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Запускает эффект шума
     */
    public static void triggerNoiseEffect(int duration) {
        noiseTimer = duration;
        noiseDuration = duration;
        noiseOffsetX = random.nextFloat() * 256;
        noiseOffsetY = random.nextFloat() * 256;
    }
    
    /**
     * Включает или выключает digital sight (теперь работает через текстуру tv_frame.png вместо шейдера)
     */
    private static void applyDigitalSightShader(boolean enable) {
        // Теперь digital sight работает через текстуру tv_frame.png вместо шейдера
        digitalSightActive = enable;
    }
    
    // #region agent log
    private static void debugLog(String location, String message, Map<String, Object> data, String hypothesisId) {
        try {
            Path logPath = Paths.get("c:\\Users\\kniki\\OneDrive\\Desktop\\pack\\vvp-sourve\\.cursor\\debug.log");
            StringBuilder dataJson = new StringBuilder("{");
            if (data != null && !data.isEmpty()) {
                boolean first = true;
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (!first) dataJson.append(",");
                    first = false;
                    Object value = entry.getValue();
                    String valueStr = value instanceof String ? "\"" + value.toString().replace("\"", "\\\"") + "\"" : 
                                     value instanceof Boolean || value instanceof Number ? value.toString() : 
                                     "\"" + String.valueOf(value).replace("\"", "\\\"") + "\"";
                    dataJson.append("\"").append(entry.getKey()).append("\":").append(valueStr);
                }
            }
            dataJson.append("}");
            String logEntry = String.format(
                "{\"id\":\"%s\",\"timestamp\":%d,\"location\":\"%s\",\"message\":\"%s\",\"data\":%s,\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"%s\"}\n",
                UUID.randomUUID().toString(),
                Instant.now().toEpochMilli(),
                location.replace("\\", "\\\\").replace("\"", "\\\""),
                message.replace("\\", "\\\\").replace("\"", "\\\""),
                dataJson.toString(),
                hypothesisId
            );
            Files.write(logPath, logEntry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
    }
    // #endregion
    
    /**
     * Сбросить состояние
     */
    public static void resetState() {
        noiseTimer = 0;
        lastRecoilShake = 0;
        lastShootAnimTimers.clear();
        // Убираем digital sight шейдер при сбросе состояния
        if (digitalSightActive) {
            applyDigitalSightShader(false);
        }
    }
    
    /**
     * Проверяет, есть ли кастомный прицел для данного типа техники.
     * Используется в mixin для отмены стандартного прицела SBW.
     * Возвращает true только от 1 лица или при зуме!
     * @param vehicleClass класс техники
     * @return true если есть кастомный прицел и он должен отображаться
     */
    public static boolean hasCustomReticle(Class<? extends VehicleEntity> vehicleClass) {
        Minecraft mc = Minecraft.getInstance();
        
        // От 3 лица без зума - возвращаем false, чтобы SBW показал свой HUD
        boolean isFirstPerson = mc.options.getCameraType().isFirstPerson();
        boolean isZooming = ClientEventHandler.zoomVehicle;
        
        if (!isFirstPerson && !isZooming) {
            return false;
        }
        
        // Проверяем точное совпадение класса (если есть хотя бы одна конфигурация)
        if (RETICLE_CONFIGS.containsKey(vehicleClass)) {
            Map<Integer, ReticleConfig> seatConfigs = RETICLE_CONFIGS.get(vehicleClass);
            if (seatConfigs != null && !seatConfigs.isEmpty()) {
                return true;
            }
        }
        
        // Проверяем по родительским классам
        for (Map.Entry<Class<? extends VehicleEntity>, Map<Integer, ReticleConfig>> entry : RETICLE_CONFIGS.entrySet()) {
            if (entry.getKey().isAssignableFrom(vehicleClass)) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Конфигурация прицела для техники
     */
    public static class ReticleConfig {
        ResourceLocation outlineTexture;
        ResourceLocation reticleTexture;
        // Thermal прицелы
        ResourceLocation thermalReticleTexture4x;
        ResourceLocation thermalReticleTexture12x;
        ResourceLocation thermalOutlineTexture;
        // Разные прицелы для разных оружий (Map<weaponName, reticleTexture>)
        Map<String, ResourceLocation> weaponReticles = new HashMap<>();
        // Прицелы для оружий с ТПВ (Map<weaponName, reticleTexture>) - используются только при включенном ТПВ
        Map<String, ResourceLocation> thermalWeaponReticles = new HashMap<>();
        float reticleColorR = 1.0f;
        float reticleColorG = 1.0f;
        float reticleColorB = 1.0f;
        float reticleColorA = 1.0f;
        float reticleScale = 1.0f;
        float zoomScale = 1.5f;
        float outlineScale = 1.0f; // Масштаб для outline (для увеличения поля обзора)
        boolean noiseEnabled = false;
        int noiseDuration = 15;
        float noiseAlpha = 0.3f;
        int seatIndex = 0;
        boolean hudRightSide = false;
        boolean thermalVisionAllowed = true; // Разрешено ли ТПВ для этой техники
        boolean isDigital = false; // Применять ли digital sight шейдер
        boolean fullscreenReticle = true; // Растягивать ли прицел на весь экран (true) или сохранять пропорции (false)
        
        public ReticleConfig setOutline(String path) {
            this.outlineTexture = new ResourceLocation(path.split(":")[0], path.split(":")[1]);
            return this;
        }
        
        public ReticleConfig setReticle(String path) {
            this.reticleTexture = new ResourceLocation(path.split(":")[0], path.split(":")[1]);
            return this;
        }
        
        public ReticleConfig setReticleColor(float r, float g, float b, float a) {
            this.reticleColorR = r;
            this.reticleColorG = g;
            this.reticleColorB = b;
            this.reticleColorA = a;
            return this;
        }
        
        public ReticleConfig setReticleScale(float scale) {
            this.reticleScale = scale;
            return this;
        }
        
        public ReticleConfig setNoiseEnabled(boolean enabled) {
            this.noiseEnabled = enabled;
            return this;
        }
        
        public ReticleConfig setNoiseDuration(int ticks) {
            this.noiseDuration = ticks;
            return this;
        }
        
        public ReticleConfig setNoiseAlpha(float alpha) {
            this.noiseAlpha = alpha;
            return this;
        }
        
        public ReticleConfig setSeatIndex(int index) {
            this.seatIndex = index;
            return this;
        }
        
        public ReticleConfig setZoomScale(float scale) {
            this.zoomScale = scale;
            return this;
        }
        
        public ReticleConfig setHudRightSide(boolean rightSide) {
            this.hudRightSide = rightSide;
            return this;
        }
        
        public ReticleConfig setThermalReticle4x(String path) {
            this.thermalReticleTexture4x = new ResourceLocation(path.split(":")[0], path.split(":")[1]);
            return this;
        }
        
        public ReticleConfig setThermalReticle12x(String path) {
            this.thermalReticleTexture12x = new ResourceLocation(path.split(":")[0], path.split(":")[1]);
            return this;
        }
        
        public ReticleConfig setThermalOutline(String path) {
            this.thermalOutlineTexture = new ResourceLocation(path.split(":")[0], path.split(":")[1]);
            return this;
        }
        
        public boolean hasThermalReticle() {
            return thermalReticleTexture4x != null || thermalReticleTexture12x != null;
        }
        
        public ReticleConfig setWeaponReticle(String weaponName, String path) {
            this.weaponReticles.put(weaponName, new ResourceLocation(path.split(":")[0], path.split(":")[1]));
            return this;
        }
        
        public ReticleConfig setOutlineScale(float scale) {
            this.outlineScale = scale;
            return this;
        }
        
        public ReticleConfig setThermalWeaponReticle(String weaponName, String path) {
            this.thermalWeaponReticles.put(weaponName, new ResourceLocation(path.split(":")[0], path.split(":")[1]));
            return this;
        }
        
        public ReticleConfig setThermalVisionAllowed(boolean allowed) {
            this.thermalVisionAllowed = allowed;
            return this;
        }
        
        public ReticleConfig setDigital(boolean digital) {
            this.isDigital = digital;
            return this;
        }
        
        public ReticleConfig setFullscreenReticle(boolean fullscreen) {
            this.fullscreenReticle = fullscreen;
            return this;
        }
    }
    
    /**
     * Проверяет, разрешено ли ТПВ для данной техники
     */
    public static boolean isThermalVisionAllowed(VehicleEntity vehicle, int seatIndex) {
        ReticleConfig config = getConfigForVehicle(vehicle, seatIndex);
        if (config == null) {
            return true; // По умолчанию разрешено, если нет конфигурации
        }
        return config.thermalVisionAllowed;
    }
}
