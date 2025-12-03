package tech.vvp.vvp.init;

import com.atsuishio.superbwarfare.item.common.container.ContainerBlockItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import tech.vvp.vvp.VVP;
import tech.vvp.vvp.init.ModItems;
import tech.vvp.vvp.init.ModEntities;

@SuppressWarnings("unused")
public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VVP.MOD_ID);

    // Общая вкладка со всеми предметами мода
    public static final RegistryObject<CreativeModeTab> NATO_VEHICLE_TAB = TABS.register("nato_tab", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.NATO_TAB_ICON.get()))
            .title(Component.translatable("natotab.vvp_natovehicle_tab"))
            .displayItems((parameters, output) -> {
                output.accept(ContainerBlockItem.createInstance(ModEntities.PUMA.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.CHALLENGER.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.M1A2_SEP.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.VARTA.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.FMTV.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.M142_HIMARS.get()));
                output.accept(ModItems.HK_GMG_ITEM.get());

            })
            .build());

    public static final RegistryObject<CreativeModeTab> RU_VEHICLE_TAB = TABS.register("ru_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("rutab.vvp_ruvehicle_tab"))
                    .icon(() -> new ItemStack(ModItems.RU_TAB_ICON.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ContainerBlockItem.createInstance(ModEntities.BMP_3.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.BMP_2.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.BMP_2M.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.T72_B3M.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.T90_M.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.TERMINATOR.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.URAL.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.GAZ_TIGR.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.MI_28.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.PANTSIR_S1.get()));
                        output.accept(ContainerBlockItem.createInstance(ModEntities.SU_25.get()));

                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> ARMOR_TAB = TABS.register("armor_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.vvp_armor_tab"))
                            .icon(() -> new ItemStack(ModItems.ARMOR_ICON.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.MULTICAM_HELMET.get());
                                output.accept(ModItems.MULTICAM_CHEST.get());
                                output.accept(ModItems.CREW_HELMET.get());
                                output.accept(ModItems.UKR_V2_HELMET.get());
                                output.accept(ModItems.UKR_CHEST.get());
                                output.accept(ModItems.UKR_V2_CHEST.get());
                                output.accept(ModItems.PMC_HELMET.get());
                                output.accept(ModItems.PMC_CHEST.get());
                                output.accept(ModItems.PMC_V2_CHEST.get());
                                output.accept(ModItems.RUS_HELMET.get());
                                output.accept(ModItems.RUS_HELMET_2.get());
                                output.accept(ModItems.RUS_HELMET_3.get());
                                output.accept(ModItems.RUS_ARMOR.get());
                                output.accept(ModItems.RUS_ARMOR_2.get());
                                output.accept(ModItems.RUS_ARMOR_3.get());
                                output.accept(ModItems.PANAMA.get());
                                output.accept(ModItems.KEPKA.get());
                                output.accept(ModItems.BERETA.get());
                                output.accept(ModItems.MI_28_HELMET.get());
                                output.accept(ModItems.MI_28_CHEST.get());
                                output.accept(ModItems.MANGAL_TURRET.get());
                                output.accept(ModItems.MANGAL_BODY.get());
                                output.accept(ModItems.SETKA_BODY.get());
                                output.accept(ModItems.SETKA_TURRET.get());
                                output.accept(ModItems.BMP3M_BODY.get());
                                output.accept(ModItems.CACTUS_TURRET_ITEM.get());
                                output.accept(ModItems.TENT.get());
                                output.accept(ModItems.KOROBKI.get());
                                output.accept(ModItems.SPRAY.get());
                                output.accept(ModItems.WRENCH.get());
                                output.accept(ModItems.FAB_500_ITEM.get());
                                output.accept(ModItems.FAB_250_ITEM.get());
                                output.accept(ModItems.LMUR_ITEM.get());
                                output.accept(ModItems.X25_ITEM.get());
                                output.accept(ModItems.AIM_120_ITEM.get());
                                output.accept(ModItems.R73_ITEM.get());
                                output.accept(ModItems.ITEM_57E6.get());
                                output.accept(ModItems.ITEM_9M340.get());
                                output.accept(ModItems.HFIRE_ITEM.get());
                                output.accept(ModItems.HRYZANTEMA_ITEM.get());
                                output.accept(ModItems.S_13.get());
                                output.accept(ModItems.GMLRS_M31.get());

                            })
                            .build());
    
    /**
     * Event for adding items to creative tabs
     */
    @Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void register(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == ModTabs.NATO_VEHICLE_TAB.getKey()) {
                // Добавляем технику
            } else if (event.getTabKey() == ModTabs.RU_VEHICLE_TAB.getKey()) {

            }
        }
    }
}
