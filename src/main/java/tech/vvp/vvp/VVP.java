package tech.vvp.vvp;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import java.util.Optional;

import org.slf4j.Logger;
import tech.vvp.vvp.config.server.VehicleConfigVVP;
import tech.vvp.vvp.config.server.ExplosionConfigVVP;
import tech.vvp.vvp.init.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.S2CRadarSyncPacket;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

@Mod(VVP.MOD_ID)
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

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, VehicleConfigVVP.SPEC, "vvp-vehicle.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ExplosionConfigVVP.SPEC, "vvp-explosion.toml");




        // Register the commonSetup method for modloading
        modEventBus.addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Some common setup code
            LOGGER.info("HELLO FROM COMMON SETUP");
            LOGGER.info("DIRT BLOCK >> {}", net.minecraft.world.level.block.Blocks.DIRT);
        });
        VVPNetwork.addNetworkMessage(S2CRadarSyncPacket.class, S2CRadarSyncPacket::buffer, S2CRadarSyncPacket::new, S2CRadarSyncPacket::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    private void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem && event.getItemStack().hasTag()) {
            CompoundTag tag = BlockItem.getBlockEntityData(event.getItemStack());
            if (tag != null && tag.contains("EntityType")) {
                String entityType = tag.getString("EntityType");
                if (entityType.startsWith(MOD_ID + ":vdv_")) {
                    // event.getToolTip().add(Component.translatable("tooltip.wmp.model_author"));
                    event.getToolTip().add(Component.translatable("tooltip.vvp.usage_restriction").withStyle(net.minecraft.ChatFormatting.RED));
                }
            }
        }
    }

    // Helper method to create ResourceLocation for this mod
    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
