package tech.vvp.vvp.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.renderer.entity.*;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MI24.get(), mi24Renderer::new);
        event.registerEntityRenderer(ModEntities.MI24POL.get(), mi24polRenderer::new);
        event.registerEntityRenderer(ModEntities.MI24UKR.get(), mi24ukrRenderer::new);
        event.registerEntityRenderer(ModEntities.M997.get(), m997Renderer::new);
        event.registerEntityRenderer(ModEntities.M997_GREEN.get(), m997_greenRenderer::new);
        event.registerEntityRenderer(ModEntities.COBRA.get(), cobraRenderer::new);
        event.registerEntityRenderer(ModEntities.COBRASHARK.get(), cobrasharkRenderer::new);
        event.registerEntityRenderer(ModEntities.BTR80A.get(), btr80aRenderer::new);
        event.registerEntityRenderer(ModEntities.BTR_80A_1.get(), btr80a_1Renderer::new);
        event.registerEntityRenderer(ModEntities.F35.get(), f35Renderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER.get(), strykerRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_HAKI.get(), stryker_hakiRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_1.get(), stryker_1Renderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_1_HAKI.get(), stryker_1_hakiRenderer::new);
        event.registerEntityRenderer(ModEntities.BTR_4.get(), btr4Renderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR.get(), terminatorRenderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR_HAKI.get(), terminatorHakiRenderer::new);



    }

    /**
     * Регистрация всех рендереров сущностей
     * @deprecated Используйте метод registerEntityRenderers с аннотацией @SubscribeEvent
     */
    @Deprecated
    public static void register() {
        // Эта функция больше не используется
        // Все регистрации перенесены в метод registerEntityRenderers
    }
}

