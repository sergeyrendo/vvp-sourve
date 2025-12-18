package tech.vvp.vvp.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;

/**
 * Обработчик свечения сущностей при thermal vision
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = VVP.MOD_ID, value = Dist.CLIENT)
public class ThermalEntityGlowHandler {
    
    /**
     * Рендерит свечение сущностей при thermal vision
     */
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (!ThermalVisionHandler.isThermalVisionEnabled()) {
            return;
        }
        
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        // Рендерим свечение для всех сущностей в радиусе видимости
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        
        poseStack.pushPose();
        
        // Проходим по всем сущностям в радиусе видимости
        double viewDistance = mc.gameRenderer.getRenderDistance() * 16.0;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity == mc.player) {
                continue; // Пропускаем игрока
            }
            
            if (!entity.isAlive()) {
                continue;
            }
            
            // Проверяем расстояние
            double distance = entity.distanceToSqr(mc.player);
            if (distance > viewDistance * viewDistance) {
                continue;
            }
            
            // Рендерим свечение для живых сущностей
            if (entity instanceof LivingEntity) {
                renderEntityGlow(poseStack, bufferSource, dispatcher, entity, event.getPartialTick());
            }
        }
        
        poseStack.popPose();
        bufferSource.endBatch();
    }
    
    /**
     * Рендерит свечение сущности
     */
    private static void renderEntityGlow(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, 
                                         EntityRenderDispatcher dispatcher, Entity entity, float partialTick) {
        try {
            poseStack.pushPose();
            
            // Перемещаемся к позиции сущности
            double x = entity.getX() - dispatcher.camera.getPosition().x;
            double y = entity.getY() - dispatcher.camera.getPosition().y;
            double z = entity.getZ() - dispatcher.camera.getPosition().z;
            
            poseStack.translate(x, y, z);
            
            // Рендерим сущность с максимальной яркостью для эффекта свечения
            // Используем максимальный packedLight (0xF000F0) для создания эффекта свечения
            dispatcher.render(entity, 0, 0, 0, 0, partialTick, poseStack, bufferSource, 0xF000F0);
            
            poseStack.popPose();
        } catch (Exception e) {
            // Игнорируем ошибки рендеринга
        }
    }
}

