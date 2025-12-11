package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.FMTVModel;
import tech.vvp.vvp.entity.vehicle.FMTVEntity;

public class FMTVRenderer extends VehicleRenderer<FMTVEntity> {
    public FMTVRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FMTVModel());
    }

    @Override
    public ResourceLocation getTextureLocation(FMTVEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        
        return textures[0];
    }
}
