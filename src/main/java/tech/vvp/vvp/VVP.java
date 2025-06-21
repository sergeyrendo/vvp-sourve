package tech.vvp.vvp;

import com.atsuishio.superbwarfare.Mod;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import org.slf4j.Logger;
import tech.vvp.vvp.client.gui.RadarHud;
import tech.vvp.vvp.init.*;
import tech.vvp.vvp.network.message.S2CRadarSyncPacket;
import net.minecraftforge.client.gui.overlay.OverlayRegistry;

import java.util.Optional;

@net.minecraftforge.fml.common.Mod(VVP.MOD_ID)
public class VVP {
    public static final String MOD_ID = "vvp";
    private static final Logger LOGGER = LogUtils.getLogger();

    public VVP() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        ModVehicleItems.register(modEventBus);
        ModSounds.REGISTRY.register(modEventBus);
        ModTabs.TABS.register(modEventBus);

        // Регистрируем наши методы настройки
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("HELLO FROM COMMON SETUP");
            LOGGER.info("DIRT BLOCK >> {}", net.minecraft.world.level.block.Blocks.DIRT);
        });
        // Регистрируем сетевой пакет
        com.atsuishio.superbwarfare.Mod.addNetworkMessage(S2CRadarSyncPacket.class, S2CRadarSyncPacket::buffer, S2CRadarSyncPacket::new, S2CRadarSyncPacket::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    // Этот метод будет вызван только на клиенте
    private void clientSetup(final FMLClientSetupEvent event) {
        // Регистрируем наш оверлей. Он будет рисоваться поверх всего.
        OverlayRegistry.registerOverlayTop("Radar", RadarHud.HUD_RADAR);
    }

    private void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem && event.getItemStack().hasTag()) {
            CompoundTag tag = BlockItem.getBlockEntityData(event.getItemStack());
            if (tag != null && tag.contains("EntityType")) {
                String entityType = tag.getString("EntityType");
                if (entityType.startsWith(MOD_ID + ":vdv_")) {
                    event.getToolTip().add(Component.translatable("tooltip.vvp.usage_restriction").withStyle(net.minecraft.ChatFormatting.RED));
                }
            }
        }
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}