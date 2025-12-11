package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.UralModel;
import tech.vvp.vvp.entity.vehicle.UralEntity;

public class UralRenderer extends VehicleRenderer<UralEntity> {
    public UralRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new UralModel());
    }

    @Override
    public ResourceLocation getTextureLocation(UralEntity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();

        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }

        return textures[0];
    }
}
