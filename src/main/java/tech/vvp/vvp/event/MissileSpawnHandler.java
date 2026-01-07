package tech.vvp.vvp.event;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.entity.projectile.PantsirMissileEntity;

@Mod.EventBusSubscriber(modid = "vvp")
public class MissileSpawnHandler {
    
    @SubscribeEvent
    public static void onMissileSpawn(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof PantsirMissileEntity missile)) return;
        if (event.getLevel().isClientSide()) return;
        
        Vec3 velocity = missile.getDeltaMovement();
        if (velocity.lengthSqr() < 0.01) return;
        
        Vec3 direction = velocity.normalize();
        Vec3 currentPos = missile.position();
        Vec3 spawnPos = currentPos.subtract(direction.scale(0.5));
        
        missile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        missile.setInitialRotation(direction);
    }
}
