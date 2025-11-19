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
        event.registerEntityRenderer(ModEntities.BRADLEY.get(), BradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR.get(), TerminatorRenderer::new);
        event.registerEntityRenderer(ModEntities.PANTSIR_S1.get(), PantsirS1Renderer::new);
        event.registerEntityRenderer(ModEntities.sosna.get(), SosnaRenderer::new);
        event.registerEntityRenderer(ModEntities.FAB_500.get(), Fab500Renderer::new);
        event.registerEntityRenderer(ModEntities.FAB_250.get(), Fab250Renderer::new);
        event.registerEntityRenderer(ModEntities.LMUR.get(), LmurRenderer::new);
        event.registerEntityRenderer(ModEntities.X25.get(), X25Renderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_57E6.get(), E6_57Renderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_9M340.get(), M337Renderer::new);
        event.registerEntityRenderer(ModEntities.S_130.get(), S130Renderer::new);
        event.registerEntityRenderer(ModEntities.CANNON_ATGM_SHELL.get(), CannonAtgmShellRenderer::new);
        event.registerEntityRenderer(ModEntities.T90_M.get(), T90MRenderer::new);
        event.registerEntityRenderer(ModEntities.T90_M_22.get(), T90M22Renderer::new);
        event.registerEntityRenderer(ModEntities.T90_A.get(), T90ARenderer::new);
        event.registerEntityRenderer(ModEntities.BRM.get(), BrmRenderer::new);
        event.registerEntityRenderer(ModEntities.MI_28.get(), Mi28Renderer::new);
        event.registerEntityRenderer(ModEntities.BMP_3.get(), Bmp3Renderer::new);
        event.registerEntityRenderer(ModEntities.CHRYZANTEMA.get(), ChryzantemaRenderer::new);
        event.registerEntityRenderer(ModEntities.M1A2.get(), M1A2Renderer::new);
        event.registerEntityRenderer(ModEntities.UH60MOD.get(), Uh60ModRenderer::new);
        event.registerEntityRenderer(ModEntities.UH60.get(), Uh60Renderer::new);
        event.registerEntityRenderer(ModEntities.SU_25.get(), Su25Renderer::new);
        event.registerEntityRenderer(ModEntities.AH_1.get(), SuperCobraRenderer::new);
        event.registerEntityRenderer(ModEntities.H_FIRE.get(), HFireRenderer::new);
        event.registerEntityRenderer(ModEntities.MI_8.get(), Mi8Renderer::new);
        event.registerEntityRenderer(ModEntities.MI_8_MTV.get(), Mi8MTVRenderer::new);
        event.registerEntityRenderer(ModEntities.MI_8_AMTSH.get(), Mi8AMTSHRenderer::new);
        event.registerEntityRenderer(ModEntities.PUMA.get(), PumaRenderer::new);
        event.registerEntityRenderer(ModEntities.SPIKE_MISSLE.get(), SpikeATGMRenderer::new);
        event.registerEntityRenderer(ModEntities.TOW_MISSILE.get(), TOWRenderer::new);
        event.registerEntityRenderer(ModEntities.HRYZANTEMA.get(), HryzantemaRenderer::new);
        event.registerEntityRenderer(ModEntities.M1A2_SEP.get(), M1A2SepRenderer::new);
        event.registerEntityRenderer(ModEntities.BUSHMASTER.get(), BushmasterRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER.get(), StrykerRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_M1296.get(), Stryker_M1296Renderer::new);
        event.registerEntityRenderer(ModEntities.MI_28_1.get(), Mi28_1Renderer::new);
        event.registerEntityRenderer(ModEntities.TU_22_M3.get(), TU22M3Renderer::new);
        event.registerEntityRenderer(ModEntities.TOYOTA.get(), ToyotaRenderer::new);
        event.registerEntityRenderer(ModEntities.CHALLENGER.get(), ChallengerRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2.get(), Bmp2Renderer::new);
        event.registerEntityRenderer(ModEntities.T72_B3M.get(), T72B3MRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2M.get(), Bmp2MRenderer::new);
        event.registerEntityRenderer(ModEntities.PANTSIR_S1_MISSILE.get(), PantsirS1MissileRenderer::new);
        event.registerEntityRenderer(ModEntities.SOSNA_MISSILE.get(), SosnaMissileRenderer::new);





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

