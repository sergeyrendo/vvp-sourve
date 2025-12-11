package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BrmEntity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/brm_black.png"),
        new ResourceLocation("vvp", "textures/entity/brm_camo.png"),
        new ResourceLocation("vvp", "textures/entity/brm_green.png"),
        new ResourceLocation("vvp", "textures/entity/brm_snow.png"),
        new ResourceLocation("vvp", "textures/entity/brm_zvezda.png")
    };
    
    private static final String[] CAMO_NAMES = {"Black", "Camo", "Green", "Snow", "Zvezda"};

    public BrmEntity(EntityType<BrmEntity> type, Level world) {
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
