package tech.vvp.vvp.tools;

import com.atsuishio.superbwarfare.init.ModParticleTypes;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class VehicleParticleHelper {

    private VehicleParticleHelper() {
    }

    public static void spawnMuzzleFlash(Level level, Vec3 muzzlePos, Vec3 direction) {
        spawnMuzzleFlash(level, muzzlePos, direction, 1.0f);
    }

    public static void spawnMuzzleFlash(Level level, Vec3 muzzlePos, Vec3 direction, float scale) {
        if (!(level instanceof ServerLevel serverLevel) || muzzlePos == null) {
            return;
        }

        Vec3 dir = direction == null ? Vec3.ZERO : direction;
        if (dir.lengthSqr() > 0) {
            dir = dir.normalize();
        }

        double flameLength = 0.2 * scale;
        double sparkLength = 0.12 * scale;
        double smokeSpread = 0.08 * scale;

        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLASH, muzzlePos.x, muzzlePos.y, muzzlePos.z,
                1, 0, 0, 0, 0, true);

        ParticleTool.sendParticle(serverLevel, ParticleTypes.SMALL_FLAME, muzzlePos.x, muzzlePos.y, muzzlePos.z,
                6, dir.x * flameLength, dir.y * flameLength, dir.z * flameLength, 0.02, true);

        ParticleTool.sendParticle(serverLevel, ParticleTypes.FLAME, muzzlePos.x, muzzlePos.y, muzzlePos.z,
                3, dir.x * sparkLength, dir.y * sparkLength, dir.z * sparkLength, 0.01, true);

        ParticleTool.sendParticle(serverLevel, ParticleTypes.CAMPFIRE_COSY_SMOKE, muzzlePos.x, muzzlePos.y, muzzlePos.z,
                2, smokeSpread, smokeSpread * 0.5, smokeSpread, 0.005, true);

        ParticleTool.sendParticle(serverLevel, ModParticleTypes.FIRE_STAR.get(), muzzlePos.x, muzzlePos.y, muzzlePos.z,
                4, dir.x * sparkLength, dir.y * sparkLength, dir.z * sparkLength, 0.15 * scale, true);
    }

}
