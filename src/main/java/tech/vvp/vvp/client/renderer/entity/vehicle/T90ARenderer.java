package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.T90AModel;
import tech.vvp.vvp.entity.vehicle.T90AEntity;

public class T90ARenderer extends VehicleRenderer<T90AEntity> {
    public T90ARenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new T90AModel());
    }

    @Override
    public ResourceLocation getTextureLocation(T90AEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return (camoType >= 0 && camoType < textures.length) ? textures[camoType] : textures[0];
    }
}
