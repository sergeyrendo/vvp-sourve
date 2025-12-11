package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.Su25Model;
import tech.vvp.vvp.entity.vehicle.Su25Entity;

public class Su25Renderer extends VehicleRenderer<Su25Entity> {
    public Su25Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Su25Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Su25Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();

        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }

        return textures[0];
    }
}
