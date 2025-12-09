package tech.vvp.vvp.entity.vehicle;

import net.minecraft.resources.ResourceLocation;

/**
 * Интерфейс для техники с поддержкой камуфляжа
 */
public interface ICamoVehicle {
    
    /**
     * Получить текущий тип камуфляжа
     */
    int getCamoType();
    
    /**
     * Установить тип камуфляжа
     */
    void setCamoType(int camoType);
    
    /**
     * Переключить на следующий камуфляж
     */
    void cycleCamo();
    
    /**
     * Получить массив доступных текстур камуфляжа
     */
    ResourceLocation[] getCamoTextures();
    
    /**
     * Получить названия камуфляжей для отображения игроку
     */
    String[] getCamoNames();
}
