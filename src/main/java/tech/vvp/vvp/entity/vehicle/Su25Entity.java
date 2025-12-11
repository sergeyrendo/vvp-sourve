package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

public class Su25Entity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/su25.png"),
        new ResourceLocation("vvp", "textures/entity/su25_1.png"),
        new ResourceLocation("vvp", "textures/entity/su25_2.png"),
        new ResourceLocation("vvp", "textures/entity/su25_3.png"),
        new ResourceLocation("vvp", "textures/entity/su25_4.png"),
        new ResourceLocation("vvp", "textures/entity/su25_5.png")
    };
    
    private static final String[] CAMO_NAMES = {"Standard", "Camo1", "Camo2", "Camo3", "Camo4", "Camo5"};

    public Su25Entity(EntityType<Su25Entity> type, Level world) {
        super(type, world);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }
    
    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier()
                .custom((source, damage) -> getSourceAngle(source, 0.25f) * damage);
    }

    @Override
    public boolean canShoot(LivingEntity living) {
        // Для X-25 (индекс 3) требуется захват цели
        if (getWeaponIndex(living) == 3) {
            if (this.level().isClientSide) {
                // На клиенте проверяем lockOnVehicle
                return isLockedOnClient() && super.canShoot(living);
            }
        }
        return super.canShoot(living);
    }

    @OnlyIn(Dist.CLIENT)
    private boolean isLockedOnClient() {
        return ClientEventHandler.lockOnVehicle;
    }

    @Override
    public void vehicleShoot(LivingEntity living, UUID uuid, Vec3 targetPos) {
        // Проверяем, выбрана ли ракета X-25 (индекс 3)
        if (getWeaponIndex(living) == 3) {
            // Блокируем стрельбу если цель не захвачена (uuid == null означает нет цели)
            if (uuid == null) {
                return; // Не стреляем без захвата
            }
        }
        
        super.vehicleShoot(living, uuid, targetPos);
    }

    public int getWeaponIndex(LivingEntity living) {
        int seatIndex = getSeatIndex(living);
        return getWeaponIndex(seatIndex);
    }
}
