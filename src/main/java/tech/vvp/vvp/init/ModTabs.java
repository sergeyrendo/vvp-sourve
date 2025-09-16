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
    public static final RegistryObject<CreativeModeTab> VEHICLES = TABS.register("vvp", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.ICON_SPAWN_ITEM.get()))
            .title(Component.translatable("item_group.vvp.vvp"))
            .displayItems((parameters, output) -> {
                // RU
                output.accept(ContainerBlockItem.createInstance(ModEntities.TERMINATOR.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.KORNET.get()));
                // UKR
                output.accept(ContainerBlockItem.createInstance(ModEntities.BTR_4.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.BRADLEY_UKR.get()));
                // USA
                output.accept(ContainerBlockItem.createInstance(ModEntities.BRADLEY.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.STRYKER.get()));
                output.accept(ContainerBlockItem.createInstance(ModEntities.STRYKER_M1296.get()));

            })
            .build());

    public static final RegistryObject<CreativeModeTab> ARMOR_TAB = TABS.register("armor_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.vvp_armor_tab"))
                            .icon(() -> new ItemStack(ModItems.ARMOR_ICON.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.MULTICAM_HELMET.get());
                                output.accept(ModItems.MULTICAM_CHEST.get());
                                output.accept(ModItems.MI_28_HELMET.get());
                                output.accept(ModItems.MI_28_CHEST.get());
                                output.accept(ModItems.MANGAL_TURRET.get());
                                output.accept(ModItems.MANGAL_BODY.get());
                                output.accept(ModItems.SETKA_BODY.get());
                                output.accept(ModItems.SETKA_TURRET.get());
                                output.accept(ModItems.TENT.get());
                                output.accept(ModItems.KOROBKI.get());
                                output.accept(ModItems.SPRAY.get());
                                output.accept(ModItems.WRENCH.get());
                                output.accept(ModItems.FAB_500_ITEM.get());
                                output.accept(ModItems.S_13.get());

                            })
                            .build());
    
    /**
     * Event for adding items to creative tabs
     */
    @Mod.EventBusSubscriber(modid = VVP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Registration {
        @SubscribeEvent
        public static void register(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == ModTabs.VEHICLES.getKey()) {
                // Добавляем технику
            }
        }
    }
}
