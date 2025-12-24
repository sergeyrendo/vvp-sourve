package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.Tos1Model;
import tech.vvp.vvp.entity.vehicle.ICamoVehicle;
import tech.vvp.vvp.entity.vehicle.Tos1Entity;

public class Tos1Renderer extends VehicleRenderer<Tos1Entity> {

    public Tos1Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Tos1Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Tos1Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return (camoType >= 0 && camoType < textures.length) ? textures[camoType] : textures[0];
    }
}
