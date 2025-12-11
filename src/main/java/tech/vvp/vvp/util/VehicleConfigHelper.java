package tech.vvp.vvp.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.VVP;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для загрузки конфигурации текстур из JSON файлов
 * Позволяет легко менять текстуры через конфиг без изменения кода
 */
public class VehicleConfigHelper {

    /**
     * Загружает текстуры из JSON конфига
     * @param configPath путь к JSON файлу (например "sbw/vehicles/bmp_2.json")
     * @return массив ResourceLocation с текстурами
     */
    public static ResourceLocation[] loadTexturesFromConfig(String configPath) {
        try {
            InputStream stream = VehicleConfigHelper.class.getResourceAsStream("/assets/vvp/" + configPath);
            if (stream == null) {
                return new ResourceLocation[0];
            }

            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
            JsonObject model = json.getAsJsonObject("Model");
            
            List<ResourceLocation> textures = new ArrayList<>();
            
            // Загружаем основную текстуру
            if (model.has("Texture")) {
                textures.add(parseResourceLocation(model.get("Texture").getAsString()));
            }
            
            // Загружаем дополнительные текстуры (Texture_2, Texture_3, и т.д.)
            int i = 2;
            while (model.has("Texture_" + i)) {
                textures.add(parseResourceLocation(model.get("Texture_" + i).getAsString()));
                i++;
            }
            
            stream.close();
            return textures.toArray(new ResourceLocation[0]);
            
        } catch (Exception e) {
            return new ResourceLocation[0];
        }
    }

    /**
     * Парсит строку в ResourceLocation
     * Поддерживает формат "modid:path" или просто "path"
     */
    private static ResourceLocation parseResourceLocation(String str) {
        if (str.contains(":")) {
            String[] parts = str.split(":", 2);
            return new ResourceLocation(parts[0], parts[1]);
        }
        return new ResourceLocation(VVP.MOD_ID, str);
    }
}
