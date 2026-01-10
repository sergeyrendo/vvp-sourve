package tech.vvp.vvp.entity.vehicle;

import net.minecraft.resources.ResourceLocation;

/**
 * Interface for vehicles with camouflage support
 */
public interface ICamoVehicle {
    
    int getCamoType();
    
    void setCamoType(int camoType);
    
    void cycleCamo();
    
    ResourceLocation[] getCamoTextures();
    
    String[] getCamoNames();
}
