package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import java.lang.reflect.Field;

public class CobraEntity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/ah1w.png"),
        new ResourceLocation("vvp", "textures/entity/ah1w_dark.png"),
        new ResourceLocation("vvp", "textures/entity/ah1w_shark.png")
    };
    
    private static final String[] CAMO_NAMES = {"Standard", "Dark", "Shark"};

    private static Field propellerRotField;
    private static Field propellerRotOField;

    static {
        try {
            Class<?> vehicleClass = Class.forName("com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity");
            propellerRotField = vehicleClass.getDeclaredField("propellerRot");
            propellerRotField.setAccessible(true);
            propellerRotOField = vehicleClass.getDeclaredField("propellerRotO");
            propellerRotOField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CobraEntity(EntityType<CobraEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }
    
    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    public float getPropellerRot() {
        try {
            return propellerRotField != null ? (float) propellerRotField.get(this) : 0f;
        } catch (Exception e) {
            return 0f;
        }
    }

    public float getPropellerRotO() {
        try {
            return propellerRotOField != null ? (float) propellerRotOField.get(this) : 0f;
        } catch (Exception e) {
            return 0f;
        }
    }
}
