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
        event.registerEntityRenderer(ModEntities.BTR_4.get(), btr4Renderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY_UKR.get(), BradleyUkrRenderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY.get(), BradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER.get(), StrykerRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_M1296.get(), Stryker_M1296Renderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR.get(), TerminatorRenderer::new);
        event.registerEntityRenderer(ModEntities.FAB_500.get(), Fab500Renderer::new);
        event.registerEntityRenderer(ModEntities.FAB_250.get(), Fab250Renderer::new);
        event.registerEntityRenderer(ModEntities.M224.get(), M224Renderer::new);
        event.registerEntityRenderer(ModEntities.KORNET.get(), KornetRenderer::new);
        event.registerEntityRenderer(ModEntities.CANNON_ATGM_SHELL.get(), CannonAtgmShellRenderer::new);




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

