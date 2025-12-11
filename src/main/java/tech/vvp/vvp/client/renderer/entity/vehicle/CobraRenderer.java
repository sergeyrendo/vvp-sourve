package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.CobraModel;
import tech.vvp.vvp.entity.vehicle.CobraEntity;

public class CobraRenderer extends VehicleRenderer<CobraEntity> {
    public CobraRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CobraModel());
    }

    @Override
    public ResourceLocation getTextureLocation(CobraEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return (camoType >= 0 && camoType < textures.length) ? textures[camoType] : textures[0];
    }
}
