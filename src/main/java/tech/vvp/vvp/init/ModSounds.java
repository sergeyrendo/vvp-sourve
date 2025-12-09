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

    // Ваша музыка
    public static final RegistryObject<SoundEvent> RADIOHEAD = register("radiohead");
    public static final RegistryObject<SoundEvent> M1128_RELOAD = register("m1128_reload");

    public static final RegistryObject<SoundEvent> HK_GMG_RELOAD = register("hk_gmg_reload");
    
    // Звук спрея для смены камуфляжа
    public static final RegistryObject<SoundEvent> SPRAY = register("spray");




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