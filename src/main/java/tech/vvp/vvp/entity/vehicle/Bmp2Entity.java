package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.vvp.vvp.util.VehicleConfigHelper;

public class Bmp2Entity extends CamoVehicleBase {

    // Текстуры загружаются автоматически из конфига sbw/vehicles/bmp_2.json
    private static final ResourceLocation[] CAMO_TEXTURES = 
        VehicleConfigHelper.loadTexturesFromConfig("sbw/vehicles/bmp_2.json");

    private static final String[] CAMO_NAMES = new String[] {
        "Стандартный",
        "Камуфляж"
    };

    public Bmp2Entity(EntityType<Bmp2Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }
}
