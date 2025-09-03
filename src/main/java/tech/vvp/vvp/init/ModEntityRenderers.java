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
        event.registerEntityRenderer(ModEntities.M997_GREEN.get(), m997_greenRenderer::new);
        event.registerEntityRenderer(ModEntities.COBRA.get(), cobraRenderer::new);
        event.registerEntityRenderer(ModEntities.UH60MOD.get(), uh60ModRenderer::new);
        event.registerEntityRenderer(ModEntities.BTR80A.get(), btr80aRenderer::new);
        event.registerEntityRenderer(ModEntities.F35.get(), f35Renderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER.get(), strykerRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_1.get(), stryker_1Renderer::new);
        event.registerEntityRenderer(ModEntities.BTR_4.get(), btr4Renderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR.get(), terminatorRenderer::new);
        event.registerEntityRenderer(ModEntities.UH60MOD.get(), uh60ModRenderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY_UKR.get(), BradleyUkrRenderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY.get(), BradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.HUMVEE.get(), HumveeRenderer::new);
        event.registerEntityRenderer(ModEntities.UH60.get(), uh60Renderer::new);
        event.registerEntityRenderer(ModEntities.FMTV.get(), FMTVRenderer::new);
        event.registerEntityRenderer(ModEntities.M60.get(), M60Renderer::new);
        event.registerEntityRenderer(ModEntities.M1A2.get(), M1A2Renderer::new);
        event.registerEntityRenderer(ModEntities.M1A2_SEP.get(), M1A2SepRenderer::new);
        event.registerEntityRenderer(ModEntities.T90.get(), T90Renderer::new);
        event.registerEntityRenderer(ModEntities.T90_2024.get(), T90_24Renderer::new);
        event.registerEntityRenderer(ModEntities.MI_28.get(), Mi28Renderer::new);
        event.registerEntityRenderer(ModEntities.FAB_500.get(), Fab500Renderer::new);




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

