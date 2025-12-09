package tech.vvp.vvp.item.varies;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.entity.vehicle.ICamoVehicle;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class SprayItem extends Item {

    public SprayItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("des.vvp.spray").withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("des.vvp.spray.usage").withStyle(ChatFormatting.DARK_GRAY));
    }

    // Вызывается при клике ПКМ по сущности
    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, net.minecraft.world.entity.LivingEntity target, InteractionHand hand) {
        // Работает с любой техникой реализующей ICamoVehicle
        if (target instanceof ICamoVehicle camoVehicle) {
            if (!player.level().isClientSide) {
                // Переключаем камуфляж
                camoVehicle.cycleCamo();
                
                // Получаем название нового камуфляжа
                String[] camoNames = camoVehicle.getCamoNames();
                int camoType = camoVehicle.getCamoType();
                String camoName = (camoType >= 0 && camoType < camoNames.length) 
                    ? camoNames[camoType] 
                    : "Unknown";
                
                // Отправляем сообщение игроку
                player.displayClientMessage(
                    Component.translatable("message.vvp.camo_changed", camoName).withStyle(ChatFormatting.GREEN),
                    true
                );
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}