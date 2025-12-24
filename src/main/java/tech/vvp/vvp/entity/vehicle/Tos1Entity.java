package tech.vvp.vvp.entity.vehicle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class Tos1Entity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/t72b3m.png"), // Используем T-72 как базу
        new ResourceLocation("vvp", "textures/entity/t72b3m_camo.png"),
        new ResourceLocation("vvp", "textures/entity/t72b3m_sandy.png")
    };
    
    private static final String[] CAMO_NAMES = {"Green", "Camo", "Sandy"};

    public Tos1Entity(EntityType<Tos1Entity> type, Level world) {
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
}
