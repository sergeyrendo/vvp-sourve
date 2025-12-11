package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.Bmp2Model;
import tech.vvp.vvp.entity.vehicle.Bmp2Entity;

public class Bmp2Renderer extends VehicleRenderer<Bmp2Entity> {
    public Bmp2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Bmp2Model());
    }

    @Override
    public ResourceLocation getTextureLocation(Bmp2Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        return (camoType >= 0 && camoType < textures.length) ? textures[camoType] : textures[0];
    }
}
