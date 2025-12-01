package tech.vvp.vvp.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.client.renderer.entity.projectile.E6_57Renderer;
import tech.vvp.vvp.client.renderer.entity.projectile.Fab250Renderer;
import tech.vvp.vvp.client.renderer.entity.projectile.Fab500Renderer;
import tech.vvp.vvp.client.renderer.entity.X25Renderer;
import tech.vvp.vvp.client.renderer.entity.vehicle.*;

@Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.BTR_4.get(), btr4Renderer::new);
        event.registerEntityRenderer(ModEntities.BRADLEY.get(), BradleyRenderer::new);
        event.registerEntityRenderer(ModEntities.TERMINATOR.get(), TerminatorRenderer::new);
        event.registerEntityRenderer(ModEntities.T90_M.get(), T90MRenderer::new);
        event.registerEntityRenderer(ModEntities.T90_M_22.get(), T90M22Renderer::new);
        event.registerEntityRenderer(ModEntities.T90_A.get(), T90ARenderer::new);
        event.registerEntityRenderer(ModEntities.BRM.get(), BrmRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_3.get(), Bmp3Renderer::new);
        event.registerEntityRenderer(ModEntities.CHRYZANTEMA.get(), ChryzantemaRenderer::new);
        event.registerEntityRenderer(ModEntities.M1A2.get(), M1A2Renderer::new);
        event.registerEntityRenderer(ModEntities.PUMA.get(), PumaRenderer::new);
        event.registerEntityRenderer(ModEntities.M1A2_SEP.get(), M1A2SepRenderer::new);
        event.registerEntityRenderer(ModEntities.BUSHMASTER.get(), BushmasterRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER.get(), StrykerRenderer::new);
        event.registerEntityRenderer(ModEntities.STRYKER_M1296.get(), Stryker_M1296Renderer::new);
        event.registerEntityRenderer(ModEntities.TOYOTA.get(), ToyotaRenderer::new);
        event.registerEntityRenderer(ModEntities.FMTV.get(), FMTVRenderer::new);
        event.registerEntityRenderer(ModEntities.GAZ_TIGR.get(), GazTigrRenderer::new);
        event.registerEntityRenderer(ModEntities.MI_28.get(), Mi28Renderer::new);
        event.registerEntityRenderer(ModEntities.UH60_WEAPON.get(), Uh60WeaponRenderer::new);
        event.registerEntityRenderer(ModEntities.CHALLENGER.get(), ChallengerRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2.get(), Bmp2Renderer::new);
        event.registerEntityRenderer(ModEntities.T72_B3M.get(), T72B3MRenderer::new);
        event.registerEntityRenderer(ModEntities.BMP_2M.get(), Bmp2MRenderer::new);
        event.registerEntityRenderer(ModEntities.C3M.get(), C3MRenderer::new);
        event.registerEntityRenderer(ModEntities.URAL.get(), UralRenderer::new);
        event.registerEntityRenderer(ModEntities.VARTA.get(), VartaRenderer::new);
        event.registerEntityRenderer(ModEntities.TOW.get(), TowRenderer::new);
        event.registerEntityRenderer(ModEntities.PANTSIR_S1.get(), PantsirS1Renderer::new);
        event.registerEntityRenderer(ModEntities.SU_25.get(), Su25Renderer::new);
        event.registerEntityRenderer(ModEntities.ENTITY_57E6.get(), E6_57Renderer::new);
        event.registerEntityRenderer(ModEntities.FAB_500.get(), Fab500Renderer::new);
        event.registerEntityRenderer(ModEntities.FAB_250.get(), Fab250Renderer::new);
        event.registerEntityRenderer(ModEntities.X25.get(), X25Renderer::new);
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

