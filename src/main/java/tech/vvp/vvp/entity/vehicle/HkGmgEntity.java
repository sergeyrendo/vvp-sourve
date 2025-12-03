package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HkGmgEntity extends GeoVehicleEntity {

    public HkGmgEntity(EntityType<HkGmgEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public @NotNull List<ItemStack> getRetrieveItems() {
        var list = new ArrayList<ItemStack>();
        list.add(new ItemStack(tech.vvp.vvp.init.ModItems.HK_GMG_ITEM.get()));
        return list;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand) {
        var gunData = getGunData(0);
        if (gunData == null) {
            return InteractionResult.SUCCESS;
        }

        // Let players mount normally when the gun is ready to fire.
        if (gunData.hasEnoughAmmoToShoot(player)) {
            return super.interact(player, hand);
        }

        var stack = player.getItemInHand(hand);
        if (!gunData.selectedAmmoConsumer().isAmmoItem(stack)) {
            return super.interact(player, hand);
        }

        if (!level().isClientSide) {
            modifyGunData(0, data -> data.reloadAmmo(player));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean banHand(LivingEntity entity) {
        return true;
    }
}
