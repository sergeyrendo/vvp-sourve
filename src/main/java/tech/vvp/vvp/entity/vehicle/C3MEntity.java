package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class C3MEntity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/2c3m_1.png"),
        new ResourceLocation("vvp", "textures/entity/2c3m_2.png"),
        new ResourceLocation("vvp", "textures/entity/2c3m_3.png"),
        new ResourceLocation("vvp", "textures/entity/2c3m_4.png")
    };
    
    private static final String[] CAMO_NAMES = {"Camo1", "Camo2", "Camo3", "Camo4"};

    public C3MEntity(EntityType<C3MEntity> type, Level world) {
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
