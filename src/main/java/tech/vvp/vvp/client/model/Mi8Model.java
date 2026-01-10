package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.entity.vehicle.Mi8Entity;

public class Mi8Model extends VehicleModel<Mi8Entity> {

    @Override
    public ResourceLocation getModelResource(Mi8Entity entity) {
        return new ResourceLocation(VVP.MOD_ID, "geo/mi8.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Mi8Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();
        
        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }
        
        return textures[0];
    }

    @Override
    public ResourceLocation getAnimationResource(Mi8Entity entity) {
        return null; // No animations file needed
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return true;
    }

    @Override
    public @Nullable TransformContext<Mi8Entity> collectTransform(String boneName) {
        return switch (boneName) {
            case "vint" ->
                    (bone, vehicle, state) -> bone.setRotY(-Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "vint2" ->
                    (bone, vehicle, state) -> bone.setRotX(6 * Mth.lerp(state.getPartialTick(), vehicle.getPropellerRotO(), vehicle.getPropellerRot()));
            case "door" ->
                    (bone, vehicle, state) -> {
                        // Left door: [0,0,0] -> [2, 0, 22]
                        float progress = vehicle.getLeftDoorProgress(state.getPartialTick());
                        bone.setPosX(2f * progress);
                        bone.setPosZ(22f * progress);
                    };
            case "door2" ->
                    (bone, vehicle, state) -> {
                        // Right door: mirror [-2, 0, 22]
                        float progress = vehicle.getRightDoorProgress(state.getPartialTick());
                        bone.setPosX(-2f * progress);
                        bone.setPosZ(22f * progress);
                    };
            default -> super.collectTransform(boneName);
        };
    }
}
