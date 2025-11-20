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

    public static final RegistryObject<SoundEvent> DOOR = register("door");

    // Звуки для ПТУРА
    public static final RegistryObject<SoundEvent> TOW_1P = register("tow_1p");
    public static final RegistryObject<SoundEvent> TOW_3P = register("tow_3p");
    public static final RegistryObject<SoundEvent> TOW_FAR = register("tow_far");
    public static final RegistryObject<SoundEvent> TOW_RELOAD = register("tow_reload");


    public static final RegistryObject<SoundEvent> ROCKET_SOUND = register("rocket_sound");

    public static final RegistryObject<SoundEvent> BMP_IDLE = register("bmp_idle");
    public static final RegistryObject<SoundEvent> BMP_START = register("bmp_start");

    public static final RegistryObject<SoundEvent> UH60_IDLE = register("uh60_idle");
    public static final RegistryObject<SoundEvent> UH60_START = register("uh60_start");

    public static final RegistryObject<SoundEvent> MI8_IDLE = register("mi8_idle");
    public static final RegistryObject<SoundEvent> MI8_START = register("mi8_start");

    // Ваша музыка
    public static final RegistryObject<SoundEvent> RADIOHEAD = register("radiohead");

    // Звук двигателя BTR-80A
    public static final RegistryObject<SoundEvent> BTR_80A_ENGINE = register("btr_80a_engine");

    // Звук двигателя Stryker
    public static final RegistryObject<SoundEvent> STRYKER_ENGINE = register("stryker_engine");

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


    public static final RegistryObject<SoundEvent> REMONT = register("remont");
    public static final RegistryObject<SoundEvent> SPRAY = register("spray");

    public static final RegistryObject<SoundEvent> BTR80_1P = register("pushka_2a72_1p");
    public static final RegistryObject<SoundEvent> BTR80_3P = register("pushka_2a72_3p");
    public static final RegistryObject<SoundEvent> BTR80_FAR = register("pushka_2a72_far");
    public static final RegistryObject<SoundEvent> BTR80_VERYFAR = register("pushka_2a72_veryfar");


    public static final RegistryObject<SoundEvent> ABRAMS_1P = register("abrams_1p");
    public static final RegistryObject<SoundEvent> ABRAMS_3P = register("abrams_3p");
    public static final RegistryObject<SoundEvent> ABRAMS_FAR = register("abrams_far");
    public static final RegistryObject<SoundEvent> ABRAMS_VERYFAR = register("abrams_veryfar");

    public static final RegistryObject<SoundEvent> T90_AUTORELOAD = register("t90_autoreload");

    public static final RegistryObject<SoundEvent> T72_AUTORELOAD = register("t72_autoreload");
    public static final RegistryObject<SoundEvent> T72_ENGINE_IDLE = register("t72_engine_idle");

    public static final RegistryObject<SoundEvent> SU25_ENGINE = register("su25_engine");

    public static final RegistryObject<SoundEvent> F16_ENGINE = register("f16_engine");

    public static final RegistryObject<SoundEvent> T64_RELOAD = register("t64_reload");

    // Звуки для Pantsir-S1
    public static final RegistryObject<SoundEvent> PANTSIR_LOCKING = register("pantsir_locking");
    public static final RegistryObject<SoundEvent> PANTSIR_LOCKED = register("pantsir_locked");




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