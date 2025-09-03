package tech.vvp.vvp.radar;

import com.atsuishio.superbwarfare.entity.vehicle.base.AirEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.HelicopterEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.S2CRadarSyncPacket;

import java.util.ArrayList;
import java.util.List;

public interface IRadarVehicle {

    String RADAR_NBT_KEY = "vvp_radar_enabled";

    // Вкл/выкл флаг (PersistentData)
    default boolean isRadarEnabled() {
        Entity self = (Entity) this;
        return !self.getPersistentData().contains(RADAR_NBT_KEY)
                || self.getPersistentData().getBoolean(RADAR_NBT_KEY);
    }

    default void setRadarEnabled(boolean enabled) {
        ((Entity) this).getPersistentData().putBoolean(RADAR_NBT_KEY, enabled);
    }

    // Дальность сканирования (переопределяй в технике при необходимости)
    default int getRadarRange() {
        return 150;
    }

    // Фильтр целей (по умолчанию воздух)
    default boolean isValidRadarTarget(Entity e) {
        return (e instanceof HelicopterEntity || e instanceof AirEntity) && e != this;
    }

    // Стоимость энергии за один скан (раз в 20 тиков)
    default int getRadarEnergyCostPerScan() {
        return 35;
    }

    // КРЮЧОК: попытка списать энергию радара. По умолчанию — ничего не списывает.
    // Переопредели в сущности (наследнике ContainerMobileVehicleEntity) и вызови this.consumeEnergy(cost).
    default boolean consumeRadarEnergy() {
        return true;
    }

    // Скан + отправка клиенту пилота
    default void scanAndSendRadarTo(ServerPlayer player) {
        if (!isRadarEnabled()) return;

        Entity self = (Entity) this;
        var level = self.level();
        var box = self.getBoundingBox().inflate(getRadarRange());
        List<Entity> potentialTargets = level.getEntities(self, box, this::isValidRadarTarget);
        if (potentialTargets.isEmpty()) return;

        List<Vec3> pos = new ArrayList<>(potentialTargets.size());
        for (Entity target : potentialTargets) {
            pos.add(target.position());
        }

        VVPNetwork.VVP_HANDLER.sendTo(
                new S2CRadarSyncPacket(pos),
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT
        );
    }
}