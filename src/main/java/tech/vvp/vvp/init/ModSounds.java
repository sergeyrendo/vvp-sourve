package tech.vvp.vvp.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;
import tech.vvp.vvp.VVP;

/**
 * Класс для регистрации звуков, используемых в моде
 */
public class ModSounds {
    // Создаем регистр для звуков
    public static final DeferredRegister<SoundEvent> REGISTRY = 
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, VVP.MOD_ID);
    
    // Звуки для YX-100 CAD System
    public static final RegistryObject<SoundEvent> YX_100_FIRE_1P = register("yx_100_fire_1p");
    public static final RegistryObject<SoundEvent> YX_100_FIRE_3P = register("yx_100_fire_3p");
    public static final RegistryObject<SoundEvent> YX_100_FAR = register("yx_100_far");
    public static final RegistryObject<SoundEvent> YX_100_VERY_FAR = register("yx_100_veryfar");
    public static final RegistryObject<SoundEvent> YX_100_RELOAD = register("yx_100_reload");
    
    // Звуки для 30мм пушки ВДВ
    public static final RegistryObject<SoundEvent> M2_1P = register("m2_1p");
    public static final RegistryObject<SoundEvent> M2_3P = register("m2_3p");
    public static final RegistryObject<SoundEvent> M2_FAR = register("m2_far");
    public static final RegistryObject<SoundEvent> M2_VERYFAR = register("m2_veryfar");
    
    // Звук для выскакивания дыма
    public static final RegistryObject<SoundEvent> SMOKE_FIRE = register("smoke_fire");
    
    // Звук двигателя танкаa
    public static final RegistryObject<SoundEvent> TANK_ENGINE = register("tank_engine");

    // Звуки для ПТУРА
    public static final RegistryObject<SoundEvent> TOW_1P = register("tow_1p");
    public static final RegistryObject<SoundEvent> TOW_3P = register("tow_3p");
    public static final RegistryObject<SoundEvent> TOW_FAR = register("tow_far");
    public static final RegistryObject<SoundEvent> TOW_RELOAD = register("tow_reload");

    // Ваша музыка
    public static final RegistryObject<SoundEvent> RADIOHEAD = register("radiohead");

    // Звук двигателя BTR-80A
    public static final RegistryObject<SoundEvent> BTR_80A_ENGINE = register("btr_80a_engine");

    // Звук двигателя Stryker
    public static final RegistryObject<SoundEvent> STRYKER_ENGINE = register("stryker_engine");
    
    // Звуки для 2A72
    public static final RegistryObject<SoundEvent> BTR_2A72_P1 = register("btr_2a72_p1");
    public static final RegistryObject<SoundEvent> BTR_2A72_P3 = register("btr_2a72_p3");
    public static final RegistryObject<SoundEvent> BTR_2A72_FAR = register("btr_2a72_far");
    public static final RegistryObject<SoundEvent> BTR_2A72_VERYFAR = register("btr_2a72_veryfar");

    // Звуки для мотоцикла
    public static final RegistryObject<SoundEvent> PLANETA_ENGINE = register("planeta_engine");

    // Звуки для F35
    public static final RegistryObject<SoundEvent> F35_ENGINE = register("f35_engine");
    
    // Звуки для MI24
    public static final RegistryObject<SoundEvent> MI24_ENGINE = register("mi24_engine");

    // Звуки дляBushmaster
    public static final RegistryObject<SoundEvent> BUSHMASTER_1P = register("bushmaster_1p");
    public static final RegistryObject<SoundEvent> BUSHMASTER_3P = register("bushmaster_3p");
    public static final RegistryObject<SoundEvent> BUSHMASTER_FAR = register("bushmaster_far");
    public static final RegistryObject<SoundEvent> BUSHMASTER_VERYFAR = register("bushmaster_veryfar");
    
    // Звуки для M1128
    public static final RegistryObject<SoundEvent> M1128_1P = register("m1128_1p");
    public static final RegistryObject<SoundEvent> M1128_3P = register("m1128_3p");
    public static final RegistryObject<SoundEvent> M1128_FAR = register("m1128_far");
    public static final RegistryObject<SoundEvent> M1128_VERYFAR = register("m1128_veryfar");
    public static final RegistryObject<SoundEvent> M1128_RELOAD = register("m1128_reload");
    
    public static final RegistryObject<SoundEvent> HUMVEE_ENGINE = register("humvee_engine");

    public static final RegistryObject<SoundEvent> COBRA_ENGINE = register("cobra_engine");
    public static final RegistryObject<SoundEvent> VAZIK_ENGINE = register("vazik_engine");
    /**
     * Вспомогательный метод для регистрации звуков
     * @param name Название звука (без пространства имен)
     * @return RegistryObject для звукового события
     */
    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(VVP.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
} 