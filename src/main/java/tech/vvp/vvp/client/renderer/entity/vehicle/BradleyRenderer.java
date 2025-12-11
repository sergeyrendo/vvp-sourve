package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.BradleyModel;
import tech.vvp.vvp.entity.vehicle.BradleyEntity;

public class BradleyRenderer extends VehicleRenderer<BradleyEntity> {
    public BradleyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BradleyModel());
    }

    @Override
    public ResourceLocation getTextureLocation(BradleyEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        
        return textures[0];
    }
}
