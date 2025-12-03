package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
    public boolean banHand(LivingEntity entity) {
        return true;
    }
}
