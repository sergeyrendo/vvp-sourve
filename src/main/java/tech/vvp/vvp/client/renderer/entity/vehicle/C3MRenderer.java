package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.C3MModel;
import tech.vvp.vvp.entity.vehicle.C3MEntity;

public class C3MRenderer extends VehicleRenderer<C3MEntity> {
    public C3MRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new C3MModel());
    }

    @Override
    public ResourceLocation getTextureLocation(C3MEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return (camoType >= 0 && camoType < textures.length) ? textures[camoType] : textures[0];
    }
}
