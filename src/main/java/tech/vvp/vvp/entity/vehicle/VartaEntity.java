package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class VartaEntity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/varta.png"),
        new ResourceLocation("vvp", "textures/entity/varta_basic.png"),
        new ResourceLocation("vvp", "textures/entity/varta_black.png"),
        new ResourceLocation("vvp", "textures/entity/varta_sandy.png"),
        new ResourceLocation("vvp", "textures/entity/varta_snow.png")
    };

    private static final String[] CAMO_NAMES = {"Standard", "Basic", "Black", "Sandy", "Snow"};

    public VartaEntity(EntityType<VartaEntity> type, Level world) {
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

    @Override
    public void baseTick() {
        super.baseTick();
    }
}
