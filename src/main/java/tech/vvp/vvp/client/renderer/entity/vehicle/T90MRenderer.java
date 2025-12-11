package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.T90MModel;
import tech.vvp.vvp.entity.vehicle.T90MEntity;

public class T90MRenderer extends VehicleRenderer<T90MEntity> {
    public T90MRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T90MModel());
    }

    @Override
    public ResourceLocation getTextureLocation(T90MEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        
        return super.getTextureLocation(entity);
    }
}
