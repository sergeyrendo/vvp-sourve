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
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Pantsir-S1 ЗРК - требует захват цели для стрельбы ракетами
 */
public class PantsirS1Entity extends CamoVehicleBase {

    private static final ResourceLocation[] CAMO_TEXTURES = {
        new ResourceLocation("vvp", "textures/entity/pantsir_s1.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_haki.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo2.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo3.png"),
        new ResourceLocation("vvp", "textures/entity/pantsir_s1_camo4.png")
    };
    
    private static final String[] CAMO_NAMES = {"Standard", "Haki", "Camo2", "Camo3", "Camo4"};

    public PantsirS1Entity(EntityType<PantsirS1Entity> type, Level world) {
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
                .custom((source, damage) -> getSourceAngle(source, 0.4f) * damage);
    }

    @Override
    public void baseTick() {
        super.baseTick();
    }

    /**
     * Переопределяем canShoot для блокировки стрельбы ракетами без захвата цели на клиенте
     * Это предотвращает проигрывание звука стрельбы когда цель не захвачена
     */
    @Override
    public boolean canShoot(LivingEntity living) {
        int seatIndex = getSeatIndex(living);
        int weaponIndex = getSelectedWeapon(seatIndex);
        
        // Пушка (индекс 0) - всегда можно стрелять
        if (weaponIndex == 0) {
            return super.canShoot(living);
        }
        
        // Ракеты (индекс 1) - требуется захват цели
        if (weaponIndex == 1) {
            // На клиенте проверяем статус захвата через ClientEventHandler
            if (this.level().isClientSide) {
                return isTargetLocked() && super.canShoot(living);
            }
            return super.canShoot(living);
        }
        
        return super.canShoot(living);
    }
    
    /**
     * Проверяет захвачена ли цель на клиенте
     */
    @OnlyIn(Dist.CLIENT)
    private boolean isTargetLocked() {
        // ClientEventHandler.lockOnVehicle показывает захвачена ли цель
        return ClientEventHandler.lockOnVehicle;
    }

    /**
     * Переопределяем vehicleShoot для блокировки стрельбы ракетами без захвата цели на сервере
     * Индекс оружия 0 = пушка (всегда можно стрелять)
     * Индекс оружия 1 = ракеты (требуется захват цели - uuid != null)
     */
    @Override
    public void vehicleShoot(@Nullable LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        if (living == null) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        
        int seatIndex = getSeatIndex(living);
        int weaponIndex = getSelectedWeapon(seatIndex);
        
        // Пушка (индекс 0) - всегда можно стрелять
        if (weaponIndex == 0) {
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        
        // Ракеты (индекс 1) - требуется захват цели (uuid != null)
        if (weaponIndex == 1) {
            // Если цель не захвачена (uuid == null) - блокируем стрельбу
            if (uuid == null) {
                return;
            }
            
            // Цель захвачена - разрешаем стрельбу
            super.vehicleShoot(living, uuid, targetPos);
            return;
        }
        
        // Для других оружий - стандартное поведение
        super.vehicleShoot(living, uuid, targetPos);
    }
}
